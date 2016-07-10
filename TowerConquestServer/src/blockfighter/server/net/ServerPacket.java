package blockfighter.server.net;

import blockfighter.server.entities.player.Player;
import java.net.DatagramPacket;
import java.net.InetAddress;

/**
 *
 * @author Ken Kwan
 */
public class ServerPacket {

    private final DatagramPacket packet;
    private final Player destPlayer;
    private final InetAddress destAddress;

    public ServerPacket(final DatagramPacket data, final Player p) {
        this.packet = data;
        this.destPlayer = p;
        this.destAddress = p.getAddress();
    }

    public ServerPacket(final DatagramPacket data, final InetAddress address) {
        this.packet = data;
        this.destPlayer = null;
        this.destAddress = address;
    }

    public DatagramPacket getDatagram() {
        return this.packet;
    }

    public InetAddress getAddress() {
        return this.destAddress;
    }

    public Player getPlayer() {
        return destPlayer;
    }
}
