package blockfighter.server;

import blockfighter.server.entities.Player;
import blockfighter.server.entities.projectiles.ProjBase;
import blockfighter.server.maps.Map;
import blockfighter.server.maps.TestMap;
import blockfighter.server.net.Broadcaster;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Logic module of the server. Updates all objects and their interactions.
 *
 * @author Ken
 */
public class LogicModule extends Thread {

    private boolean isRunning = false;
    private Player[] players = new Player[Globals.MAX_PLAYERS];
    private HashMap<Integer, ProjBase> projectiles = new HashMap<>();

    private Broadcaster broadcaster;
    private Map map;
    private ConcurrentLinkedQueue<Player> pAddQueue = new ConcurrentLinkedQueue<>();
    private ConcurrentLinkedQueue<byte[]> pMoveQueue = new ConcurrentLinkedQueue<>();
    private ConcurrentLinkedQueue<ProjBase> projEffectQueue = new ConcurrentLinkedQueue<>();
    private ConcurrentLinkedQueue<ProjBase> projAddQueue = new ConcurrentLinkedQueue<>();
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
    }

    @Override
    public void run() {
        double lastUpdateTime = System.nanoTime();
        long lastRefreshAll = System.currentTimeMillis();
        ExecutorService threadPool = Executors.newCachedThreadPool();

        while (isRunning) {
            processQueues();
            double now = System.nanoTime();
            long nowMs = System.currentTimeMillis();
            if (now - lastUpdateTime >= Globals.LOGIC_UPDATE) {
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
                        Logger.getLogger(LogicModule.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

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
                            Logger.getLogger(LogicModule.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
                /*
                 for (ProjBase p : projectiles) {
                 if (p != null) {
                 threadPool.execute(p);
                 }
                 }

                 for (ProjBase p : projectiles) {
                 try {
                 if (p != null) {
                 p.join();
                 }
                 } catch (InterruptedException ex) {
                 Logger.getLogger(LogicModule.class.getName()).log(Level.SEVERE, null, ex);
                 }
                 }
                 */

                removeProjectiles();
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
            projectiles.remove(remove.pop());
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
    public HashMap getProj() {
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
     * @param data Bytes to be processed - 1:Index, 2:direction, 3:1 = true, 0 =
     * false
     */
    public void queuePlayerMove(byte[] data) {
        pMoveQueue.add(data);
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
     * Queue knockback to be applied to player.
     *
     * @param p Projectile which will knockback the player
     */
    public void queueKnockPlayer(ProjBase p) {
        projEffectQueue.add(p);
    }

    private void processQueues() {
        while (!pAddQueue.isEmpty()) {
            Player newPlayer = pAddQueue.remove();
            byte index = newPlayer.getIndex();
            players[index] = newPlayer;
        }

        while (!pMoveQueue.isEmpty()) {
            byte[] data = pMoveQueue.remove();
            if (players[data[1]] == null) {
                continue;
            }
            players[data[1]].setMove(data[2], data[3] == 1);
        }

        while (!projEffectQueue.isEmpty()) {
            ProjBase proj = projEffectQueue.remove();
            proj.processQueue();
        }

        while (!projAddQueue.isEmpty()) {
            ProjBase p = projAddQueue.remove();
            projectiles.put(p.getKey(), p);
        }
    }

    /**
     * Kill server logic.
     */
    public void shutdown() {
        isRunning = false;
    }
}
