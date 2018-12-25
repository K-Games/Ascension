package blockfighter.client.net.hub.tcp;

import blockfighter.shared.Globals;
import blockfighter.shared.data.net.hub.ServerInfo;
import com.esotericsoftware.kryonet.Connection;

public class HubHandler {

    public static void process(ServerInfo[] data, Connection c) {
        Globals.log(HubSender.class, "Received Server List from Hub Server...", Globals.LOG_TYPE_DATA);
        HubClient.setServerInfo(data);
        HubClient.getClient().close();
    }
}
