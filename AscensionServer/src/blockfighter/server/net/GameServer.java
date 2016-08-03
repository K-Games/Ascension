package blockfighter.server.net;

import blockfighter.server.Globals;
import blockfighter.server.entities.player.Player;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class GameServer {

    public static ConcurrentHashMap<Connection, Player> connectionPlayerMap = new ConcurrentHashMap<>();

    private Server server;
    private PacketReceiver receiver;

    public void start() {
        try {
            this.server = new Server(Globals.PACKET_MAX_SIZE * Globals.PACKET_MAX_PER_CON, Globals.PACKET_MAX_SIZE);
            Kryo kyro = this.server.getKryo();
            kyro.register(byte[].class);
            this.receiver = new PacketReceiver();
            this.server.addListener(new Listener.ThreadedListener(this.receiver));
            server.bind(Globals.SERVER_TCP_PORT);
            server.start();
            Globals.log(GameServer.class, "Server listening on port TCP: " + Globals.SERVER_TCP_PORT, Globals.LOG_TYPE_DATA, true);
            //Globals.log(GameServer.class, "Server listening on port UDP: " + Globals.SERVER_UDP_PORT, Globals.LOG_TYPE_DATA, true);
        } catch (IOException ex) {
            Globals.logError(ex.getLocalizedMessage(), ex, true);
            System.exit(1);
        }
    }

    public static void addPlayerConnection(final Connection c, final Player p) {
        c.setKeepAliveTCP(1500);
        c.setTimeout(15000);
        connectionPlayerMap.put(c, p);
    }

    public static Player getPlayerFromConnection(final Connection c) {
        return connectionPlayerMap.get(c);
    }

    public static void removeConnectionPlayer(final Connection c) {
        connectionPlayerMap.remove(c);
    }
}
