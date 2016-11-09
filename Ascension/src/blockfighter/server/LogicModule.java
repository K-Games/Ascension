package blockfighter.server;

import blockfighter.server.entities.mob.Mob;
import blockfighter.server.entities.player.Player;
import blockfighter.server.entities.proj.Projectile;
import blockfighter.shared.Globals;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

public class LogicModule extends Thread {

    private long currentTime = 0;
    private final Room room;

    private ConcurrentLinkedQueue<Player> playAddQueue = new ConcurrentLinkedQueue<>();
    private ConcurrentLinkedQueue<byte[]> playDirKeydownQueue = new ConcurrentLinkedQueue<>();
    private ConcurrentLinkedQueue<byte[]> playUseSkillQueue = new ConcurrentLinkedQueue<>();
    private ConcurrentLinkedQueue<Projectile> projEffectQueue = new ConcurrentLinkedQueue<>();
    private ConcurrentLinkedQueue<Projectile> projAddQueue = new ConcurrentLinkedQueue<>();
    private ConcurrentLinkedQueue<Mob> mobAddQueue = new ConcurrentLinkedQueue<>();

    private long lastRefreshAll = 0;
    private long lastUpdateTime = 0, lastProcessQueue = 0, lastResetCheckTime = 0, resetStartTime = 0;

    private static final ExecutorService LOGIC_THREAD_POOL = Executors.newFixedThreadPool(Globals.SERVER_LOGIC_THREADS,
            new BasicThreadFactory.Builder()
            .namingPattern("LogicModule-%d")
            .daemon(true)
            .priority(Thread.NORM_PRIORITY)
            .build());

    public LogicModule(final byte r, final byte roomIndex) {
        this.room = new Room(r, roomIndex);
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
                return;
            }
            final long nowMs = System.currentTimeMillis();

            if (currentTime - this.lastUpdateTime >= Globals.SERVER_LOGIC_UPDATE) {
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
            Globals.logError(ex.getStackTrace()[0].toString(), ex, true);
        }
    }

    private void updateMobs() {
        for (final Map.Entry<Integer, Mob> mob : this.room.getMobs().entrySet()) {
            LOGIC_THREAD_POOL.execute(mob.getValue());
        }

        Iterator<Entry<Integer, Mob>> mobsIter = this.room.getMobs().entrySet().iterator();
        while (mobsIter.hasNext()) {
            Entry<Integer, Mob> mob = mobsIter.next();
            try {
                mob.getValue().join();
                if (mob.getValue().isDead()) {
                    mobsIter.remove();
                    this.room.returnMobKey(mob.getKey());
                }
            } catch (final InterruptedException ex) {
                Globals.logError(ex.getStackTrace()[0].toString(), ex, true);
            }
        }
    }

    private void updatePlayers() {
        final ConcurrentHashMap<Byte, Player> players = this.room.getPlayers();

        for (final Map.Entry<Byte, Player> player : players.entrySet()) {
            LOGIC_THREAD_POOL.execute(player.getValue());
        }

        this.room.clearPlayerBuckets();
        Iterator<Entry<Byte, Player>> playersIter = players.entrySet().iterator();
        while (playersIter.hasNext()) {
            Entry<Byte, Player> player = playersIter.next();
            try {
                player.getValue().join();
                this.room.putPlayerIntoBuckets(player.getValue());
                if (!(player.getValue().isConnected())) {
                    playersIter.remove();
                    this.room.returnPlayerKey(player.getKey());
                }
            } catch (final InterruptedException ex) {
                Globals.logError(ex.getStackTrace()[0].toString(), ex, true);
            }
        }
    }

    private void updateProjectiles() {
        final ConcurrentHashMap<Integer, Projectile> projectiles = this.room.getProj();
        for (final Map.Entry<Integer, Projectile> p : projectiles.entrySet()) {
            LOGIC_THREAD_POOL.execute(p.getValue());
        }

        Iterator<Entry<Integer, Projectile>> projectilesIter = projectiles.entrySet().iterator();
        while (projectilesIter.hasNext()) {
            Entry<Integer, Projectile> projectile = projectilesIter.next();
            try {
                projectile.getValue().join();
                if (projectile.getValue().isExpired()) {
                    projectilesIter.remove();
                    this.room.returnProjKey(projectile.getValue().getKey());
                }
            } catch (final InterruptedException ex) {
                Globals.logError(ex.getStackTrace()[0].toString(), ex, true);
            }
        }
    }

    public Room getRoom() {
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
        final Runnable[] queues = new Runnable[5];
        final ConcurrentHashMap<Byte, Player> players = this.room.getPlayers();
        final ConcurrentHashMap<Integer, Mob> mobs = this.room.getMobs();
        final ConcurrentHashMap<Integer, Projectile> projectiles = this.room.getProj();

        while (!this.playAddQueue.isEmpty()) {
            final Player newPlayer = this.playAddQueue.poll();
            if (newPlayer != null) {
                this.room.addPlayer(newPlayer);
            }
        }

        queues[0] = () -> {
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
                Globals.logError(ex.getStackTrace()[0].toString(), ex, true);
            }
        };

        queues[1] = () -> {
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
                Globals.logError(ex.getStackTrace()[0].toString(), ex, true);
            }
        };

        queues[2] = () -> {
            try {
                while (!this.projEffectQueue.isEmpty()) {
                    final Projectile proj = this.projEffectQueue.poll();
                    if (proj != null) {
                        proj.applyEffect();
                    }
                }
            } catch (final Exception ex) {
                Globals.logError(ex.getStackTrace()[0].toString(), ex, true);
            }
        };

        queues[3] = () -> {
            try {
                while (!this.projAddQueue.isEmpty()) {
                    final Projectile p = this.projAddQueue.poll();
                    if (p != null) {
                        projectiles.put(p.getKey(), p);
                    }
                }
            } catch (final Exception ex) {
                Globals.logError(ex.getStackTrace()[0].toString(), ex, true);
            }
        };

        queues[4] = () -> {
            try {
                while (!this.mobAddQueue.isEmpty()) {
                    final Mob p = this.mobAddQueue.poll();
                    if (p != null) {
                        mobs.put(p.getKey(), p);
                    }
                }
            } catch (final Exception ex) {
                Globals.logError(ex.getStackTrace()[0].toString(), ex, true);
            }
        };

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
            if (!this.mobAddQueue.isEmpty()) {
                LOGIC_THREAD_POOL.execute(queues[4]);
            }
            return;
        }

        for (final Runnable t : queues) {
            LOGIC_THREAD_POOL.execute(t);
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
}
