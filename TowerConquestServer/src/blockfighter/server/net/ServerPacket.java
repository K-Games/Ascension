package blockfighter.server.net;

import blockfighter.server.entities.player.Player;
import java.net.DatagramPacket;

/**
 *
 * @author Ken Kwan
 */
public class ServerPacket {

    private final DatagramPacket packet;
    private final Player destPlayer;

    public ServerPacket(final DatagramPacket data, final Player p) {
        this.packet = data;
        this.destPlayer = p;
    }

    public DatagramPacket getDatagram() {
        return this.packet;
    }

    public Player getPlayer() {
        return destPlayer;
    }
}
