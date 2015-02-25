package blockfighter.server;

import blockfighter.server.entities.player.Player;
import blockfighter.server.entities.proj.ProjBase;
import blockfighter.server.maps.GameMap;
import blockfighter.server.maps.GameMapFloor1;
import blockfighter.server.net.PacketSender;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Logic module of the server. Updates all objects and their interactions.
 *
 * @author Ken Kwan
 */
public class LogicModule extends Thread {

    private static PacketSender sender;
    private byte room = -1;

    private final ConcurrentHashMap<Byte, Player> players = new ConcurrentHashMap<>(Globals.SERVER_MAX_PLAYERS, 0.9f, Math.max(Globals.SERVER_MAX_PLAYERS / 5, 3));
    private final ConcurrentHashMap<Integer, ProjBase> projectiles = new ConcurrentHashMap<>(500, 0.75f, 10);
    private final GameMap map;
    private int projMaxKeys = 500;

    private final ConcurrentLinkedQueue<Byte> playerKeys = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<Integer> projKeys = new ConcurrentLinkedQueue<>();

    private final ConcurrentLinkedQueue<Player> pAddQueue = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<byte[]> pDirKeydownQueue = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<byte[]> pUseSkillQueue = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<ProjBase> projEffectQueue = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<ProjBase> projAddQueue = new ConcurrentLinkedQueue<>();

    private static ExecutorService threadPool = Executors.newFixedThreadPool(10 * Globals.SERVER_ROOMS);
    private long lastRefreshAll = 0;
    private double lastUpdateTime = 0;

    /**
     * Create a server logic module
     * <p>
     * Servers can have multiple logic modules for multiple instances of levels. When logic is required, it should be referenced and not created
     * </p>
     *
     * @param r Room number
     */
    public LogicModule(byte r) {
        room = r;
        map = new GameMapFloor1();
        for (int i = 0; i < 500; i++) {
            projKeys.add(i);
        }
        for (byte i = 0; i < Globals.SERVER_MAX_PLAYERS; i++) {
            playerKeys.add(i);
        }
    }

    public static void shutdownThreads() {
        threadPool.shutdownNow();
    }

    public void reset() {
        players.clear();
        projectiles.clear();
        projKeys.clear();
        playerKeys.clear();

        pAddQueue.clear();
        pDirKeydownQueue.clear();
        pUseSkillQueue.clear();
        projEffectQueue.clear();
        projAddQueue.clear();

        projMaxKeys = 500;
        for (int i = 0; i < 500; i++) {
            projKeys.add(i);
        }
        for (byte i = 0; i < Globals.SERVER_MAX_PLAYERS; i++) {
            playerKeys.add(i);
        }
    }

    @Override
    public void run() {
        try {
            processQueues();
            double now = System.nanoTime();
            long nowMs = System.currentTimeMillis();
            if (now - lastUpdateTime >= Globals.LOGIC_UPDATE) {
                updatePlayers();
                updateProjectiles();
                lastUpdateTime = now;
            }

            if (nowMs - lastRefreshAll >= 30000) {
                //sender.broadcastAllPlayersUpdate(room);
                //System.out.println(sender.getBytes()/1024D);
                //sender.resetByte();
                lastRefreshAll = nowMs;
            }
        } catch (Exception ex) {
            Globals.log(ex.getLocalizedMessage(), ex, true);
        }
    }

    private void updatePlayers() {
        for (Map.Entry<Byte, Player> player : players.entrySet()) {
            threadPool.execute(player.getValue());
        }
        LinkedList<Byte> remove = new LinkedList<>();
        for (Map.Entry<Byte, Player> player : players.entrySet()) {
            try {
                player.getValue().join();
                if (!(player.getValue().isConnected())) {
                    remove.add(player.getValue().getKey());
                    byte[] bytes = new byte[2];
                    bytes[0] = Globals.DATA_PLAYER_DISCONNECT;
                    bytes[1] = player.getValue().getKey();
                    sender.sendAll(bytes, room);
                }
            } catch (InterruptedException ex) {
                Globals.log(ex.getLocalizedMessage(), ex, true);
            }
        }
        removeDisconnectedPlayers(remove);
    }

    private void removeDisconnectedPlayers(LinkedList<Byte> remove) {
        while (!remove.isEmpty()) {
            byte key = remove.pop();
            players.remove(key);
            playerKeys.add(key);
        }
    }

    private void updateProjectiles() {
        for (Map.Entry<Integer, ProjBase> p : projectiles.entrySet()) {
            threadPool.execute(p.getValue());
        }
        LinkedList<Integer> remove = new LinkedList<>();
        for (Map.Entry<Integer, ProjBase> p : projectiles.entrySet()) {
            try {
                p.getValue().join();
                if (p.getValue().isExpired()) {
                    remove.add(p.getValue().getKey());
                }
            } catch (InterruptedException ex) {
                Globals.log(ex.getLocalizedMessage(), ex, true);
            }
        }
        removeProjectiles(remove);
    }

    private void removeProjectiles(LinkedList<Integer> remove) {
        while (!remove.isEmpty()) {
            int key = remove.peek();
            projectiles.remove(remove.pop());
            returnProjKey(key);
        }
    }

    /**
     * Set a reference to the Server PacketSender.
     *
     * @param ps Server PacketSender
     */
    public static void setPacketSender(PacketSender ps) {
        sender = ps;
    }

    /**
     * Return the array of players.
     *
     * @return Hash map of connected players
     */
    public ConcurrentHashMap<Byte, Player> getPlayers() {
        return players;
    }

    /**
     * Return the hash map of projectiles
     *
     * @return Array of connected players
     */
    public ConcurrentHashMap<Integer, ProjBase> getProj() {
        return projectiles;
    }

    /**
     * Return the loaded server map
     *
     * @return Server GameMap
     */
    public GameMap getMap() {
        return map;
    }

    /**
     * Get this logic module's room number
     *
     * @return Byte - Room number
     */
    public byte getRoom() {
        return room;
    }

    /**
     * Return the next key open for connection
     *
     * @return returns next open key
     */
    public byte getNextPlayerKey() {
        if (playerKeys.peek() == null) {
            return -1;
        }
        return playerKeys.poll();
    }

    /**
     * Queue a new player object to be added to the server.
     * <p>
     * Queue will be processed later.
     * </p>
     *
     * @param newPlayer New player to be queued
     */
    public void queueAddPlayer(Player newPlayer) {
        pAddQueue.add(newPlayer);
    }

    /**
     * Queue move update to be applied for a player.
     * <p>
     * Data is only referenced here.<br/>
     * Data to be processed in the queue later.
     * </p>
     *
     * @param data Bytes to be processed - 1:Key, 2:direction, 3:1 = true, 0 = false
     */
    public void queuePlayerDirKeydown(byte[] data) {
        pDirKeydownQueue.add(data);
    }

    /**
     * Queue a player action to be performed
     *
     * @param data 1:key, 2:action type
     */
    public void queuePlayerUseSkill(byte[] data) {
        pUseSkillQueue.add(data);
    }

    /**
     * Queue projectile entity to be added to the game.
     * <p>
     * Projectile must have been created when calling this.
     * </p>
     *
     * @param p New projectile to be added
     */
    public void queueAddProj(ProjBase p) {
        projAddQueue.add(p);
    }

    /**
     * Queue project effects to be applied to player.
     *
     * @param p Projectile which will affect the player
     */
    public void queueProjEffect(ProjBase p) {
        projEffectQueue.add(p);
    }

    private void processQueues() {
        Runnable[] queues = new Runnable[4];
        
        while (!pAddQueue.isEmpty()) {
            Player newPlayer = pAddQueue.poll();
            if (newPlayer != null) {
                byte key = newPlayer.getKey();
                players.put(key, newPlayer);
            }
        }

        queues[0] = new Runnable() {
            @Override
            public void run() {
                try {
                    while (!pDirKeydownQueue.isEmpty()) {
                        byte[] data = pDirKeydownQueue.poll();
                        if (data != null) {
                            byte key = data[2], dir = data[3], value = data[4];
                            if (players.containsKey(key)) {
                                players.get(key).setDirKeydown(dir, value == 1);
                            }
                        }
                    }
                } catch (Exception ex) {
                    Globals.log(ex.getLocalizedMessage(), ex, true);
                }
            }
        };

        queues[1] = new Runnable() {
            @Override
            public void run() {
                try {
                    while (!pUseSkillQueue.isEmpty()) {
                        byte[] data = pUseSkillQueue.poll();
                        if (data != null) {
                            byte key = data[2];
                            if (players.containsKey(key)) {
                                players.get(key).queueSkillUse(data);
                            }
                        }
                    }
                } catch (Exception ex) {
                    Globals.log(ex.getLocalizedMessage(), ex, true);
                }
            }
        };

        queues[2] = new Runnable() {
            @Override
            public void run() {
                try {
                    while (!projEffectQueue.isEmpty()) {
                        ProjBase proj = projEffectQueue.poll();
                        if (proj != null) {
                            proj.processQueue();
                        }
                    }
                } catch (Exception ex) {
                    Globals.log(ex.getLocalizedMessage(), ex, true);
                }
            }
        };

        queues[3] = new Runnable() {
            @Override
            public void run() {
                try {
                    while (!projAddQueue.isEmpty()) {
                        ProjBase p = projAddQueue.poll();
                        if (p != null) {
                            projectiles.put(p.getKey(), p);
                        }
                    }
                } catch (Exception ex) {
                    Globals.log(ex.getLocalizedMessage(), ex, true);
                }
            }
        };

        for (Runnable t : queues) {
            threadPool.execute(t);
        }
    }

    /**
     * Get next available projectile key
     *
     * @return key as integer
     */
    public int getNextProjKey() {
        if (projKeys.isEmpty()) {
            for (int i = projMaxKeys; i < projMaxKeys + 500; i++) {
                projKeys.add(i);
            }
            projMaxKeys += 500;
        }
        return projKeys.remove();
    }

    /**
     * Insert freed proj key into queue
     *
     * @param key Integer
     */
    public void returnProjKey(int key) {
        projKeys.add(key);
    }

    /**
     * Check if this room contains this Player's unique ID.
     *
     * @param id Player uID
     * @return True if a player in this room has this uID.
     */
    public boolean containsPlayerID(int id) {
        for (Map.Entry<Byte, Player> player : players.entrySet()) {
            if (player.getValue().getUniqueID() == id) {
                return true;
            }
        }
        return false;
    }
}
