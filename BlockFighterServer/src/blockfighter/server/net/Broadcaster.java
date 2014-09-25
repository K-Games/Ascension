package blockfighter.server.net;

import blockfighter.server.LogicModule;
import blockfighter.server.entities.Player;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * The server broadcaster. 
 * <p>
 * Sends data to players. Only one is created on the server.
 * </p>
 * @author Ken
 */
public class Broadcaster {

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
     * Create a broadcaster to send bytes to connected players.
     * <p>
     * Server should only have one broadcaster. The broadcaster should always be referenced after construction and never initialized again.
     * </p>
     *
     * @param logic
     */
    public Broadcaster(LogicModule logic) {
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
     * @param bytes Data to be sent in bytes
     * @param address IP address of player
     * @param port Connected port of player
     */
    public void sendPlayer(byte[] bytes, InetAddress address, int port) {
        DatagramPacket packet = new DatagramPacket(bytes, bytes.length, address, port);
        bytesSent += packet.getLength();
        try {
            socket.send(packet);
        } catch (IOException ex) {
            System.err.println("ServerBroadcast:sendPlayer: " + ex);
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
                DatagramPacket packet = new DatagramPacket(bytes, bytes.length, p.getAddress(), p.getPort());
                bytesSent += packet.getLength();
                try {
                    socket.send(packet);
                } catch (IOException ex) {
                    System.err.println("ServerBroadcast:sendAll: " + ex);
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
}
