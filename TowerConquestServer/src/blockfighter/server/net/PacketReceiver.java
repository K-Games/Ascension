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
 * @author Ken Kwan
 */
public class PacketReceiver extends Thread {

    private static LogicModule[] logic;
    private static PacketSender sender;

    /**
     * Set the static logic module array
     *
     * @param l Logic Module array
     */
    public static void setLogic(LogicModule[] l) {
        logic = l;
    }

    /**
     * Set the static packet sender
     *
     * @param ps Server PacketSender
     */
    public static void setPacketSender(PacketSender ps) {
        sender = ps;
    }

    @Override
    public void run() {
        ExecutorService tpes = Executors.newFixedThreadPool(10);
        try {
            DatagramSocket socket = new DatagramSocket(Globals.SERVER_PORT);
            System.out.println("Server listening on port " + Globals.SERVER_PORT);
            sender.setSocket(socket);
            sender.setThreadPool(tpes);
            while (true) {
                byte[] request = new byte[Globals.PACKET_MAX_SIZE];
                DatagramPacket packet = new DatagramPacket(request, request.length);
                socket.receive(packet);
                tpes.execute(new PacketHandler(packet));
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
