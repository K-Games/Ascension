package blockfighter.server.net.hub.tcp;

import blockfighter.server.AscensionServer;
import blockfighter.shared.Globals;
import blockfighter.shared.data.net.hub.ServerInfo;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class HubSender {

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

    public static void sendServerInfo() {
        if (PUBLIC_IP == null || COUNTRY == null || AREA == null) {
            fetchServerInfo();
        }
        ServerInfo info = new ServerInfo(PUBLIC_IP, COUNTRY + ", " + AREA, AscensionServer.getServerCapacityStatus(),
                (Integer) Globals.ServerConfig.TCP_PORT.getValue(), (Integer) Globals.ServerConfig.UDP_PORT.getValue());
        HubClient.getClient().sendTCP(info);
    }
}
