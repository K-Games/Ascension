package blockfighter.server;

import blockfighter.server.entities.Player;
import blockfighter.server.entities.proj.ProjBase;
import blockfighter.server.maps.Map;
import blockfighter.server.maps.TestMap;
import blockfighter.server.net.Broadcaster;
import java.util.Iterator;
import java.util.LinkedList;
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
    private final Player[] players = new Player[Globals.MAX_PLAYERS];
    private final ConcurrentHashMap<Integer, ProjBase> projectiles = new ConcurrentHashMap<>();

    private Broadcaster broadcaster;
    private final Map map;
    
    private int projMaxKeys = 1000;
    
    private final ConcurrentLinkedQueue<Player> pAddQueue = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<byte[]> pMoveQueue = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<byte[]> pActionQueue = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<Integer> projKeys = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<ProjBase> projEffectQueue = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<ProjBase> projAddQueue = new ConcurrentLinkedQueue<>();
    private byte numPlayers = 0;

    /**
     * Create a server logic module
     * <p>
     * Server should only have 1 logic module.<br/>
     * When logic is required, it should be referenced and not created
     * </p>
     */
    public LogicModule() {
        isRunning = true;
        map = new TestMap();
        for (int i = 0; i < 1000; i++) {
            projKeys.add(i);
        }
    }

    @Override
    public void run() {
        double lastUpdateTime = System.nanoTime();
        long lastRefreshAll = System.currentTimeMillis();
        ExecutorService threadPool = Executors.newCachedThreadPool();

        while (isRunning) {
            processQueues(threadPool);
            double now = System.nanoTime();
            long nowMs = System.currentTimeMillis();
            if (now - lastUpdateTime >= Globals.LOGIC_UPDATE) {
                updatePlayers(threadPool);
                updateProjectiles(threadPool);
                
                lastUpdateTime = now;
            }

            if (nowMs - lastRefreshAll >= 1000) {
                //broadcaster.broadcastAllPlayersUpdate();
                //System.out.println(broadcaster.getBytes());
                //broadcaster.resetByte();
                lastRefreshAll = nowMs;
            }

            while (now - lastUpdateTime < Globals.LOGIC_UPDATE) {
                Thread.yield();
                now = System.nanoTime();
            }
        }
    }

    private void updatePlayers(ExecutorService threadPool) {
        for (Player player : players) {
            if (player != null) {
                threadPool.execute(player);
            }
        }

        for (Player player : players) {
            try {
                if (player != null) {
                    player.join();
                }
            } catch (InterruptedException ex) {
                Globals.log(ex.getLocalizedMessage(), ex, true);
            }
        }
    }

    private void updateProjectiles(ExecutorService threadPool) {
        Iterator<Integer> projItr = projectiles.keySet().iterator();
        while (projItr.hasNext()) {
            Integer key = projItr.next();
            if (projectiles.get(key) != null) {
                threadPool.execute(projectiles.get(key));
            }
        }

        projItr = projectiles.keySet().iterator();
        while (projItr.hasNext()) {
            Integer key = projItr.next();
            if (projectiles.get(key) != null) {
                try {
                    projectiles.get(key).join();
                } catch (InterruptedException ex) {
                    Globals.log(ex.getLocalizedMessage(), ex, true);
                }
            }
        }
        removeProjectiles();
    }

    private void removeProjectiles() {
        LinkedList<Integer> remove = new LinkedList<>();
        Iterator<Integer> projItr = projectiles.keySet().iterator();

        while (projItr.hasNext()) {
            Integer key = projItr.next();
            ProjBase p = projectiles.get(key);
            if (p.isExpired()) {
                remove.add(key);
            }
        }

        /*
         for (ProjBase p : projectiles) {
         if (p != null && p.isExpired()) {
         remove.add(p);
         }
         }*/
        while (!remove.isEmpty()) {
            int key = remove.peek();
            projectiles.remove(remove.pop());
            returnProjKey(key);
        }
    }

    /**
     * Set a reference to the Server Broadcaster.
     *
     * @param bc Server Broadcaster
     */
    public void setBroadcaster(Broadcaster bc) {
        broadcaster = bc;
    }

    /**
     * Return the array of players.
     *
     * @return Array of connected players
     */
    public Player[] getPlayers() {
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
     * @return Server Map
     */
    public Map getMap() {
        return map;
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
     * Return the next index open for connection
     *
     * @return
     */
    public byte getNextIndex() {
        if (numPlayers >= Globals.MAX_PLAYERS) {
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
     * @param data Bytes to be processed - 1:Index, 2:direction, 3:1 = true, 0 = false
     */
    public void queuePlayerMove(byte[] data) {
        pMoveQueue.add(data);
    }

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
        Thread[] queues = new Thread[5];

        queues[0] = new Thread() {
            @Override
            public void run() {
                while (!pAddQueue.isEmpty()) {
                    Player newPlayer = pAddQueue.remove();
                    byte index = newPlayer.getIndex();
                    players[index] = newPlayer;
                }
            }
        };

        queues[1] = new Thread() {
            @Override
            public void run() {
                while (!pMoveQueue.isEmpty()) {
                    byte[] data = pMoveQueue.remove();
                    if (players[data[1]] == null) {
                        continue;
                    }
                    players[data[1]].setMove(data[2], data[3] == 1);
                }
            }
        };

        queues[2] = new Thread() {
            @Override
            public void run() {
                while (!pActionQueue.isEmpty()) {
                    byte[] data = pActionQueue.remove();
                    if (players[data[1]] == null) {
                        continue;
                    }
                    players[data[1]].processAction(data);
                }
            }
        };

        queues[3] = new Thread() {
            @Override
            public void run() {
                while (!projEffectQueue.isEmpty()) {
                    ProjBase proj = projEffectQueue.remove();
                    proj.processQueue();
                }
            }
        };

        queues[4] = new Thread() {
            @Override
            public void run() {
                while (!projAddQueue.isEmpty()) {
                    ProjBase p = projAddQueue.remove();
                    projectiles.put(p.getKey(), p);
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

    public int getNextProjKey() {
        if (projKeys.isEmpty()) {
            for (int i = projMaxKeys; i < projMaxKeys + 1000; i++){
                projKeys.add(i);
            }
            projMaxKeys += 1000;
        }
        return projKeys.remove();
    }
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
