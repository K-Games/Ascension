/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package blockfighter.client.net;

import blockfighter.client.Globals;
import blockfighter.client.LogicModule;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author Ken
 */
public class PacketReceiver extends Thread {

    private final LogicModule logic;
    private DatagramSocket socket = null;

    public PacketReceiver(LogicModule logic, DatagramSocket s) {
        this.logic = logic;
        socket = s;
    }

    @Override
    public void run() {
        ExecutorService tpes = Executors.newCachedThreadPool();
        try {
            while (true) {
                byte[] request = new byte[Globals.PACKET_MAX_SIZE];
                DatagramPacket p = new DatagramPacket(request, request.length);
                socket.receive(p);
                tpes.execute(new PacketHandler(p, logic));
            }
        } catch (IOException e) {
            System.out.println("Server Offline");
        } finally {
            tpes.shutdownNow();
        }
    }
}
