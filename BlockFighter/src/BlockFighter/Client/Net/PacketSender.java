/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package blockfighter.Client.Net;

import BlockFighter.Client.Globals;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ken
 */
public class PacketSender {
    InetAddress address;
    DatagramSocket socket = null;
    
    public PacketSender(DatagramSocket socket) {
        this.socket = socket;
        try {
            this.address = InetAddress.getByName(Globals.SERVER_ADDRESS);
        } catch (UnknownHostException ex) {}
    }
    
    public void sendLogin(){
        byte[] bytes = new byte[Globals.PACKET_BYTE];
        bytes[0] = Globals.DATA_LOGIN;
        
        DatagramPacket requestPacket = createPacket(bytes);
        sendPacket(requestPacket);
    }
    
    public void sendGetAll() {
        byte[] bytes = new byte[Globals.PACKET_BYTE];
        bytes[0] = Globals.DATA_GET_ALL_PLAYER;
        
        DatagramPacket requestPacket = createPacket(bytes);
        sendPacket(requestPacket);
    }
    
    public void sendMove(byte index, byte direction, boolean move) {
        byte[] bytes = new byte[Globals.PACKET_BYTE + Globals.PACKET_BYTE + Globals.PACKET_BYTE + Globals.PACKET_BYTE];
        bytes[0] = Globals.DATA_SET_PLAYER_MOVE;
        bytes[1] = index;
        bytes[2] = direction;
        bytes[3] = (byte)(move ? 1:0);
        DatagramPacket requestPacket = createPacket(bytes);
        sendPacket(requestPacket);
    }
    
    public void sendKnockTest(byte index) {
        byte[] bytes = new byte[Globals.PACKET_BYTE + Globals.PACKET_BYTE];
        bytes[0] = Globals.DATA_KNOCK_TEST;
        bytes[1] = index;
        DatagramPacket requestPacket = createPacket(bytes);
        sendPacket(requestPacket);
    }
    
    public void sendGetPing(byte pID){
        byte[] bytes = new byte[Globals.PACKET_BYTE + Globals.PACKET_BYTE ];
        bytes[0] = Globals.DATA_PING;
        bytes[1] = pID;
        DatagramPacket requestPacket = createPacket(bytes);
        sendPacket(requestPacket);
    }
    
    private void sendPacket(DatagramPacket packet){
        try {
            socket.send(packet);
        } catch (IOException ex) {
            Logger.getLogger(PacketSender.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private DatagramPacket createPacket(byte[] bytes){
        return new DatagramPacket(bytes, bytes.length, address, Globals.SERVER_PORT);
    }
}
