/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package BlockFighter.Server;

import BlockFighter.Server.Entities.Player;
import BlockFighter.Server.Maps.TestMap;
import BlockFighter.Server.Net.Broadcaster;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Logic module of the server. Updates all objects and their interactions.
 * @author Ken
 */
public class LogicModule extends Thread{
    private boolean isRunning = false;
    private Player[] players = new Player[Globals.MAX_PLAYERS];
    private Broadcaster broadcaster;
    private TestMap map;
    private ConcurrentLinkedQueue<Player> pAddQueue = new ConcurrentLinkedQueue<>();
    private ConcurrentLinkedQueue<byte[]> pMoveQueue = new ConcurrentLinkedQueue<>();
    private byte numPlayers = 0;
    
    /**
     * Create a server logic module
     * Server should only have 1 logic module
     * When logic is required, it should be referenced and not created
     */
    public LogicModule(){
        isRunning = true;
        map = new TestMap();
    }
    
    @Override
    public void run(){
        double lastUpdateTime = System.nanoTime();
        long lastRefreshAll = System.currentTimeMillis();
        ExecutorService playerPool = Executors.newCachedThreadPool();
        
        while (isRunning) {
            processQueues();
            double now = System.nanoTime();
            long nowMs = System.currentTimeMillis();
            if (now - lastUpdateTime >= Globals.LOGIC_UPDATE) {
                for (Player player : players) {
                   if (player!=null) playerPool.execute(player);
                }
                
                for (Player player : players) {
                    try {
                        if (player!=null) player.join();
                    } catch (InterruptedException ex) {
                        Logger.getLogger(LogicModule.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
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
    
    /**
     * Set a reference to the Server Broadcaster.
     * @param bc Server Broadcaster
     */
    public void setBroadcaster(Broadcaster bc) {
        broadcaster = bc;
    }
    
    /**
     * Return the array of players.
     * @return Array of connected players
     */
    public Player[] getPlayers() { return players; }

    /**
     * Return the loaded server map
     * @return Server Map
     */
    public TestMap getMap() { return map; }

    /**
     * Return the number of connected players.
     * @return Number of connected players
     */
    public byte getNumPlayers() { return numPlayers; }

    /**
     * Return the next index open for connection
     * @return
     */
    public byte getNextIndex() { return numPlayers++; }
    
    /**
     * Queue a new player object to be added to the server.
     * Queue will be processed later.
     * @param newPlayer New player to be queued
     */
    public void queueAddPlayer(Player newPlayer) {
        pAddQueue.add(newPlayer);
    }
    
    /**
     * Queue move update for a player.
     * Data is only referenced here.
     * Data to be processed in the queue later.
     * @param data Bytes to be processed - 1:Index, 2:direction, 3:1 = true, 0 = false
     */
    public void queuePlayerMove(byte[] data) {
        pMoveQueue.add(data);
    }

    private void processQueues(){
        while (!pAddQueue.isEmpty()) {
            Player newPlayer = pAddQueue.remove();
            byte index = newPlayer.getIndex();
            players[index] = newPlayer;
        }

        while (!pMoveQueue.isEmpty()) {
            byte[] data = pMoveQueue.remove();
            if (players[data[1]] == null) continue;
            players[data[1]].setMove(data[2], data[3] == 1);
        }
    }
    
    /**
     * Kill server logic.
     */
    public void shutdown(){
        isRunning = false;
    }
}
