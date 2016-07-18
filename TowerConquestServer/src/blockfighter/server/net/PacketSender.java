package blockfighter.server.net;

import blockfighter.server.Globals;
import blockfighter.server.LogicModule;
import blockfighter.server.entities.player.Player;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.kryonet.Connection;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

/**
 * The server Packet Sender.
 * <p>
 * <div>Sends data packaged in a DatagramPacket via a DatagramSocket.</div>
 * <p>
 * The Packet Sender does not create the datagram packet. DatagramPackets are created outside this class. Client-side Packet Sender
 * <b>DOES</b> construct the datagram packet.
 * </p>
 * <div>Only one PacketSender is created on the server during program entry.</div>
 *
 * </p>
 *
 * @author Ken Kwan
 */
public class PacketSender {

    private static LogicModule[] logic;
    private static Server server;
    private int dataSent = 0;

    private static final ExecutorService SENDER_THREAD_POOL = new ThreadPoolExecutor(Globals.SERVER_PACKETSENDER_THREADS, Globals.SERVER_PACKETSENDER_THREADS,
            10L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(),
            new BasicThreadFactory.Builder()
            .namingPattern("PacketSender-%d")
            .daemon(true)
            .priority(Thread.MAX_PRIORITY)
            .build());

//    @Override
//    public void run() {
//        while (!this.outPacketQueue.isEmpty()) {
//            final ServerPacket p = this.outPacketQueue.poll();
//            if (p != null) {
//                SENDER_THREAD_POOL.execute(() -> {
//                    if ((p.getPlayer() == null && p.getConnection() != null) || p.getPlayer().isConnected()) {
//                        server.sendToTCP(p.getConnection().getID(), p.getData());
//                    }
//                });
//            }
//        }
//    }
    /**
     * Reset sent byte count.
     */
    public void resetByte() {
        this.dataSent = 0;
    }

    /**
     * Return number of data sent so far
     *
     * @return Number of data sent
     */
    public int getBytes() {
        return this.dataSent;
    }

    /**
     * Set the static Logic Module array
     *
     * @param l Logic Module array
     */
    public static void setLogic(final LogicModule[] l) {
        logic = l;
    }

    /**
     * Set the socket of the server.
     *
     * @param s The DatagramSocket that was initialized in the Packet Receiver
     */
    public static void setServer(final Server s) {
        server = s;
    }

    /**
     * Send data to a specific player.
     * <p>
     * Get destination IP address and port from the Player object.
     * </p>
     *
     * @param data Data to be sent in byte array
     * @param c Connection to send to
     */
    public static void sendConnection(final byte[] data, final Connection c) {
        server.sendToTCP(c.getID(), data);
        //this.outPacketQueue.add(new ServerPacket(data, c));
    }

    public static void sendPlayer(final byte[] data, final Player p) {
        if (p.isConnected()) {
            try {
                server.sendToTCP(p.getConnection().getID(), data);
            } catch (Exception e) {
            }
        }
        //this.outPacketQueue.add(new ServerPacket(data, p));
    }

    /**
     * Queue data to be sent to every connected player.
     *
     * @param data Data to be sent in byte array
     * @param room Room(Logic Module index) that contains the players
     */
    public static void sendAll(final byte[] data, final byte room) {
        for (final Map.Entry<Byte, Player> pEntry : logic[Globals.SERVER_ROOMS.get(room)].getPlayers().entrySet()) {
            sendPlayer(data, pEntry.getValue());
        }
//        server.sendToAllTCP(data);
    }

    /**
     * Broadcast an update to all players about all player's data.
     *
     * @param room Room(Logic Module index) which is broadcasting the all player update.
     */
    public static void broadcastAllPlayersUpdate(final byte room) {
        for (final Map.Entry<Byte, Player> pEntry : logic[room].getPlayers().entrySet()) {
            final Player player = pEntry.getValue();
            player.sendData();
        }
    }
}
