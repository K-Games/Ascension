package blockfighter.client.net.hub.tcp;

import blockfighter.client.Core;
import blockfighter.client.screen.ScreenServerList;
import blockfighter.shared.Globals;
import blockfighter.shared.ServerInfo;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Listener;
import java.io.IOException;
import java.net.InetAddress;

public class HubClient implements Runnable {

    private static Client client;
    private static ServerInfo[] serverInfo;

    @Override
    public void run() {
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
            serverInfo = null;
            if (!client.isConnected()) {
                client.connect(2000, InetAddress.getByName((String) Globals.ServerConfig.HUB_SERVER_ADDRESS.getValue()), (Integer) Globals.ServerConfig.HUB_SERVER_TCP_PORT.getValue());
            }
            HubSender.sendGetServerInfos();
        } catch (IOException ex) {
            client.close();
            if (Core.getLogicModule().getScreen() instanceof ScreenServerList) {
                ((ScreenServerList) Core.getLogicModule().getScreen()).setStatus(ScreenServerList.STATUS_REFRESHING_FAILED);
            }
        }
    }

    public static Client getClient() {
        return client;
    }

    public static void setServerInfo(final ServerInfo[] info) {
        serverInfo = info;
    }

    public static ServerInfo[] getServerInfo() {
        return serverInfo;
    }
}
