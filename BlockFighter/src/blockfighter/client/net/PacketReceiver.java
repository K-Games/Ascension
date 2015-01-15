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
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author Ken
 */
public class PacketReceiver extends Thread {

    private final LogicModule logic;
    private DatagramSocket socket = null;
    private static ExecutorService tpes = Executors.newFixedThreadPool(4);

    public PacketReceiver(LogicModule logic, DatagramSocket s) {
        this.logic = logic;
        socket = s;
    }

    @Override
    public void run() {
        try {
            while (true) {
                byte[] request = new byte[Globals.PACKET_MAX_SIZE];
                DatagramPacket p = new DatagramPacket(request, request.length);
                try {
                    if (socket != null) {
                        socket.receive(p);
                    }
                } catch (SocketException e) {
                }
                tpes.execute(new PacketHandler(p, logic));
            }
        } catch (IOException ex) {
        } finally {
            tpes.shutdownNow();
        }
    }

    public void setSocket(DatagramSocket s) {
        if (socket != null) {
            socket.close();
        }
        socket = s;
    }
}
