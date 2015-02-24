/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package blockfighter.client.net;

import blockfighter.client.Globals;
import blockfighter.client.LogicModule;
import blockfighter.client.screen.ScreenIngame;
import blockfighter.client.screen.ScreenLoading;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author Ken Kwan
 */
public class PacketReceiver extends Thread {

    private static LogicModule logic;
    private DatagramSocket socket = null;
    private static ExecutorService threadPool = Executors.newFixedThreadPool(5);
    private boolean isConnected = true;

    public static void setLogic(LogicModule l) {
        logic = l;
    }

    public PacketReceiver(DatagramSocket s) {
        socket = s;
    }

    @Override
    public void run() {
        try {
            while (true) {
                byte[] request = new byte[Globals.PACKET_MAX_SIZE];
                DatagramPacket p = new DatagramPacket(request, request.length);
                socket.receive(p);
                threadPool.execute(new PacketHandler(p));
            }
        } catch (SocketTimeoutException | SocketException e) {
        } catch (IOException ex) {
        }
        System.out.println("Receiver End");
        if (logic.getScreen() instanceof ScreenIngame || logic.getScreen() instanceof ScreenLoading) {
            logic.returnMenu();
        }
        isConnected = false;
    }

    public void shutdown() {
        socket.close();
    }

    public boolean isConnected() {
        return isConnected;
    }
}
