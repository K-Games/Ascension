package blockfighter.server.net;

import blockfighter.server.Globals;
import blockfighter.server.LogicModule;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Thread to accept incoming connections.
 *
 * @author Ken
 */
public class ConnectionThread extends Thread {

    private final LogicModule logic;
    private final Broadcaster broadcaster;

    /**
     * A new thread for accepting connections.
     * <p>
     * Logic module and broadcaster must have been initialized
     * </p>
     *
     * @param logic Logic module
     * @param broadcaster Server broadcaster
     */
    public ConnectionThread(LogicModule logic, Broadcaster broadcaster) {
        this.logic = logic;
        this.broadcaster = broadcaster;
    }

    @Override
    public void run() {
        ExecutorService tpes = Executors.newCachedThreadPool();
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket(Globals.SERVER_PORT, InetAddress.getByName("0.0.0.0"));
            System.out.println("Server listening on port " + Globals.SERVER_PORT);
            broadcaster.setSocket(socket);
            while (true) {
                byte[] request = new byte[Globals.PACKET_MAX_SIZE];
                DatagramPacket packet = new DatagramPacket(request, request.length);
                try {
                    socket.receive(packet);
                    tpes.execute(new PacketHandler(broadcaster, packet, logic));
                } catch (IOException ex) {
                    Globals.log(ex.getLocalizedMessage(), ex, true);
                }
            }
        } catch (SocketException | UnknownHostException ex) {
            logic.shutdown();
            Globals.log(ex.getLocalizedMessage(), ex, true);
        }
    }
}
