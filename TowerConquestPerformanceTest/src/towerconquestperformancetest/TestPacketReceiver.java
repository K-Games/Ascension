package towerconquestperformancetest;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TestPacketReceiver extends Thread {

    private DatagramSocket socket = null;
    private TestPacketHandler ph;
    private boolean isConnected = true;

    public TestPacketReceiver(DatagramSocket s, TestPacketHandler ph) {
        this.socket = s;
        this.ph = ph;
    }

    @Override
    public void run() {
        try {
            while (true) {
                final byte[] request = new byte[Globals.PACKET_MAX_SIZE];
                final DatagramPacket p = new DatagramPacket(request, request.length);
                this.socket.receive(p);
                ph.queuePacket(p);
            }
        } catch (final SocketTimeoutException e) {

        } catch (final SocketException e) {

        } catch (final IOException ex) {
        }
        this.isConnected = false;
    }

    public void shutdown() {
        this.socket.close();
    }

    public boolean isConnected() {
        return this.isConnected;
    }
}
