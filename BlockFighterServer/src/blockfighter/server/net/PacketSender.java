package blockfighter.server.net;

import blockfighter.server.Globals;
import blockfighter.server.LogicModule;
import blockfighter.server.entities.player.Player;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * The server packetSender.
 * <p>
 * Sends data to players. Only one is created on the server.
 * </p>
 *
 * @author Ken
 */
public class PacketSender {

    private final LogicModule logic;
    private DatagramSocket socket = null;
    private int bytesSent = 0;

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
     * Create a packetSender to send bytes to connected players.
     * <p>
     * Server should only have one packetSender. The packetSender should always be referenced after construction and never initialized again.
     * </p>
     *
     * @param logic
     */
    public PacketSender(LogicModule logic) {
        this.logic = logic;
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
    public void sendPlayer(byte[] bytes, InetAddress address, int port) {
        DatagramPacket packet = createPacket(bytes, address, port);
        bytesSent += packet.getLength();
        try {
            socket.send(packet);
        } catch (IOException ex) {
            Globals.log(ex.getLocalizedMessage(), ex, true);
        }
    }

    /**
     * Send data to every connected player
     *
     * @param bytes Data to be sent in bytes
     */
    public void sendAll(byte[] bytes) {
        //tell everyone
        for (Player p : logic.getPlayers()) {
            if (p != null) {
                DatagramPacket packet = createPacket(bytes, p);
                bytesSent += packet.getLength();
                try {
                    socket.send(packet);
                } catch (IOException ex) {
                    Globals.log(ex.getLocalizedMessage(), ex, true);
                }
            }
        }
    }

    /**
     * Broadcast an update to all players about all players.
     */
    public void broadcastAllPlayersUpdate() {
        Player[] players = logic.getPlayers();
        for (Player player : players) {
            if (player == null) {
                continue;
            }
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
