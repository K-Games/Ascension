package blockfighter.server.net;

import blockfighter.server.Globals;
import blockfighter.server.LogicModule;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Thread to accept incoming connections.
 *
 * @author Ken
 */
public class PacketReceiver extends Thread {

    private final LogicModule[] logic;
    private final PacketSender packetSender;

    /**
     * A new thread for accepting connections.
     * <p>
     * Logic module and packetSender must have been initialized
     * </p>
     *
     * @param logic Logic module
     * @param packetSender Server packetSender
     */
    public PacketReceiver(LogicModule[] logic, PacketSender packetSender) {
        this.logic = logic;
        this.packetSender = packetSender;
    }

    @Override
    public void run() {
        ExecutorService tpes = Executors.newFixedThreadPool(10);
        try {
            DatagramSocket socket = new DatagramSocket(Globals.SERVER_PORT);
            System.out.println("Server listening on port " + Globals.SERVER_PORT);
            packetSender.setSocket(socket);
            while (true) {
                byte[] request = new byte[Globals.PACKET_MAX_SIZE];
                DatagramPacket packet = new DatagramPacket(request, request.length);
                socket.receive(packet);
                tpes.execute(new PacketHandler(packetSender, packet, logic));
            }
        } catch (SocketException ex) {
            Globals.log(ex.getLocalizedMessage(), ex, true);
        } catch (IOException ex) {
            Globals.log(ex.getLocalizedMessage(), ex, true);
        } finally {
            for (LogicModule l : logic) {
                l.shutdown();
            }
            tpes.shutdownNow();
        }
    }
}
