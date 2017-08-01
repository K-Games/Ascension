package blockfighter.server.net;

import blockfighter.server.LogicModule;
import blockfighter.server.entities.player.Player;
import blockfighter.shared.AscensionSerialization;
import blockfighter.shared.Globals;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class GameServer {

    private static final ConcurrentHashMap<Connection, Player> CONN_PLAYER_MAP = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Connection, RoomPlayerKey> CONN_PLAYER_KEY_MAP = new ConcurrentHashMap<>();

    private Server server;
    private PacketReceiver receiver;

    public void start() {
        try {
            this.server = new Server(Globals.PACKET_MAX_SIZE * (Byte) Globals.ServerConfig.MAX_PACKETS_PER_CON.getValue(), Globals.PACKET_MAX_SIZE, new AscensionSerialization());
            this.receiver = new PacketReceiver();
            this.server.addListener(new Listener.ThreadedListener(this.receiver));
            if ((Boolean) Globals.ServerConfig.UDP_MODE.getValue()) {
                server.bind((Integer) Globals.ServerConfig.TCP_PORT.getValue(), (Integer) Globals.ServerConfig.UDP_PORT.getValue());
                Globals.log(GameServer.class, "Server listening on port TCP: " + (Integer) Globals.ServerConfig.TCP_PORT.getValue(), Globals.LOG_TYPE_DATA);
                Globals.log(GameServer.class, "Server listening on port UDP: " + (Integer) Globals.ServerConfig.UDP_PORT.getValue(), Globals.LOG_TYPE_DATA);
            } else {
                server.bind((Integer) Globals.ServerConfig.TCP_PORT.getValue());
                Globals.log(GameServer.class, "Server listening on port TCP: " + (Integer) Globals.ServerConfig.TCP_PORT.getValue(), Globals.LOG_TYPE_DATA);
            }
            server.start();

        } catch (IOException ex) {
            Globals.logError(ex.toString(), ex);
            System.exit(1);
        }
    }

    public static void addPlayerConnection(final Connection c, final Player p) {
        c.setKeepAliveTCP(1500);
        c.setTimeout(15000);
        CONN_PLAYER_MAP.put(c, p);
    }

    public static Player getPlayerFromConnection(final Connection c) {
        return CONN_PLAYER_MAP.get(c);
    }

    public static void removeConnectionPlayer(final Connection c) {
        CONN_PLAYER_MAP.remove(c);
    }

    public static byte getPlayerKeyFromConnection(final Connection c) {
        return CONN_PLAYER_KEY_MAP.get(c).playerKey;
    }

    public static void removeConnectionPlayerKey(final Connection c) {
        if (CONN_PLAYER_KEY_MAP.containsKey(c)) {
            RoomPlayerKey rpk = CONN_PLAYER_KEY_MAP.remove(c);
            rpk.room.getRoomData().returnPlayerKey(rpk.playerKey);
        }
    }

    public static void addPlayerKeyConnection(final Connection c, final LogicModule room, final byte key) {
        CONN_PLAYER_KEY_MAP.put(c, new RoomPlayerKey(room, key));
    }

    public void shutdown() {
        server.stop();
    }

    private static class RoomPlayerKey {

        private LogicModule room;
        private Byte playerKey;

        public RoomPlayerKey(final LogicModule room, final byte key) {
            this.room = room;
            this.playerKey = key;
        }
    }
}
