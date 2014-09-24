/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package BlockFighter.Server.Net;

import BlockFighter.Server.Globals;
import BlockFighter.Server.LogicModule;
import BlockFighter.Server.Entities.Player;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.Arrays;

/**
 * Threads to handle incoming requests.
 * @author Ken
 */
public class PacketHandler extends Thread{
    private DatagramPacket requestPacket = null;
    private final LogicModule logic;
    private Broadcaster broadcaster;
    
    /**
     * Initialize request handler when a request is received by the socket.
     * @param bc Reference to Server Broadcaster
     * @param request Packet that is received
     * @param logic Reference to Logic module
     */
    public PacketHandler(Broadcaster bc, DatagramPacket request, LogicModule logic) {
        requestPacket = request;
        this.broadcaster = bc;
        this.logic = logic;
    }
    
    @Override
    public void run(){
        byte[] data = requestPacket.getData();
        byte dataType = data[0];
        
        InetAddress address = requestPacket.getAddress();
        int port = requestPacket.getPort();
        switch (dataType){
            case Globals.DATA_LOGIN: receiveLogin(address, port); break;
            case Globals.DATA_GET_ALL_PLAYER: receiveGetAllPlayer(address, port); break;
            case Globals.DATA_SET_PLAYER_MOVE: receiveSetPlayerMove(data); break;
            case Globals.DATA_PING: receiveGetPing(address, port, data); break;
            case Globals.DATA_KNOCK_TEST: receiveKnockTest(data); break;
        }
    }
    
    private void receiveGetPing(InetAddress address, int port, byte[] data){
        //Buffer header
        byte[] bytes = new byte[Globals.PACKET_BYTE + Globals.PACKET_BYTE];
        //Index
        bytes[0] = Globals.DATA_PING;
        bytes[1] = data[1];
        broadcaster.sendPlayer(bytes, address, port);
    }
    
    private void receiveKnockTest(byte[] data){
        logic.queueKnockback(data);
    }
    
    private void receiveLogin(InetAddress address, int port){
        System.out.println("DATA_LOGIN");
        byte freeIndex = logic.getNextIndex();
        
        if (freeIndex == -1) return;
        Player newPlayer = new Player(freeIndex, address, port, Math.random()* 1180.0 + 100, 0, broadcaster, logic.getMap());
        logic.queueAddPlayer(newPlayer);
        //Buffer header
        byte[] bytes = new byte[Globals.PACKET_BYTE + Globals.PACKET_BYTE + Globals.PACKET_BYTE];
        //Index
        bytes[0] = Globals.DATA_LOGIN;
        bytes[1] = freeIndex;
        bytes[2] = Globals.MAX_PLAYERS;
        broadcaster.sendPlayer(bytes, address, port);
        
        //Buffer header
        bytes = new byte[Globals.PACKET_BYTE + Globals.PACKET_BYTE + Globals.PACKET_INT + Globals.PACKET_INT];
        //Index
        bytes[0] = Globals.DATA_GET_PLAYER_POS;
        bytes[1] = newPlayer.getIndex();
        //Send Pos
        byte[] posXInt = Globals.intToByte((int)newPlayer.getX());
        bytes[2] = posXInt[0];
        bytes[3] = posXInt[1];
        bytes[4] = posXInt[2];
        bytes[5] = posXInt[3];
        byte[] posYInt = Globals.intToByte((int)newPlayer.getY());
        bytes[6] = posYInt[0];
        bytes[7] = posYInt[1];
        bytes[8] = posYInt[2];
        bytes[9] = posYInt[3];
        
        //tell everyone
        broadcaster.sendAll(bytes);
        
        bytes = new byte[Globals.PACKET_BYTE + Globals.PACKET_BYTE + Globals.PACKET_BYTE];
        bytes[0] = Globals.DATA_SET_PLAYER_FACING;
        bytes[1] = newPlayer.getIndex();
        bytes[2] = newPlayer.getFacing();
        broadcaster.sendAll(bytes);
    }
    
    private void receiveGetAllPlayer(InetAddress address, int port){
        Player[] players = Arrays.copyOf(logic.getPlayers(), logic.getPlayers().length);
        for (Player player : players) {
            if (player == null) continue;
            //Buffer header
            byte[] bytes = new byte[Globals.PACKET_BYTE + Globals.PACKET_INT + Globals.PACKET_INT + Globals.PACKET_INT];
            //Index
            bytes[0] = Globals.DATA_GET_PLAYER_POS;
            bytes[1] = player.getIndex();          
            //Send Pos
            byte[] posXInt = Globals.intToByte((int) player.getX());
            bytes[2] = posXInt[0];
            bytes[3] = posXInt[1];
            bytes[4] = posXInt[2];
            bytes[5] = posXInt[3];
            byte[] posYInt = Globals.intToByte((int) player.getY());
            bytes[6] = posYInt[0];
            bytes[7] = posYInt[1];
            bytes[8] = posYInt[2];
            bytes[9] = posYInt[3];
            broadcaster.sendPlayer(bytes, address, port);
            
            bytes = new byte[Globals.PACKET_BYTE + Globals.PACKET_BYTE + Globals.PACKET_BYTE];
            bytes[0] = Globals.DATA_SET_PLAYER_FACING;
            bytes[1] = player.getIndex();
            bytes[2] = player.getFacing();
            broadcaster.sendPlayer(bytes, address, port);
            
            bytes = new byte[Globals.PACKET_BYTE + Globals.PACKET_BYTE + Globals.PACKET_BYTE + Globals.PACKET_BYTE];
            bytes[0] = Globals.DATA_SET_PLAYER_STATE;
            bytes[1] = player.getIndex();
            bytes[2] = player.getPlayerState();
            bytes[3] = player.getFrame();
            broadcaster.sendPlayer(bytes, address, port);
        }
    }
    
    private void receiveSetPlayerMove(byte[] data){
        logic.queuePlayerMove(data);
    }
}
