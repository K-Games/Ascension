package blockfighter.server;

import blockfighter.server.entities.player.Player;
import blockfighter.server.entities.proj.ProjBase;
import blockfighter.server.maps.GameMap;
import blockfighter.server.maps.TestMap;
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
 * @author Ken
 */
public class LogicModule extends Thread {

    private boolean isRunning = false;
    private final ConcurrentHashMap<Byte, Player> players = new ConcurrentHashMap<>(Globals.SERVER_MAX_PLAYERS, 0.9f, Globals.SERVER_MAX_PLAYERS / 10);
    private final ConcurrentHashMap<Integer, ProjBase> projectiles = new ConcurrentHashMap<>(500, 0.75f, 10);

    private PacketSender sender;
    private final GameMap map;

    private int projMaxKeys = 500;

    private final ConcurrentLinkedQueue<Player> pAddQueue = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<byte[]> pMoveQueue = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<byte[]> pActionQueue = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<Integer> projKeys = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<ProjBase> projEffectQueue = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<ProjBase> projAddQueue = new ConcurrentLinkedQueue<>();
    private byte numPlayers = 0;
    private byte room = -1;

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
        isRunning = true;
        map = new TestMap();
        for (int i = 0; i < 500; i++) {
            projKeys.add(i);
        }
    }

    @Override
    public void run() {
        double lastUpdateTime = System.nanoTime();
        long lastRefreshAll = System.currentTimeMillis();
        ExecutorService threadPool = Executors.newFixedThreadPool(10);
        sender.setThreadPool(threadPool);

        while (isRunning) {
            processQueues(threadPool);
            double now = System.nanoTime();
            long nowMs = System.currentTimeMillis();
            if (now - lastUpdateTime >= Globals.LOGIC_UPDATE) {
                updatePlayers(threadPool);
                updateProjectiles(threadPool);

                lastUpdateTime = now;
            }

            if (nowMs - lastRefreshAll >= 10000) {
                sender.broadcastAllPlayersUpdate(room);
                //System.out.println(sender.getBytes());
                //packetSender.resetByte();
                lastRefreshAll = nowMs;
            }

            while (now - lastUpdateTime < Globals.LOGIC_UPDATE) {
                Thread.yield();
                now = System.nanoTime();
            }
        }
        threadPool.shutdownNow();
        Globals.LOG_THREADS.shutdown();
    }

    private void updatePlayers(ExecutorService threadPool) {
        for (Map.Entry<Byte, Player> player : players.entrySet()) {
            threadPool.execute(player.getValue());
        }

        for (Map.Entry<Byte, Player> player : players.entrySet()) {
            try {
                player.getValue().join();
            } catch (InterruptedException ex) {
                Globals.log(ex.getLocalizedMessage(), ex, true);
            }
        }
    }

    private void updateProjectiles(ExecutorService threadPool) {
        LinkedList<Integer> remove = new LinkedList<>();
        for (Map.Entry<Integer, ProjBase> p : projectiles.entrySet()) {
            threadPool.execute(p.getValue());
        }

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
     * @param bc Server PacketSender
     */
    public void setPacketSender(PacketSender bc) {
        sender = bc;
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

    public byte getRoom() {
        return room;
    }

    /**
     * Return the number of connected players.
     *
     * @return Number of connected players
     */
    public byte getNumPlayers() {
        return numPlayers;
    }

    /**
     * Return the next key open for connection
     *
     * @return returns next open key
     */
    public byte getNextPlayerKey() {
        if (numPlayers >= Globals.SERVER_MAX_PLAYERS) {
            return -1;
        }
        return numPlayers++;
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
    public void queuePlayerMove(byte[] data) {
        pMoveQueue.add(data);
    }

    /**
     * Queue a player action to be performed
     *
     * @param data 1:key, 2:action type
     */
    public void queuePlayerAction(byte[] data) {
        pActionQueue.add(data);
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

    private void processQueues(ExecutorService threadPool) {
        Thread[] queues = new Thread[4];

        while (!pAddQueue.isEmpty()) {
            Player newPlayer = pAddQueue.poll();
            if (newPlayer != null) {
                byte key = newPlayer.getKey();
                players.put(key, newPlayer);
            }
        }

        queues[0] = new Thread() {
            @Override
            public void run() {
                while (!pMoveQueue.isEmpty()) {
                    byte[] data = pMoveQueue.poll();
                    byte key = data[2], dir = data[3], value = data[4];
                    if (data != null && players.containsKey(key)) {
                        players.get(key).setMove(dir, value == 1);
                    }
                }
            }
        };

        queues[1] = new Thread() {
            @Override
            public void run() {
                while (!pActionQueue.isEmpty()) {
                    byte[] data = pActionQueue.poll();
                    byte key = data[2];
                    if (data != null && players.containsKey(key)) {
                        players.get(key).processAction(data);
                    }
                }
            }
        };

        queues[2] = new Thread() {
            @Override
            public void run() {
                while (!projEffectQueue.isEmpty()) {
                    ProjBase proj = projEffectQueue.poll();
                    if (proj != null) {
                        proj.processQueue();
                    }
                }
            }
        };

        queues[3] = new Thread() {
            @Override
            public void run() {
                while (!projAddQueue.isEmpty()) {
                    ProjBase p = projAddQueue.poll();
                    if (p != null) {
                        projectiles.put(p.getKey(), p);
                    }
                }
            }
        };

        for (Thread t : queues) {
            threadPool.execute(t);
        }

        for (Thread t : queues) {
            try {
                t.join();
            } catch (InterruptedException ex) {
                Globals.log(ex.getLocalizedMessage(), ex, true);
            }
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
     * Kill server logic.
     */
    public void shutdown() {
        isRunning = false;
    }
}
