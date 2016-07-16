package blockfighter.server.net;

import blockfighter.server.Globals;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * Thread to accept incoming connections. Start only one in the server. An instance of this class should not be required to be referenced at any time.
 *
 * @author Ken Kwan
 */
public class PacketReceiver extends Thread {

    private PacketHandler packetHandler;

    public PacketReceiver(PacketHandler ph) {
        this.packetHandler = ph;
    }

    @Override
    public void run() {
        try {
            final DatagramSocket socket = new DatagramSocket(Globals.SERVER_PORT);
            Globals.log(PacketReceiver.class, "Server listening on port " + Globals.SERVER_PORT, Globals.LOG_TYPE_DATA, true);
            PacketSender.setSocket(socket);
            while (true) {
                final byte[] request = new byte[Globals.PACKET_MAX_SIZE];
                final DatagramPacket packet = new DatagramPacket(request, request.length);
                socket.receive(packet);
                packetHandler.queuePacket(packet);
            }
        } catch (final SocketException ex) {
            Globals.logError(ex.getLocalizedMessage(), ex, true);
        } catch (final IOException ex) {
            Globals.logError(ex.getLocalizedMessage(), ex, true);
        }
    }
}
