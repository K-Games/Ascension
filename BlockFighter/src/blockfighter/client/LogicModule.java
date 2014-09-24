/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package blockfighter.client;

import blockfighter.client.entities.Player;
import blockfighter.client.net.PacketSender;
import java.net.DatagramSocket;
import java.util.Arrays;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ckwa290
 */
public class LogicModule extends Thread {
    final Lock lock = new ReentrantLock();
    final Condition gotLogin = lock.newCondition();
    
    private boolean isRunning = false;
    //Concurrent Queuing
    private ConcurrentLinkedQueue<Byte> playersAddQueue = new ConcurrentLinkedQueue<>();
    private ConcurrentLinkedQueue<byte[]> playersMoveQueue = new ConcurrentLinkedQueue<>();
    private ConcurrentLinkedQueue<byte[]> playersFacingQueue = new ConcurrentLinkedQueue<>();
    private ConcurrentLinkedQueue<byte[]> playersStateQueue = new ConcurrentLinkedQueue<>();
    
    private Player[] players = null;
    private byte myIndex = -1;

    private PacketSender sender = null;
    private long pingTime = 0;
    private int ping = -1;
    private byte pID = 0;
    
    boolean[] keyDownMove = {false, false, false, false};
    
    public LogicModule(DatagramSocket socket){
        sender = new PacketSender(socket);
        isRunning = true;
    }
    
    @Override
    public void run(){
        double lastUpdateTime = System.nanoTime(); //Last time we updated
        double lastRequestTime = lastUpdateTime;
        double lastQueueTime = lastUpdateTime;
        double lastPingTime = lastUpdateTime;
        
        int attempt = 0;
        
        while (attempt < 1 && myIndex == -1){
            lock.lock();
            try {
                attempt++;
                sender.sendLogin();
                System.out.println("Login Attempt #" + attempt);
                gotLogin.await(5000, TimeUnit.MILLISECONDS);
            } catch (InterruptedException ex) {
                Logger.getLogger(LogicModule.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                lock.unlock();
            }
        }
        
        if (attempt > 1) {
            System.out.println("Cannot connect");
            return;
        }
        sender.sendGetAll();
        
        while (isRunning) {
            double now = System.nanoTime(); //Get time now
            
            if (now - lastQueueTime >= Globals.QUEUES_UPDATE) {
                processQueues();
                lastQueueTime = now;
            }
            
            if (now - lastUpdateTime >= Globals.LOGIC_UPDATE) {
                sender.sendMove(myIndex,Globals.UP, keyDownMove[Globals.UP]);
                sender.sendMove(myIndex,Globals.DOWN, keyDownMove[Globals.DOWN]);
                sender.sendMove(myIndex,Globals.LEFT, keyDownMove[Globals.LEFT]);
                sender.sendMove(myIndex,Globals.RIGHT, keyDownMove[Globals.RIGHT]);
                lastUpdateTime = now;
            }
            
            if (now - lastRequestTime >= Globals.REQUESTALL_UPDATE) {
                sender.sendGetAll();
                lastRequestTime = now;
            }
            
            if (now - lastPingTime >= Globals.PING_UPDATE) {
                pID = (byte)(Math.random() * 256);
                pingTime = System.currentTimeMillis();
                sender.sendGetPing(pID);
                lastPingTime = now;
            }
            
            //Yield until something is happening
            while (now - lastQueueTime < Globals.QUEUES_UPDATE
                    && now - lastPingTime < Globals.PING_UPDATE
                    && now - lastRequestTime < Globals.REQUESTALL_UPDATE
                    && now - lastUpdateTime < Globals.LOGIC_UPDATE) {
                Thread.yield();
                now = System.nanoTime();
            }
        }
    }
    
    private void processQueues(){
        while (!playersAddQueue.isEmpty()){
            if (players==null) break;
            players[playersAddQueue.remove()] = new Player(0,0);
        }
        
        while (!playersMoveQueue.isEmpty()){
            if (players==null) break;
            byte[] data = playersMoveQueue.remove();
            byte index = data[1];
            int x = Globals.bytesToInt(Arrays.copyOfRange(data, 2, 6));
            int y = Globals.bytesToInt(Arrays.copyOfRange(data, 6, 10));
            if (players[index] != null) {
                players[index].setPos(x, y);
            } else {
                players[index] = new Player(x,y);
            }
        }
        
        while (!playersFacingQueue.isEmpty()){
            if (players==null) break;
            byte[] data = playersFacingQueue.remove();
            byte index = data[1];
            byte facing = data[2];
            if (players[index] != null) {
                players[index].setFacing(facing);
            }
        }
        
        while (!playersStateQueue.isEmpty()){
            if (players==null) break;
            byte[] data = playersStateQueue.remove();
            byte index = data[1];
            if (players[index] != null) {
                byte state = data[2];
                byte frame = data[3];
                players[index].setState(state);
                players[index].setFrame(frame);
            }
        }

    }
        
    public boolean isRunning(){ return isRunning; }
    
    public void setKeyDown(int direction, boolean move) {
        keyDownMove[direction] = move;
    }
    
    public void setMyIndex(byte index){
        myIndex = index;
        lock.lock();
        try {
            gotLogin.signal();
        } finally {
            lock.unlock();
        }
    }
    public byte getMyIndex(){ return myIndex; }
    public int getPing() { return ping; }
    public void setPlayersSize(byte size) {
        players = new Player[size];
    }   
    public Player[] getPlayers() { return players; }
    
    public void addPlayer(byte index) {
        playersAddQueue.add(index);
    }
    
    public void setPlayerPos(byte[] data){
        playersMoveQueue.add(data);
    }
    
    public void setPlayerFacing(byte[] data){
        playersFacingQueue.add(data);
    }
    
    public void setPlayerState(byte[] data){
        playersStateQueue.add(data);
    }
    
    public void setPing(byte rID){
        if (rID != pID) return;
        ping = (int) (System.currentTimeMillis() - pingTime);
        if (ping >= 1000) ping = 9999;
    }
}
