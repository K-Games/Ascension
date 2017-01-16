package blockfighter.server.net.hub;

import blockfighter.shared.Globals;
import blockfighter.shared.ServerInfo;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Listener;
import java.io.IOException;

public class HubClient implements Runnable {

    private static Client client;

    public static void connect() {
        if (client == null) {
            client = new Client();
            client.setTimeout(5000);
            client.setKeepAliveTCP(1000);
            client.start();

            Kryo kyro = client.getKryo();

            kyro.register(byte[].class);
            kyro.register(ServerInfo.class);
            kyro.register(ServerInfo[].class);

            client.addListener(new Listener.ThreadedListener(new HubReceiver()));
        }

        try {
            client.connect(2000, Globals.HUB_SERVER_ADDRESS, Globals.HUB_SERVER_TCP_PORT);
            HubSender.sendServerInfo();
            Globals.log(HubClient.class, "Connected to Hub Server " + Globals.HUB_SERVER_ADDRESS, Globals.LOG_TYPE_DATA);
        } catch (IOException ex) {
            client.close();
        }
    }

    @Override
    public void run() {
        if (client == null || !client.isConnected()) {
            Globals.log(HubClient.class, "Connecting to Hub Server " + Globals.HUB_SERVER_ADDRESS, Globals.LOG_TYPE_DATA);
            connect();
        }
    }

    public static Client getClient() {
        return client;
    }

}
