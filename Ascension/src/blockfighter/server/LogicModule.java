package blockfighter.server;

import blockfighter.server.entities.mob.Mob;
import blockfighter.server.entities.player.Player;
import blockfighter.server.entities.proj.Projectile;
import blockfighter.server.net.PacketSender;
import blockfighter.shared.Globals;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

public class LogicModule implements Runnable {

    private long currentTime = 0;
    private final RoomData room;
    private ScheduledFuture future;

    private ConcurrentLinkedQueue<Player> playAddQueue = new ConcurrentLinkedQueue<>();
    private ConcurrentLinkedQueue<byte[]> playDirKeydownQueue = new ConcurrentLinkedQueue<>();
    private ConcurrentLinkedQueue<byte[]> playUseSkillQueue = new ConcurrentLinkedQueue<>();
    private ConcurrentLinkedQueue<Projectile> projEffectQueue = new ConcurrentLinkedQueue<>();
    private ConcurrentLinkedQueue<Projectile> projAddQueue = new ConcurrentLinkedQueue<>();
    private ConcurrentLinkedQueue<Mob> mobAddQueue = new ConcurrentLinkedQueue<>();

    private long lastRefreshAll = 0;
    private long lastUpdateTime = 0, lastProcessQueue = 0, lastResetCheckTime = 0, resetStartTime = 0, roomIdleStartTime = 0;

    private static final ExecutorService LOGIC_THREAD_POOL = Executors.newFixedThreadPool(Globals.SERVER_LOGIC_THREADS,
            new BasicThreadFactory.Builder()
                    .namingPattern("Logic-Processor-%d")
                    .daemon(true)
                    .priority(Thread.NORM_PRIORITY)
                    .build());

    public LogicModule(final byte roomIndex, final byte minLevel, final byte maxLevel) {
        this.room = new RoomData(roomIndex, minLevel, maxLevel);
        reset();
    }

    public long getTime() {
        return this.currentTime;
    }

    private void reset() {
        this.playAddQueue.clear();
        this.playDirKeydownQueue.clear();
        this.playUseSkillQueue.clear();
        this.projEffectQueue.clear();
        this.projAddQueue.clear();

        this.resetStartTime = 0;
        this.currentTime = System.nanoTime();
        this.roomIdleStartTime = System.nanoTime();
        this.room.reset();
    }

    @Override
    public void run() {
        try {
            currentTime = System.nanoTime();
            if (currentTime - this.lastProcessQueue >= Globals.PROCESS_QUEUE) {
                processQueues();
                this.lastProcessQueue = currentTime;
            }
            if (this.room.getPlayers().isEmpty()) {
                if (currentTime - this.roomIdleStartTime >= Globals.msToNs(Globals.SERVER_ROOM_MAX_ILDE)) {
                    this.future.cancel(true);
                    AscensionServer.removeRoom(this.room.getRoomIndex());
                    Globals.log(AscensionServer.class, "Room instance removed - Room: " + getRoomData().getRoomIndex(), Globals.LOG_TYPE_DATA, true);
                }
                return;
            }
            final long nowMs = System.currentTimeMillis();

            if (currentTime - this.lastUpdateTime >= Globals.SERVER_LOGIC_UPDATE) {
                this.roomIdleStartTime = currentTime;
                updatePlayers();
                updateMobs();
                updateProjectiles();
                this.lastUpdateTime = currentTime;
            }

//            if (currentTime - this.lastRefreshAll >= Globals.SENDALL_UPDATE) {
//                for (final Map.Entry<Byte, Player> pEntry : getPlayers().entrySet()) {
//                    final Player player = pEntry.getValue();
//                    player.sendData();
//                }
//                this.lastRefreshAll = currentTime;
//            }
            if (!this.room.getMap().isPvP() && currentTime - this.lastResetCheckTime >= Globals.msToNs(1000)) {
                if (this.resetStartTime == 0) {
                    if (this.room.getMobs().isEmpty()) {
                        this.resetStartTime = currentTime;
                    }
                } else if (currentTime - this.resetStartTime >= Globals.msToNs(5000)) {
                    reset();
                }
                this.lastResetCheckTime = currentTime;
            }

        } catch (final Exception ex) {
            Globals.logError(ex.toString(), ex, true);
        }
    }

    private void updateMobs() {
        LinkedList<Future<Mob>> futures = new LinkedList<>();
        for (final Map.Entry<Integer, Mob> mob : this.room.getMobs().entrySet()) {
            futures.add(LOGIC_THREAD_POOL.submit(mob.getValue()));
        }

        for (Future<Mob> task : futures) {
            try {
                Mob mob = task.get();
                if (mob.isDead()) {
                    this.room.getMobs().remove(mob.getKey());
                    this.room.returnMobKey(mob.getKey());
                }
            } catch (final Exception ex) {
                Globals.logError(ex.toString(), ex, true);
            }
        }
    }

    private void updatePlayers() {
        final ConcurrentHashMap<Byte, Player> players = this.room.getPlayers();
        LinkedList<Future<Player>> futures = new LinkedList<>();

        final LinkedList<byte[]> posDatas = new LinkedList<>();

        this.room.clearPlayerBuckets();
        for (final Map.Entry<Byte, Player> player : players.entrySet()) {
            this.room.putPlayerIntoBuckets(player.getValue());
        }

        for (final Map.Entry<Byte, Player> player : players.entrySet()) {
            futures.add(LOGIC_THREAD_POOL.submit(player.getValue()));
        }

        for (Future<Player> task : futures) {
            try {
                Player player = task.get();
                if (player.isUpdatePos()) {
                    byte[] posData = player.getPosData();
                    posDatas.add(posData);
                }

                if (!(player.isConnected())) {
                    this.room.getPlayers().remove(player.getKey());
                    this.room.returnPlayerKey(player.getKey());
                }
            } catch (Exception ex) {
                Globals.logError(ex.toString(), ex, true);
            }
        }

        if (posDatas.size() > 0) {
            LOGIC_THREAD_POOL.execute(() -> {
                byte[] bytes = new byte[Globals.PACKET_BYTE * 1
                        + (Globals.PACKET_BYTE * 2 + Globals.PACKET_INT * 2) * posDatas.size()];
                Arrays.fill(bytes, (byte) -1);

                bytes[0] = Globals.DATA_PLAYER_SET_POS;
                int bytePos = 1;

                while (!posDatas.isEmpty()) {
                    byte[] posData = posDatas.poll();
                    System.arraycopy(posData, 0, bytes, bytePos, posData.length);
                    bytePos += posData.length;
                }
                PacketSender.sendAll(bytes, this);
            });
        }
    }

    private void updateProjectiles() {
        final ConcurrentHashMap<Integer, Projectile> projectiles = this.room.getProj();

        LinkedList<Future<Projectile>> futures = new LinkedList<>();
        for (final Map.Entry<Integer, Projectile> p : projectiles.entrySet()) {
            futures.add(LOGIC_THREAD_POOL.submit(p.getValue()));
        }

        for (Future<Projectile> task : futures) {
            try {
                Projectile projectile = task.get();
                if (projectile.isExpired()) {
                    this.room.getProj().remove(projectile.getKey());
                    this.room.returnProjKey(projectile.getKey());
                }
            } catch (final Exception ex) {
                Globals.logError(ex.toString(), ex, true);
            }
        }
    }

    public RoomData getRoomData() {
        return this.room;
    }

    public void queueAddPlayer(final Player newPlayer) {
        this.playAddQueue.add(newPlayer);
    }

    public void queuePlayerDirKeydown(final byte[] data) {
        this.playDirKeydownQueue.add(data);
    }

    public void queuePlayerUseSkill(final byte[] data) {
        this.playUseSkillQueue.add(data);
    }

    public void queueAddProj(final Projectile projectile) {
        this.projAddQueue.add(projectile);
    }

    public void queueAddMob(final Mob mob) {
        if (mob.getKey() != -1) {
            this.mobAddQueue.add(mob);
        }
    }

    public void queueProjEffect(final Projectile p) {
        this.projEffectQueue.add(p);
    }

    private void processQueues() {
        final ConcurrentHashMap<Byte, Player> players = this.room.getPlayers();
        final ConcurrentHashMap<Integer, Mob> mobs = this.room.getMobs();
        final ConcurrentHashMap<Integer, Projectile> projectiles = this.room.getProj();

        LOGIC_THREAD_POOL.execute(() -> {
            try {
                while (!this.playAddQueue.isEmpty()) {
                    final Player newPlayer = this.playAddQueue.poll();
                    if (newPlayer != null) {
                        this.room.addPlayer(newPlayer);
                    }
                }
            } catch (final Exception ex) {
                Globals.logError(ex.toString(), ex, true);
            }
        });

        LOGIC_THREAD_POOL.execute(() -> {
            try {
                while (!this.mobAddQueue.isEmpty()) {
                    final Mob p = this.mobAddQueue.poll();
                    if (p != null) {
                        mobs.put(p.getKey(), p);
                    }
                }
            } catch (final Exception ex) {
                Globals.logError(ex.toString(), ex, true);
            }
        });

        if (players.isEmpty()) {
            if (!this.playDirKeydownQueue.isEmpty()) {
                this.playDirKeydownQueue.clear();
            }
            if (!this.playUseSkillQueue.isEmpty()) {
                this.playUseSkillQueue.clear();
            }
            if (!this.projEffectQueue.isEmpty()) {
                this.projEffectQueue.clear();
            }
        } else {
            LOGIC_THREAD_POOL.execute(() -> {
                try {
                    while (!this.playDirKeydownQueue.isEmpty()) {
                        final byte[] data = this.playDirKeydownQueue.poll();
                        if (data != null) {
                            final byte key = data[2], dir = data[3], value = data[4];
                            if (players.containsKey(key)) {
                                players.get(key).setDirKeydown(dir, value == 1);
                            }
                        }
                    }
                } catch (final Exception ex) {
                    Globals.logError(ex.toString(), ex, true);
                }
            });

            LOGIC_THREAD_POOL.execute(() -> {
                try {
                    while (!this.playUseSkillQueue.isEmpty()) {
                        final byte[] data = this.playUseSkillQueue.poll();
                        if (data != null) {
                            final byte key = data[2];
                            if (players.containsKey(key)) {
                                players.get(key).queueSkillUse(data);
                            }
                        }
                    }
                } catch (final Exception ex) {
                    Globals.logError(ex.toString(), ex, true);
                }
            });

            LOGIC_THREAD_POOL.execute(() -> {
                try {
                    while (!this.projEffectQueue.isEmpty()) {
                        final Projectile proj = this.projEffectQueue.poll();
                        if (proj != null) {
                            proj.applyEffect();
                        }
                    }
                } catch (final Exception ex) {
                    Globals.logError(ex.toString(), ex, true);
                }
            });

            LOGIC_THREAD_POOL.execute(() -> {
                try {
                    while (!this.projAddQueue.isEmpty()) {
                        final Projectile p = this.projAddQueue.poll();
                        if (p != null) {
                            projectiles.put(p.getKey(), p);
                        }
                    }
                } catch (final Exception ex) {
                    Globals.logError(ex.toString(), ex, true);
                }
            });
        }

    }

    public void setPlayAddQueue(ConcurrentLinkedQueue<Player> playAddQueue) {
        this.playAddQueue = playAddQueue;
    }

    public void setPlayDirKeydownQueue(ConcurrentLinkedQueue<byte[]> playDirKeydownQueue) {
        this.playDirKeydownQueue = playDirKeydownQueue;
    }

    public void setPlayUseSkillQueue(ConcurrentLinkedQueue<byte[]> playUseSkillQueue) {
        this.playUseSkillQueue = playUseSkillQueue;
    }

    public void setProjEffectQueue(ConcurrentLinkedQueue<Projectile> projEffectQueue) {
        this.projEffectQueue = projEffectQueue;
    }

    public void setProjAddQueue(ConcurrentLinkedQueue<Projectile> projAddQueue) {
        this.projAddQueue = projAddQueue;
    }

    public void setMobAddQueue(ConcurrentLinkedQueue<Mob> mobAddQueue) {
        this.mobAddQueue = mobAddQueue;
    }

    public void setFuture(final ScheduledFuture f) {
        this.future = f;
    }
}
