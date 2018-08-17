package blockfighter.client.net.hub.http;

import blockfighter.client.Core;
import blockfighter.client.screen.ScreenServerList;
import blockfighter.shared.Globals;
import blockfighter.shared.ServerInfo;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public class HubServerInfoGetter {

    private static ServerInfo[] serverList;
    private static long lastSendTime;

    public static ServerInfo[] getServerList() {
        return serverList;
    }

    public static void sendGetServerInfos() {
        if (Core.getLogicModule().getTime() - lastSendTime > Globals.msToNs(1500)) {
            ArrayList<ServerInfo> res = new ArrayList<>();
            lastSendTime = Core.getLogicModule().getTime();
            try {
                URL ipURL = new URL("http://" + Globals.ServerConfig.HUB_SERVER_ADDRESS.getValue() + ":" + Globals.ServerConfig.HUB_SERVER_TCP_PORT.getValue() + "/get-list");
                Globals.log(HubServerInfoGetter.class, "Getting server list from " + ipURL, Globals.LOG_TYPE_DATA);
                JSONArray serverListJson = new JSONArray(IOUtils.toString(ipURL, "UTF-8"));
                serverListJson.forEach((object) -> {
                    JSONObject jsonObj = (JSONObject) object;
                    res.add(new ServerInfo(jsonObj));
                });
            } catch (IOException ex) {
                Globals.log(HubServerInfoGetter.class, "Failed to get server list.", Globals.LOG_TYPE_ERR);
                if (Core.getLogicModule().getScreen() instanceof ScreenServerList) {
                    ((ScreenServerList) Core.getLogicModule().getScreen()).setStatus(ScreenServerList.STATUS_REFRESHING_FAILED);
                }
                serverList = null;
                return;
            }
            serverList = res.toArray(new ServerInfo[0]);
        }
    }
}
