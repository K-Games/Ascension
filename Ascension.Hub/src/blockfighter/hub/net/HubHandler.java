package blockfighter.hub.net;

import blockfighter.hub.HubModule;
import blockfighter.shared.ServerInfo;
import blockfighter.shared.Globals;
import com.esotericsoftware.kryonet.Connection;

public class HubHandler {

    public static void process(byte[] data, Connection c) {
        final byte dataType = data[0];

        switch (dataType) {
            case Globals.HUB_DATA_GET_SERVERINFOS:
                HubSender.sendServerInfos(c);
                break;
            default:
                Globals.log(HubHandler.class, "DATA_UNKNOWN " + c + " Unknown data type: " + dataType, Globals.LOG_TYPE_DATA, true);
                break;
        }
    }

    public static void process(ServerInfo data, Connection c) {
        HubModule.addServerInfo(c, data);
    }
}
