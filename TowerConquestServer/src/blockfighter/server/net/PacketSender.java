package blockfighter.server.net;

import blockfighter.server.Globals;
import blockfighter.server.LogicModule;
import blockfighter.server.entities.player.Player;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

/**
 * The server Packet Sender.
 * <p>
 * <div>Sends bytes packaged in a DatagramPacket via a DatagramSocket.</div>
 * <p>
 * The Packet Sender does not create the datagram packet. DatagramPackets are created outside this class. Client-side Packet Sender <b>DOES</b> construct the datagram packet. </p>
 * <div>Only one PacketSender is created on the server during program entry.</div>
 *
 * </p>
 *
 * @author Ken Kwan
 */
public class PacketSender implements Runnable {

    private static LogicModule[] logic;
    private static DatagramSocket socket = null;
    private int bytesSent = 0;
    private ConcurrentLinkedQueue<DatagramPacket> sendAllQueue = new ConcurrentLinkedQueue<>();

    private static ExecutorService senderThreadPool;

    public static void init() {
        senderThreadPool = new ThreadPoolExecutor(0, Globals.SERVER_PACKETSENDER_THREADS,
                10L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(),
                new BasicThreadFactory.Builder()
                .namingPattern("PacketSender-%d")
                .daemon(true)
                .priority(Thread.MAX_PRIORITY)
                .build());
    }

    @Override
    public void run() {
        while (!sendAllQueue.isEmpty()) {
            final DatagramPacket p = sendAllQueue.poll();
            if (p != null) {
                senderThreadPool.execute(() -> {
                    try {
                        socket.send(p);
                    } catch (IOException ex) {
                        Globals.log(ex.getLocalizedMessage(), ex, true);
                    }
                });
            }
        }
    }

    /**
     * Reset sent byte count.
     */
    public void resetByte() {
        bytesSent = 0;
    }

    /**
     * Return number of bytes sent so far
     *
     * @return Number of bytes sent
     */
    public int getBytes() {
        return bytesSent;
    }

    /**
     * Set the static Logic Module array
     *
     * @param l Logic Module array
     */
    public static void setLogic(LogicModule[] l) {
        logic = l;
    }

    /**
     * Set the socket of the server.
     *
     * @param s The DatagramSocket that was initialized in the Packet Receiver
     */
    public static void setSocket(DatagramSocket s) {
        socket = s;
    }

    /**
     * Send bytes to a specific player.
     * <p>
     * Get destination IP address and port from the Player object.
     * </p>
     *
     * @param bytes Data to be sent in byte array
     * @param address IP address of destination player
     * @param port Port of destination player
     */
    public void sendPlayer(final byte[] bytes, final InetAddress address, final int port) {
        senderThreadPool.execute(() -> {
            try {
                DatagramPacket packet = createPacket(bytes, address, port);
                socket.send(packet);
                //bytesSent += bytes.length;
            } catch (IOException ex) {
                Globals.log(ex.getLocalizedMessage(), ex, true);
            }
        });
    }

    /**
     * Queue bytes to be sent to every connected player.
     *
     * @param bytes Data to be sent in byte array
     * @param room Room(Logic Module index) that contains the players
     */
    public void sendAll(final byte[] bytes, final byte room) {
        for (Map.Entry<Byte, Player> pEntry : logic[room].getPlayers().entrySet()) {
            DatagramPacket packet = createPacket(bytes, pEntry.getValue());
            sendAllQueue.add(packet);
            //socket.send(packet);
            //bytesSent += bytes.length;
        }
    }

    /**
     * Broadcast an update to all players about all player's data.
     *
     * @param room Room(Logic Module index) which is broadcasting the all player update.
     */
    public void broadcastAllPlayersUpdate(byte room) {
        for (Map.Entry<Byte, Player> pEntry : logic[room].getPlayers().entrySet()) {
            Player player = pEntry.getValue();
            player.sendData();
        }
    }

    private DatagramPacket createPacket(byte[] bytes, InetAddress address, int port) {
        return new DatagramPacket(bytes, bytes.length, address, port);
    }

    private DatagramPacket createPacket(byte[] bytes, Player p) {
        return createPacket(bytes, p.getAddress(), p.getPort());
    }

}
