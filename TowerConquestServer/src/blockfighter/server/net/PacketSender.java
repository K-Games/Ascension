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
import java.util.concurrent.Executors;

/**
 * The server packetSender.
 * <p>
 * The packet sender does not create the datagram packet. It is just a thread to send data. Datagram packet is created outside this class. Sends data to players. Only one is created on the server.
 * Client side packet sender DOES construct the datagram packet.
 * </p>
 *
 * @author Ken Kwan
 */
public class PacketSender {

    private static LogicModule[] logic;
    private DatagramSocket socket = null;
    private int bytesSent = 0;
    private ConcurrentLinkedQueue<DatagramPacket> sendAllQueue = new ConcurrentLinkedQueue<>();

    private static ExecutorService threadPool = Executors.newScheduledThreadPool(15);

    public void processSendQueue() {
        while (!sendAllQueue.isEmpty()) {
            final DatagramPacket p = sendAllQueue.poll();
            if (p != null) {
                threadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            socket.send(p);
                        } catch (IOException ex) {
                            Globals.log(ex.getLocalizedMessage(), ex, true);
                        }
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
     * Set the static logic module array
     *
     * @param l Logic Module array
     */
    public static void setLogic(LogicModule[] l) {
        logic = l;
    }

    /**
     * Set the socket of the server.
     *
     * @param socket The DatagramSocket that was initialized at the start
     */
    public void setSocket(DatagramSocket socket) {
        this.socket = socket;
    }

    /**
     * Send data to specific player.
     * <p>
     * Get IP address and port from the player object.
     * </p>
     *
     * @param bytes Data to be sent in bytes
     * @param address IP address of player
     * @param port Connected port of player
     */
    public void sendPlayer(final byte[] bytes, final InetAddress address, final int port) {
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    DatagramPacket packet = createPacket(bytes, address, port);
                    socket.send(packet);
                    //bytesSent += bytes.length;
                } catch (IOException ex) {
                    Globals.log(ex.getLocalizedMessage(), ex, true);
                }
            }
        });
    }

    /**
     * Send data to every connected player
     *
     * @param bytes Data to be sent in bytes
     * @param room room to send to
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
     * Broadcast an update to all players about all players.
     *
     * @param room Room which is broadcasting all player update
     */
    public void broadcastAllPlayersUpdate(byte room) {
        for (Map.Entry<Byte, Player> pEntry : logic[room].getPlayers().entrySet()) {
            Player player = pEntry.getValue();
            player.sendPos();
            player.sendState();
            player.sendFacing();
        }
    }

    private DatagramPacket createPacket(byte[] bytes, InetAddress address, int port) {
        return new DatagramPacket(bytes, bytes.length, address, port);
    }

    private DatagramPacket createPacket(byte[] bytes, Player p) {
        return createPacket(bytes, p.getAddress(), p.getPort());
    }

}
