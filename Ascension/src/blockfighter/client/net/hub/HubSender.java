package blockfighter.client.net.hub;

import blockfighter.shared.Globals;

public class HubSender {

    public static void sendGetServerInfos() {
        Globals.log(HubSender.class, "Sending Server List request to Hub Server...", Globals.LOG_TYPE_DATA);
        byte[] data = new byte[Globals.PACKET_BYTE];
        data[0] = Globals.HUB_DATA_GET_SERVERINFOS;
        HubClient.getClient().sendTCP(data);
    }
}
