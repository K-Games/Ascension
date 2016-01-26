package blockfighter.server.net;

import blockfighter.server.Globals;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

/**
 * Thread to accept incoming connections. Start only one in the server. An instance of this class should not be required to be referenced at any time.
 *
 * @author Ken Kwan
 */
public class PacketReceiver extends Thread {

    @Override
    public void run() {
        ExecutorService threadPool = new ThreadPoolExecutor(1, 3,
                10L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(),
                new BasicThreadFactory.Builder()
                .namingPattern("PacketHandlerExecutor-%d")
                .daemon(true)
                .priority(Thread.NORM_PRIORITY)
                .build());
        try {
            DatagramSocket socket = new DatagramSocket(Globals.SERVER_PORT);
            System.out.println("Server listening on port " + Globals.SERVER_PORT);
            PacketSender.setSocket(socket);
            while (true) {
                byte[] request = new byte[Globals.PACKET_MAX_SIZE];
                DatagramPacket packet = new DatagramPacket(request, request.length);
                socket.receive(packet);
                threadPool.execute(new PacketHandler(packet));
            }
        } catch (SocketException ex) {
            Globals.log(ex.getLocalizedMessage(), ex, true);
        } catch (IOException ex) {
            Globals.log(ex.getLocalizedMessage(), ex, true);
        }
    }
}
