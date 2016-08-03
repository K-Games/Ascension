package blockfighter.server;

import blockfighter.server.entities.mob.Mob;
import blockfighter.server.entities.player.Player;
import blockfighter.server.entities.proj.Projectile;
import blockfighter.server.maps.GameMap;
import blockfighter.server.maps.GameMapArena;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

public class LogicModule extends Thread {

    private long currentTime = 0;
    private byte room = -1;

    private ConcurrentHashMap<Byte, Player> players = new ConcurrentHashMap<>(Globals.SERVER_MAX_PLAYERS, 0.9f,
            Math.max(Globals.SERVER_MAX_PLAYERS / 5, 3));
    private ConcurrentHashMap<Byte, Mob> mobs = new ConcurrentHashMap<>(1, 0.9f, 1);
    private ConcurrentHashMap<Integer, Projectile> projectiles = new ConcurrentHashMap<>(500, 0.75f, 3);

    private GameMap map;
    private int projMaxKeys = 500;
    private int mobMaxKeys = 255;
    private int minLevel = 0, maxLevel = 0;

    private ConcurrentLinkedQueue<Byte> playerKeys = new ConcurrentLinkedQueue<>();
    private ConcurrentLinkedQueue<Integer> projKeys = new ConcurrentLinkedQueue<>();
    private ConcurrentLinkedQueue<Byte> mobKeys = new ConcurrentLinkedQueue<>();

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

    public LogicModule(final byte r) {
        this.room = r;
        reset();
    }

    public LogicModule(ConcurrentLinkedQueue<Byte> playerKeys, ConcurrentLinkedQueue<Byte> mobKeys) {
        this.playerKeys = playerKeys;
        this.mobKeys = mobKeys;
    }

    public LogicModule(ConcurrentLinkedQueue<Integer> projKeys) {
        this.projKeys = projKeys;
        this.projMaxKeys = this.projKeys.size();
    }

    public LogicModule(final int minlvl, final int maxlvl) {
        this.minLevel = minlvl;
        this.maxLevel = maxlvl;
    }

    public LogicModule(ConcurrentHashMap<Byte, Player> players) {
        this.players = players;
    }

    public long getTime() {
        return this.currentTime;
    }

    public boolean isFull() {
        return this.playerKeys.isEmpty();
    }

    public boolean isInLevelRange(int level) {
        return level >= this.minLevel && level <= this.maxLevel;
    }

    private void resetKeys() {
        for (int i = 0; i < 500; i++) {
            this.projKeys.add(i);
        }
        Byte[] keys = new Byte[mobMaxKeys];
        for (int i = 0; i < this.mobMaxKeys; i++) {
            keys[i] = (byte) i;
        }
        mobKeys.addAll(Arrays.asList(keys));
        for (byte i = 0; i < Globals.SERVER_MAX_PLAYERS; i++) {
            this.playerKeys.add(i);
        }
    }

    private void reset() {
        this.players.clear();
        this.mobs.clear();
        this.projectiles.clear();
        this.projKeys.clear();
        this.playerKeys.clear();
        this.mobKeys.clear();

        this.playAddQueue.clear();
        this.playDirKeydownQueue.clear();
        this.playUseSkillQueue.clear();
        this.projEffectQueue.clear();
        this.projAddQueue.clear();

        this.projMaxKeys = 500;
        this.resetStartTime = 0;
        //if (this.room == 0) {
        this.setMap(new GameMapArena());
        this.setMinLevel(this.room * 10 + 1);
        this.setMaxLevel((this.room + 1) * 10);
        //} else {
        //    this.setMap(new GameMapFloor1());
        //}
        resetKeys();
        this.map.spawnMapMobs(this);
    }

    @Override
    public void run() {
        try {
            currentTime = System.nanoTime();
            if (currentTime - this.lastProcessQueue >= Globals.PROCESS_QUEUE) {
                processQueues();
                this.lastProcessQueue = currentTime;
            }
            if (this.players.isEmpty()) {
                return;
            }
            final long nowMs = System.currentTimeMillis();

            if (currentTime - this.lastUpdateTime >= Globals.LOGIC_UPDATE) {
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
            if (!this.getMap().isPvP() && currentTime - this.lastResetCheckTime >= Globals.msToNs(1000)) {
                if (this.resetStartTime == 0) {
                    if (this.mobs.isEmpty()) {
                        this.resetStartTime = currentTime;
                    }
                } else if (currentTime - this.resetStartTime >= Globals.msToNs(5000)) {
                    reset();
                }
                this.lastResetCheckTime = currentTime;
            }

        } catch (final Exception ex) {
            Globals.logError(ex.getLocalizedMessage(), ex, true);
        }
    }

    private void updateMobs() {
        for (final Map.Entry<Byte, Mob> mob : this.mobs.entrySet()) {
            LOGIC_THREAD_POOL.execute(mob.getValue());
        }

        Iterator<Entry<Byte, Mob>> mobsIter = this.mobs.entrySet().iterator();
        while (mobsIter.hasNext()) {
            Entry<Byte, Mob> mob = mobsIter.next();
            try {
                mob.getValue().join();
                if (mob.getValue().isDead()) {
                    mobsIter.remove();
                    returnMobKey(mob.getKey());
                }
            } catch (final InterruptedException ex) {
                Globals.logError(ex.getLocalizedMessage(), ex, true);
            }
        }
    }

    private void updatePlayers() {
        for (final Map.Entry<Byte, Player> player : this.players.entrySet()) {
            LOGIC_THREAD_POOL.execute(player.getValue());
        }
        Iterator<Entry<Byte, Player>> playersIter = this.players.entrySet().iterator();
        while (playersIter.hasNext()) {
            Entry<Byte, Player> player = playersIter.next();
            try {
                player.getValue().join();
                if (!(player.getValue().isConnected())) {
                    playersIter.remove();
                    this.playerKeys.add(player.getKey());
                    Globals.log(LogicModule.class, "Room: " + this.room + " Returned player key: " + player.getKey() + " Keys Remaining: " + this.playerKeys.size(), Globals.LOG_TYPE_DATA, true);
                }
            } catch (final InterruptedException ex) {
                Globals.logError(ex.getLocalizedMessage(), ex, true);
            }
        }
    }

    private void updateProjectiles() {
        for (final Map.Entry<Integer, Projectile> p : this.projectiles.entrySet()) {
            LOGIC_THREAD_POOL.execute(p.getValue());
        }

        Iterator<Entry<Integer, Projectile>> projectilesIter = this.projectiles.entrySet().iterator();
        while (projectilesIter.hasNext()) {
            Entry<Integer, Projectile> projectile = projectilesIter.next();
            try {
                projectile.getValue().join();
                if (projectile.getValue().isExpired()) {
                    projectilesIter.remove();
                    returnProjKey(projectile.getValue().getKey());
                }
            } catch (final InterruptedException ex) {
                Globals.logError(ex.getLocalizedMessage(), ex, true);
            }
        }
    }

    public ConcurrentHashMap<Byte, Player> getPlayers() {
        return this.players;
    }

    public ConcurrentHashMap<Byte, Mob> getMobs() {
        return this.mobs;
    }

    public ConcurrentHashMap<Integer, Projectile> getProj() {
        return this.projectiles;
    }

    public GameMap getMap() {
        return this.map;
    }

    public byte getRoom() {
        return this.room;
    }

    public byte getNextPlayerKey() {
        if (this.playerKeys.isEmpty()) {
            return -1;
        }
        return this.playerKeys.poll();
    }

    public byte getNextMobKey() {
        if (this.mobKeys.isEmpty()) {
            return -1;
        }
        return this.mobKeys.poll();
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

        while (!this.playAddQueue.isEmpty()) {
            final Player newPlayer = this.playAddQueue.poll();
            if (newPlayer != null) {
                final byte key = newPlayer.getKey();
                this.players.put(key, newPlayer);
            }
        }

        queues[0] = () -> {
            try {
                while (!this.playDirKeydownQueue.isEmpty()) {
                    final byte[] data = this.playDirKeydownQueue.poll();
                    if (data != null) {
                        final byte key = data[2], dir = data[3], value = data[4];
                        if (this.players.containsKey(key)) {
                            this.players.get(key).setDirKeydown(dir, value == 1);
                        }
                    }
                }
            } catch (final Exception ex) {
                Globals.logError(ex.getLocalizedMessage(), ex, true);
            }
        };

        queues[1] = () -> {
            try {
                while (!this.playUseSkillQueue.isEmpty()) {
                    final byte[] data = this.playUseSkillQueue.poll();
                    if (data != null) {
                        final byte key = data[2];
                        if (this.players.containsKey(key)) {
                            this.players.get(key).queueSkillUse(data);
                        }
                    }
                }
            } catch (final Exception ex) {
                Globals.logError(ex.getLocalizedMessage(), ex, true);
            }
        };

        queues[2] = () -> {
            try {
                while (!this.projEffectQueue.isEmpty()) {
                    final Projectile proj = this.projEffectQueue.poll();
                    if (proj != null) {
                        proj.processQueue();
                    }
                }
            } catch (final Exception ex) {
                Globals.logError(ex.getLocalizedMessage(), ex, true);
            }
        };

        queues[3] = () -> {
            try {
                while (!this.projAddQueue.isEmpty()) {
                    final Projectile p = this.projAddQueue.poll();
                    if (p != null) {
                        this.projectiles.put(p.getKey(), p);
                    }
                }
            } catch (final Exception ex) {
                Globals.logError(ex.getLocalizedMessage(), ex, true);
            }
        };

        queues[4] = () -> {
            try {
                while (!this.mobAddQueue.isEmpty()) {
                    final Mob p = this.mobAddQueue.poll();
                    if (p != null) {
                        this.mobs.put(p.getKey(), p);
                    }
                }
            } catch (final Exception ex) {
                Globals.logError(ex.getLocalizedMessage(), ex, true);
            }
        };

        if (this.players.isEmpty()) {
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

    public int getNextProjKey() {
        Integer nextKey = this.projKeys.poll();
        while (nextKey == null) {
            this.projKeys.add(this.projMaxKeys);
            this.projMaxKeys++;
            nextKey = this.projKeys.poll();
        }
        return nextKey;
    }

    public void returnProjKey(final int key) {
        this.projKeys.add(key);
    }

    public void returnMobKey(final byte key) {
        this.mobKeys.add(key);
    }

    public boolean containsPlayerID(final UUID id) {
        for (final Map.Entry<Byte, Player> player : this.players.entrySet()) {
            if (player.getValue().getUniqueID().equals(id)) {
                return true;
            }
        }
        return false;
    }

    public byte getPlayerKey(final UUID id) {
        for (final Map.Entry<Byte, Player> player : this.players.entrySet()) {
            if (player.getValue().getUniqueID().equals(id)) {
                return player.getKey();
            }
        }
        return -1;
    }

    public ArrayList<Player> getPlayersInRange(final Player player, final double radius) {
        ArrayList<Player> playersInRange = new ArrayList<>(Globals.SERVER_MAX_PLAYERS);
        for (final Map.Entry<Byte, Player> pEntry : getPlayers().entrySet()) {
            final Player p = pEntry.getValue();
            if (p != player && !p.isDead() && !p.isInvulnerable()) {
                double distance = Math.sqrt(Math.pow((player.getX() - p.getX()), 2) + Math.pow((player.getY() - p.getY()), 2));
                if (distance <= radius) {
                    playersInRange.add(p);
                }
            }
        }
        return playersInRange;
    }

    public ArrayList<Mob> getMobsInRange(final Player player, final double radius) {
        ArrayList<Mob> mobInRange = new ArrayList<>(getMobs().size());
        for (final Map.Entry<Byte, Mob> bEntry : getMobs().entrySet()) {
            final Mob b = bEntry.getValue();
            double distance = Math.sqrt(Math.pow((player.getX() - b.getX()), 2) + Math.pow((player.getY() - b.getY()), 2));
            if (distance <= 100) {
                mobInRange.add(b);
            }
        }
        return mobInRange;
    }

    public void setPlayerKeys(ConcurrentLinkedQueue<Byte> playerKeys) {
        this.playerKeys = playerKeys;
    }

    public void setMobKeys(ConcurrentLinkedQueue<Byte> mobKeys) {
        this.mobKeys = mobKeys;
    }

    public void setProjKeys(ConcurrentLinkedQueue<Integer> projKeys) {
        this.projKeys = projKeys;
        this.projMaxKeys = this.projKeys.size();
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

    public void setPlayers(ConcurrentHashMap<Byte, Player> players) {
        this.players = players;
    }

    public void setMobs(ConcurrentHashMap<Byte, Mob> mobs) {
        this.mobs = mobs;
    }

    public void setProjectiles(ConcurrentHashMap<Integer, Projectile> projectiles) {
        this.projectiles = projectiles;
    }

    public void setMap(GameMap map) {
        this.map = map;
    }

    public void setMinLevel(int minLevel) {
        this.minLevel = minLevel;
    }

    public void setMaxLevel(int maxLevel) {
        this.maxLevel = maxLevel;
    }
}
