package blockfighter.server.net;

import blockfighter.server.Globals;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

/**
 * Thread to accept incoming connections. Start only one in the server. An instance of this class should not be required to be referenced at any time.
 *
 * @author Ken Kwan
 */
public class PacketReceiver extends Listener {

    @Override
    public void received(Connection c, Object object) {
        if (object instanceof byte[]) {
            PacketHandler.process((byte[]) object, c);
        }
    }

    @Override
    public void disconnected(Connection c) {
        try {
            if (GameServer.getPlayerFromConnection(c) != null) {
                GameServer.getPlayerFromConnection(c).disconnect();
            }
        } catch (Exception e) {
            Globals.log(PacketReceiver.class, "Exception while disconnecting a connection", Globals.LOG_TYPE_ERR, true);
        }
    }
}
