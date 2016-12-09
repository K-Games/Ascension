package blockfighter.hub.net;

import blockfighter.shared.ServerInfo;
import blockfighter.shared.Globals;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import java.io.IOException;

public class HubServer {

    private Server server;

    public void start() {
        try {
            this.server = new Server();
            Kryo kyro = this.server.getKryo();

            kyro.register(byte[].class);
            kyro.register(ServerInfo.class);
            kyro.register(ServerInfo[].class);

            this.server.addListener(new Listener.ThreadedListener(new HubReceiver()));
            server.bind(Globals.HUB_SERVER_TCP_PORT);
            Globals.log(HubServer.class, "Hub Server listening on port TCP: " + Globals.HUB_SERVER_TCP_PORT, Globals.LOG_TYPE_DATA, true);
            server.start();
        } catch (IOException ex) {
            Globals.logError(ex.toString(), ex, true);
            System.exit(1);
        }
    }

    public void shutdown() {
        server.stop();
    }

}
