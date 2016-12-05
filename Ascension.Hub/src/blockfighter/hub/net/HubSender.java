package blockfighter.hub.net;

import blockfighter.hub.HubModule;
import blockfighter.shared.Globals;
import com.esotericsoftware.kryonet.Connection;

public class HubSender {

    public static void sendServerInfos(Connection c) {
        Globals.log(HubServer.class, "Sending server list " + c, Globals.LOG_TYPE_DATA, true);
        c.sendTCP(HubModule.getServerInfos());
    }

    public static void sendGetServerInfo(Connection c) {
        byte[] data = new byte[Globals.PACKET_BYTE];
        data[0] = Globals.HUB_DATA_GET_SERVERSTATS;
        c.sendTCP(data);
    }
}
