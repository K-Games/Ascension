package blockfighter.server.net.hub.tcp;

import blockfighter.shared.Globals;
import com.esotericsoftware.kryonet.Connection;

public class HubHandler {

    public static void process(byte[] data, Connection c) {
        final byte dataType = data[0];

        switch (dataType) {
            case Globals.HUB_DATA_GET_SERVERSTATS:
                HubSender.sendServerInfo();
                break;
            default:
                Globals.log(HubHandler.class, "HUB_DATA_UNKNOWN " + c + " Unknown data type: " + dataType, Globals.LOG_TYPE_DATA);
                break;
        }
    }
}
