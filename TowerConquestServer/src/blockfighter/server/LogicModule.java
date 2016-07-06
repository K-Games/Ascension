package blockfighter.server;

import blockfighter.server.entities.mob.Mob;
import blockfighter.server.entities.player.Player;
import blockfighter.server.entities.proj.Projectile;
import blockfighter.server.maps.GameMap;
import blockfighter.server.maps.GameMapArena;
import blockfighter.server.maps.GameMapFloor1;
import blockfighter.server.net.PacketSender;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

/**
 * Logic module of the server. Updates all objects and their interactions.
 *
 * @author Ken Kwan
 */
public class LogicModule extends Thread {

    private long currentTime = 0;
    private static PacketSender sender;
    private byte room = -1;

    private final ConcurrentHashMap<Byte, Player> players = new ConcurrentHashMap<>(Globals.SERVER_MAX_PLAYERS, 0.9f,
            Math.max(Globals.SERVER_MAX_PLAYERS / 5, 3));
    private final ConcurrentHashMap<Byte, Mob> mobs = new ConcurrentHashMap<>(1, 0.9f, 1);
    private final ConcurrentHashMap<Integer, Projectile> projectiles = new ConcurrentHashMap<>(500, 0.75f, 3);

    private GameMap map;
    private int projMaxKeys = 500;
    private int mobMaxKeys = 255;

    private final ConcurrentLinkedQueue<Byte> playerKeys = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<Integer> projKeys = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<Byte> mobKeys = new ConcurrentLinkedQueue<>();

    private final ConcurrentLinkedQueue<Player> pAddQueue = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<byte[]> pDirKeydownQueue = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<byte[]> pUseSkillQueue = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<Projectile> projEffectQueue = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<Projectile> projAddQueue = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<Mob> mobAddQueue = new ConcurrentLinkedQueue<>();

    private long lastRefreshAll = 0;
    private long lastUpdateTime = 0, lastProcessQueue = 0, lastResetCheckTime = 0, resetStartTime = 0;

    private static final ExecutorService LOGIC_THREAD_POOL = Executors.newFixedThreadPool(Globals.SERVER_LOGIC_THREADS,
            new BasicThreadFactory.Builder()
            .namingPattern("LogicModule-%d")
            .daemon(true)
            .priority(Thread.NORM_PRIORITY)
            .build());

    /**
     * Create a server logic module
     * <p>
     * Servers can have multiple logic modules for multiple instances of levels. When logic is required, it should be referenced and not created
     * </p>
     *
     * @param r Room number
     */
    public LogicModule(final byte r) {
        this.room = r;
        if (r == 0) {
            this.map = new GameMapArena();
        } else {
            this.map = new GameMapFloor1();
        }
        reset();
    }

    /**
     * Set a reference to the Server PacketSender.
     *
     * @param ps Server PacketSender
     */
    public static void setPacketSender(final PacketSender ps) {
        sender = ps;
    }

    public long getTime() {
        return this.currentTime;
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

        this.pAddQueue.clear();
        this.pDirKeydownQueue.clear();
        this.pUseSkillQueue.clear();
        this.projEffectQueue.clear();
        this.projAddQueue.clear();

        this.projMaxKeys = 500;
        this.resetStartTime = 0;
        if (this.room == 0) {
            this.map = new GameMapArena();
        } else {
            this.map = new GameMapFloor1();
        }
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

            /*if (nowMs - this.lastRefreshAll >= 30000) {
                // sender.broadcastAllPlayersUpdate(room);
                // System.out.println(sender.getBytes()/1024D);
                // sender.resetByte();
                this.lastRefreshAll = nowMs;
            }*/
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

        final LinkedList<Byte> remove = new LinkedList<>();
        for (final Map.Entry<Byte, Mob> mob : this.mobs.entrySet()) {
            try {
                mob.getValue().join();
                if (mob.getValue().isDead()) {
                    remove.add(mob.getValue().getKey());
                }
            } catch (final InterruptedException ex) {
                Globals.logError(ex.getLocalizedMessage(), ex, true);
            }
        }
        removeMobs(remove);
    }

    private void removeMobs(final LinkedList<Byte> remove) {
        while (!remove.isEmpty()) {
            final byte key = remove.pop();
            this.mobs.remove(key);
            returnMobKey(key);
        }
    }

    private void updatePlayers() {
        for (final Map.Entry<Byte, Player> player : this.players.entrySet()) {
            LOGIC_THREAD_POOL.execute(player.getValue());
        }
        final LinkedList<Byte> remove = new LinkedList<>();
        for (final Map.Entry<Byte, Player> player : this.players.entrySet()) {
            try {
                player.getValue().join();
                if (!(player.getValue().isConnected())) {
                    remove.add(player.getValue().getKey());
                    final byte[] bytes = new byte[2];
                    bytes[0] = Globals.DATA_PLAYER_DISCONNECT;
                    bytes[1] = player.getValue().getKey();
                    sender.sendAll(bytes, this.room);
                }
            } catch (final InterruptedException ex) {
                Globals.logError(ex.getLocalizedMessage(), ex, true);
            }
        }
        removeDisconnectedPlayers(remove);
    }

    private void removeDisconnectedPlayers(final LinkedList<Byte> remove) {
        while (!remove.isEmpty()) {
            final byte key = remove.pop();
            this.players.remove(key);
            this.playerKeys.add(key);
        }
    }

    private void updateProjectiles() {
        for (final Map.Entry<Integer, Projectile> p : this.projectiles.entrySet()) {
            LOGIC_THREAD_POOL.execute(p.getValue());
        }
        final LinkedList<Integer> remove = new LinkedList<>();
        for (final Map.Entry<Integer, Projectile> p : this.projectiles.entrySet()) {
            try {
                p.getValue().join();
                if (p.getValue().isExpired()) {
                    remove.add(p.getValue().getKey());
                }
            } catch (final InterruptedException ex) {
                Globals.logError(ex.getLocalizedMessage(), ex, true);
            }
        }
        removeProjectiles(remove);
    }

    private void removeProjectiles(final LinkedList<Integer> remove) {
        while (!remove.isEmpty()) {
            final int key = remove.pop();
            this.projectiles.remove(key);
            returnProjKey(key);
        }
    }

    /**
     * Return the array of players.
     *
     * @return Hash map of connected players
     */
    public ConcurrentHashMap<Byte, Player> getPlayers() {
        return this.players;
    }

    public ConcurrentHashMap<Byte, Mob> getMobs() {
        return this.mobs;
    }

    /**
     * Return the hash map of projectiles
     *
     * @return Array of connected players
     */
    public ConcurrentHashMap<Integer, Projectile> getProj() {
        return this.projectiles;
    }

    /**
     * Return the loaded server map
     *
     * @return Server GameMap
     */
    public GameMap getMap() {
        return this.map;
    }

    /**
     * Get this logic module's room number
     *
     * @return Byte - Room number
     */
    public byte getRoom() {
        return this.room;
    }

    /**
     * Return the next key open for connection
     *
     * @return returns next open key
     */
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

    /**
     * Queue a new player object to be added to the server.
     * <p>
     * Queue will be processed later.
     * </p>
     *
     * @param newPlayer New player to be queued
     */
    public void queueAddPlayer(final Player newPlayer) {
        this.pAddQueue.add(newPlayer);
    }

    /**
     * Queue move update to be applied for a player.
     * <p>
     * Data is only referenced here. Data to be processed in the queue later.
     * </p>
     *
     * @param data Bytes to be processed - 1:Key, 2:direction, 3:1 = true, 0 = false
     */
    public void queuePlayerDirKeydown(final byte[] data) {
        this.pDirKeydownQueue.add(data);
    }

    /**
     * Queue a player action to be performed
     *
     * @param data 1:key, 2:action type
     */
    public void queuePlayerUseSkill(final byte[] data) {
        this.pUseSkillQueue.add(data);
    }

    /**
     * Queue projectile entity to be added to the game.
     * <p>
     * Projectile must have been created when calling this.
     * </p>
     *
     * @param projectile New projectile to be added
     */
    public void queueAddProj(final Projectile projectile) {
        this.projAddQueue.add(projectile);
    }

    public void queueAddMob(final Mob mob) {
        if (mob.getKey() != -1) {
            this.mobAddQueue.add(mob);
        }
    }

    /**
     * Queue project effects to be applied to player.
     *
     * @param p Projectile which will have it's effects processed
     */
    public void queueProjEffect(final Projectile p) {
        this.projEffectQueue.add(p);
    }

    private void processQueues() {
        final Runnable[] queues = new Runnable[5];

        while (!this.pAddQueue.isEmpty()) {
            final Player newPlayer = this.pAddQueue.poll();
            if (newPlayer != null) {
                final byte key = newPlayer.getKey();
                this.players.put(key, newPlayer);
            }
        }

        queues[0] = () -> {
            try {
                while (!this.pDirKeydownQueue.isEmpty()) {
                    final byte[] data = this.pDirKeydownQueue.poll();
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
                while (!this.pUseSkillQueue.isEmpty()) {
                    final byte[] data = this.pUseSkillQueue.poll();
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
            if (!this.pDirKeydownQueue.isEmpty()) {
                this.pDirKeydownQueue.clear();
            }
            if (!this.pUseSkillQueue.isEmpty()) {
                this.pUseSkillQueue.clear();
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

    /**
     * Get next available projectile key
     *
     * @return key as integer
     */
    public int getNextProjKey() {
        Integer nextKey = this.projKeys.poll();
        while (nextKey == null) {
            this.projKeys.add(this.projMaxKeys);
            this.projMaxKeys++;
            nextKey = this.projKeys.poll();
        }
        return nextKey;
    }

    /**
     * Insert freed proj key into queue
     *
     * @param key Integer
     */
    public void returnProjKey(final int key) {
        this.projKeys.add(key);
    }

    public void returnMobKey(final byte key) {
        this.mobKeys.add(key);
    }

    /**
     * Check if this room contains this Player's unique ID.
     *
     * @param id Player uID
     * @return True if a player in this room has this uID.
     */
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
}
