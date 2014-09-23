/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package blockfighter.Client.Net;

import BlockFighter.Client.Globals;
import BlockFighter.Client.LogicModule;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author Ken
 */
public class ConnectionThread extends Thread{
    private final LogicModule logic;
    private DatagramSocket socket = null;
    
    public ConnectionThread(LogicModule logic, DatagramSocket s){
        this.logic = logic;
        socket = s;
    }
    
    @Override
    public void run() {
        ExecutorService tpes = Executors.newCachedThreadPool();
        while (true) {
            byte[] request = new byte[Globals.PACKET_MAX_SIZE];
            DatagramPacket p = new DatagramPacket(request, request.length);
            try {
                socket.receive(p);
                tpes.execute(new PacketHandler(p, logic));
            } catch (SocketTimeoutException e) {
                System.out.println("Timed out");
                break;
            } catch (IOException e) {
            }
        }
    }
}
