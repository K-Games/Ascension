package blockfighter.server.net;

import blockfighter.server.entities.player.Player;
import com.esotericsoftware.kryonet.Connection;

public class GamePacket {

    private final Connection c;
    private final Player player;
    private final byte[] data;

    public GamePacket(final byte[] data, final Player p) {
        this.data = data;
        this.player = p;
        this.c = p.getConnection();
    }

    public GamePacket(final byte[] data, final Connection c) {
        this.data = data;
        this.player = null;
        this.c = c;
    }

    /**
     * @return the c
     */
    public Connection getConnection() {
        return c;
    }

    /**
     * @return the player
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * @return the data
     */
    public byte[] getData() {
        return data;
    }

}
