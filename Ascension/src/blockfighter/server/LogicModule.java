package blockfighter.server;

import blockfighter.server.entities.mob.Mob;
import blockfighter.server.entities.player.Player;
import blockfighter.server.entities.proj.Projectile;
import blockfighter.server.net.PacketSender;
import blockfighter.shared.Globals;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;

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
    private long lastUpdateTime = 0, lastProcessQueue = 0, lastFinishCheckTime = 0, matchEndStartTime = 0, roomIdleStartTime = 0;
    private long lastSendPingTime = 0;
    private final long matchStartTime;
    private Player winningPlayer = null;

    public LogicModule(final byte roomIndex, final byte minLevel, final byte maxLevel) {
        this.room = new RoomData(roomIndex, minLevel, maxLevel);
        this.matchStartTime = System.nanoTime();
        reset();
    }

    public long getTime() {
        return this.currentTime;
    }

    public int getMatchTimeRemaining() {
        long remainingTime = (Integer) Globals.ServerConfig.MATCH_DURATION.getValue() - Globals.nsToMs((this.currentTime - this.matchStartTime));
        return (remainingTime > 0) ? (int) remainingTime : 0;
    }

    private void reset() {
        this.playAddQueue.clear();
        this.playDirKeydownQueue.clear();
        this.playUseSkillQueue.clear();
        this.projEffectQueue.clear();
        this.projAddQueue.clear();

        this.matchEndStartTime = 0;
        this.currentTime = System.nanoTime();
        this.roomIdleStartTime = System.nanoTime();
        this.room.reset();
    }

    private void closeRoom() {
        this.future.cancel(true);
        AscensionServer.removeRoom(this.room.getRoomIndex());
        Globals.log(LogicModule.class, "Closing room instance - Room: " + getRoomData().getRoomIndex(), Globals.LOG_TYPE_DATA);

    }

    public boolean gameFinished() {
        return (this.winningPlayer != null && this.winningPlayer.getScore() >= (Integer) Globals.ServerConfig.WIN_SCORE_COUNT.getValue())
                || this.currentTime - this.matchStartTime >= Globals.msToNs((Integer) Globals.ServerConfig.MATCH_DURATION.getValue());
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
                if (currentTime - this.roomIdleStartTime >= Globals.msToNs((Integer) Globals.ServerConfig.MAX_ROOM_IDLE.getValue())) {
                    closeRoom();
                }
                return;
            }

            if (currentTime - this.lastUpdateTime >= Globals.SERVER_LOGIC_UPDATE) {
                this.roomIdleStartTime = currentTime;
                updatePlayers();
                updateMobs();
                updateProjectiles();
                sendPlayerUpdates();
                this.lastUpdateTime = currentTime;
            }

            if (currentTime - this.lastSendPingTime >= Globals.msToNs(1000)) {
                sendScoreAndPing();
                this.lastSendPingTime = this.currentTime;
            }

            if (currentTime - this.lastFinishCheckTime >= Globals.msToNs(100)) {
                updateFinishingMatch();
                this.lastFinishCheckTime = currentTime;
            }

        } catch (final Exception ex) {
            Globals.logError(ex.toString(), ex);
        }
    }

    private void disconnectAllPlayers() {
        final ConcurrentHashMap<Byte, Player> players = this.room.getPlayers();
        players.entrySet().forEach((player) -> {
            player.getValue().disconnect();
        });
    }

    private void sendScoreAndPing() {
        final ConcurrentHashMap<Byte, Player> players = this.room.getPlayers();
        players.entrySet().forEach((player) -> {
            player.getValue().getConnection().updateReturnTripTime();
            player.getValue().updateClientScore();
        });
    }

    private void sendMatchEndRewards() {
        ArrayList<Player> sortedPlayers = new ArrayList<>(this.room.getPlayers().values());
        Collections.sort(sortedPlayers, (Player a, Player b) -> b.getScore() - a.getScore());
        ArrayDeque<Player> firsts = new ArrayDeque<>();
        ArrayDeque<Player> seconds = new ArrayDeque<>();
        ArrayDeque<Player> thirds = new ArrayDeque<>();
        ArrayDeque<Player> rest = new ArrayDeque<>();
        sortedPlayers.forEach((player) -> {
            if (player.getScore() > 0) {
                if (firsts.isEmpty() || firsts.getFirst().getScore() == player.getScore()) {
                    firsts.add(player);
                    Globals.log(LogicModule.class, player.getPlayerName() + " finished 1st", Globals.LOG_TYPE_DATA);
                } else if (seconds.isEmpty() || seconds.getFirst().getScore() == player.getScore()) {
                    seconds.add(player);
                    Globals.log(LogicModule.class, player.getPlayerName() + " finished 2nd", Globals.LOG_TYPE_DATA);
                } else if (thirds.isEmpty() || thirds.getFirst().getScore() == player.getScore()) {
                    thirds.add(player);
                    Globals.log(LogicModule.class, player.getPlayerName() + " finished 3rd", Globals.LOG_TYPE_DATA);
                }
            } else {
                rest.add(player);
                Globals.log(LogicModule.class, player.getPlayerName() + " did not place", Globals.LOG_TYPE_DATA);
            }
        });

        // 1st - 3 Items + 20% exp
        firsts.forEach((player) -> {
            for (int i = 0; i < 3; i++) {
                player.giveEquipDrop(player.getStats()[Globals.STAT_LEVEL], true);
            }
            player.giveEXP(player.getStats()[Globals.STAT_MAXEXP] * 0.2);
            player.sendMatchResult(Globals.VictoryStatus.FIRST);
        });

        // 2nd - 2 Items + 15% exp
        seconds.forEach((player) -> {
            for (int i = 0; i < 2; i++) {
                player.giveEquipDrop(player.getStats()[Globals.STAT_LEVEL], true);
            }
            player.giveEXP(player.getStats()[Globals.STAT_MAXEXP] * 0.15);
            player.sendMatchResult(Globals.VictoryStatus.SECOND);
        });

        //3rd - 1 Item + 10% exp
        thirds.forEach((player) -> {
            for (int i = 0; i < 1; i++) {
                player.giveEquipDrop(player.getStats()[Globals.STAT_LEVEL], true);
            }
            player.giveEXP(player.getStats()[Globals.STAT_MAXEXP] * 0.1);
            player.sendMatchResult(Globals.VictoryStatus.THIRD);
        });

        // Rest - 10% exp
        rest.forEach((player) -> {
            player.giveEXP(player.getStats()[Globals.STAT_MAXEXP] * 0.1);
            player.sendMatchResult(Globals.VictoryStatus.LAST);
        });
    }

    private void updateFinishingMatch() {
        if (this.matchEndStartTime == 0) {
            if (gameFinished()) {

                //Send victory/defeat notice
                //Send match rewards
                sendMatchEndRewards();

                Globals.log(LogicModule.class, "Room: " + this.room.getRoomIndex() + " Match finished. Disconnecting all players in 5 seconds.", Globals.LOG_TYPE_DATA);
                this.matchEndStartTime = currentTime;
            }
        } else if (currentTime - this.matchEndStartTime >= Globals.msToNs(10000)) {
            disconnectAllPlayers();
            closeRoom();
        }
    }

    private void updateMobs() {
        ArrayDeque<Future<Mob>> futures = new ArrayDeque<>(this.room.getMobs().size());
        this.room.getMobs().entrySet().forEach((mob) -> {
            futures.add(Core.SHARED_THREADPOOL.submit(mob.getValue()));
        });
        futures.forEach((task) -> {
            try {
                Mob mob = task.get();
                if (mob.isDead()) {
                    this.room.getMobs().remove(mob.getKey());
                    this.room.returnMobKey(mob.getKey());
                }
            } catch (final Exception ex) {
                Globals.logError(ex.toString(), ex);
            }
        });
    }

    private void sendPlayerUpdates() {
        final ConcurrentHashMap<Byte, Player> players = this.room.getPlayers();

        final ArrayDeque<byte[]> posDatas = new ArrayDeque<>(this.room.getPlayers().size());
        final ArrayDeque<byte[]> stateDatas = new ArrayDeque<>(this.room.getPlayers().size());

        players.entrySet().forEach((playerEntry) -> {
            try {
                Player player = playerEntry.getValue();
                if (player.isUpdatePos() && !player.isUpdateAnimState()) {
                    byte[] posData = player.getPosData();
                    posDatas.add(posData);
                } else if (player.isUpdateAnimState()) {
                    byte[] stateData = player.getStateData();
                    stateDatas.add(stateData);
                }
            } catch (Exception ex) {
                Globals.logError(ex.toString(), ex);
            }
        });

        if (posDatas.size() > 0) {
            Core.SHARED_THREADPOOL.execute(() -> {
                final int maxPosCount = 50;
                while (!posDatas.isEmpty()) {
                    int packetSize = (posDatas.size() >= maxPosCount) ? maxPosCount : posDatas.size();
                    byte[] bytes = new byte[Globals.PACKET_BYTE * 1
                            + (Globals.PACKET_BYTE * 2 + Globals.PACKET_INT * 2) * packetSize];
                    Arrays.fill(bytes, (byte) -1);

                    bytes[0] = Globals.DATA_PLAYER_SET_POS;
                    int bytePos = 1;

                    for (int i = 0; i < maxPosCount && !posDatas.isEmpty(); i++) {
                        byte[] posData = posDatas.poll();
                        System.arraycopy(posData, 0, bytes, bytePos, posData.length);
                        bytePos += posData.length;
                    }
                    PacketSender.sendAll(bytes, this);
                }
            });
        }

        if (stateDatas.size() > 0) {
            Core.SHARED_THREADPOOL.execute(() -> {
                final int maxPosCount = 50;
                while (!stateDatas.isEmpty()) {
                    int packetSize = (stateDatas.size() >= maxPosCount) ? maxPosCount : stateDatas.size();
                    byte[] bytes = new byte[Globals.PACKET_BYTE * 1
                            + (Globals.PACKET_BYTE * 3 + Globals.PACKET_INT * 2) * packetSize];
                    Arrays.fill(bytes, (byte) -1);

                    bytes[0] = Globals.DATA_PLAYER_SET_STATE;
                    int bytePos = 1;

                    for (int i = 0; i < maxPosCount && !stateDatas.isEmpty(); i++) {
                        byte[] stateData = stateDatas.poll();
                        System.arraycopy(stateData, 0, bytes, bytePos, stateData.length);
                        bytePos += stateData.length;
                    }
                    PacketSender.sendAll(bytes, this);
                }
            });
        }
    }

    private void updatePlayers() {
        final ConcurrentHashMap<Byte, Player> players = this.room.getPlayers();
        ArrayDeque<Future<Player>> futures = new ArrayDeque<>(players.size());

        this.room.clearPlayerBuckets();
        players.entrySet().forEach((player) -> {
            this.room.putPlayerIntoBuckets(player.getValue());
        });
        players.entrySet().forEach((player) -> {
            futures.add(Core.SHARED_THREADPOOL.submit(player.getValue()));
        });
        futures.forEach((task) -> {
            try {
                Player player = task.get();
                if (!player.isConnected()) {
                    this.room.getPlayers().remove(player.getKey());
                    this.room.returnPlayerKey(player.getKey());
                } else {
                    if (this.winningPlayer == null || this.winningPlayer.getScore() < player.getScore()) {
                        this.winningPlayer = player;
                    }
                }
            } catch (Exception ex) {
                Globals.logError(ex.toString(), ex);
            }
        });
    }

    private void updateProjectiles() {
        final ConcurrentHashMap<Integer, Projectile> projectiles = this.room.getProj();

        ArrayDeque<Future<Projectile>> futures = new ArrayDeque<>(projectiles.size());
        projectiles.entrySet().forEach((p) -> {
            futures.add(Core.SHARED_THREADPOOL.submit(p.getValue()));
        });
        futures.forEach((task) -> {
            try {
                Projectile projectile = task.get();
                if (projectile.isExpired()) {
                    this.room.getProj().remove(projectile.getKey());
                    this.room.returnProjKey(projectile.getKey());
                }
            } catch (final Exception ex) {
                Globals.logError(ex.toString(), ex);
            }
        });
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

        Core.SHARED_THREADPOOL.execute(() -> {
            try {
                while (!this.playAddQueue.isEmpty()) {
                    final Player newPlayer = this.playAddQueue.poll();
                    if (newPlayer != null) {
                        this.room.addPlayer(newPlayer);
                    }
                }
            } catch (final Exception ex) {
                Globals.logError(ex.toString(), ex);
            }
        });

        Core.SHARED_THREADPOOL.execute(() -> {
            try {
                while (!this.mobAddQueue.isEmpty()) {
                    final Mob p = this.mobAddQueue.poll();
                    if (p != null) {
                        mobs.put(p.getKey(), p);
                    }
                }
            } catch (final Exception ex) {
                Globals.logError(ex.toString(), ex);
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
            Core.SHARED_THREADPOOL.execute(() -> {
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
                    Globals.logError(ex.toString(), ex);
                }
            });

            Core.SHARED_THREADPOOL.execute(() -> {
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
                    Globals.logError(ex.toString(), ex);
                }
            });

            Core.SHARED_THREADPOOL.execute(() -> {
                try {
                    while (!this.projEffectQueue.isEmpty()) {
                        final Projectile proj = this.projEffectQueue.poll();
                        if (proj != null) {
                            proj.applyEffect();
                        }
                    }
                } catch (final Exception ex) {
                    Globals.logError(ex.toString(), ex);
                }
            });

            Core.SHARED_THREADPOOL.execute(() -> {
                try {
                    while (!this.projAddQueue.isEmpty()) {
                        final Projectile p = this.projAddQueue.poll();
                        if (p != null) {
                            projectiles.put(p.getKey(), p);
                        }
                    }
                } catch (final Exception ex) {
                    Globals.logError(ex.toString(), ex);
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
