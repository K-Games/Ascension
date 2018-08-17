package blockfighter.server.net.hub.http;

import blockfighter.server.AscensionServer;
import blockfighter.shared.Globals;
import blockfighter.shared.ServerInfo;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONObject;

public class HubServerInfoSender implements Runnable {

    private static String COUNTRY = null, AREA = null, PUBLIC_IP = null;

    private static void fetchServerInfo() {
        try {
            URL ipURL = new URL("http://checkip.amazonaws.com");
            InputStreamReader reader = new InputStreamReader(ipURL.openStream());
            try (BufferedReader in = new BufferedReader(reader)) {
                PUBLIC_IP = in.readLine();
                URL regionURL = new URL("http://ip-api.com/line/" + PUBLIC_IP + "?fields=country,regionName");
                InputStreamReader regionReader = new InputStreamReader(regionURL.openStream());
                try (BufferedReader regionIn = new BufferedReader(regionReader)) {
                    COUNTRY = regionIn.readLine();
                    AREA = regionIn.readLine();
                }
            }
        } catch (IOException ex) {
            Globals.logError(ex.toString(), ex);
        }
    }

    @Override
    public void run() {
        if (PUBLIC_IP == null || COUNTRY == null || AREA == null) {
            fetchServerInfo();
        }
        ServerInfo info = null;
        try {
            info = new ServerInfo(PUBLIC_IP, COUNTRY + ", " + AREA, AscensionServer.getServerCapacityStatus(),
                    (Integer) Globals.ServerConfig.TCP_PORT.getValue(), (Integer) Globals.ServerConfig.UDP_PORT.getValue());
        } catch (Exception ex) {
            Globals.logError("Failed to create server info.", ex);
            return;
        }

        HttpPost post = new HttpPost("http://" + Globals.ServerConfig.HUB_SERVER_ADDRESS.getValue() + ":" + Globals.ServerConfig.HUB_SERVER_TCP_PORT.getValue() + "/server-info");
        JSONObject json = new JSONObject(info);
        StringEntity entity = new StringEntity(json.toString(), "UTF-8");
        post.setEntity(entity);
        post.setHeader("Accept", "application/json");
        post.setHeader("Content-type", "application/json");

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            client.execute(post);
        } catch (IOException ex) {
            Globals.log(HubServerInfoSender.class, "Failed to send server info", Globals.LOG_TYPE_DATA);
        }

    }
}
