package blockfighter.server.net.hub;

import blockfighter.server.AscensionServer;
import blockfighter.shared.Globals;
import blockfighter.shared.ServerInfo;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class HubSender {

    public static void sendServerInfo() {
        BufferedReader in;
        try {
            URL ipURL = new URL("http://checkip.amazonaws.com");
            in = new BufferedReader(new InputStreamReader(ipURL.openStream()));
            String ip = in.readLine();

            String country = "Unknown", area = "Unknown";
            try {
                URL regionURL = new URL("http://ip-api.com/line/" + ip + "?fields=country,regionName");
                in = new BufferedReader(new InputStreamReader(regionURL.openStream()));
                country = in.readLine();
                area = in.readLine();
            } catch (IOException ex) {
                Globals.logError("Failed to get country name.", ex);
            }

            ServerInfo info = new ServerInfo(ip, area + ", " + country, AscensionServer.getServerCapacityStatus());
            HubClient.getClient().sendTCP(info);
        } catch (IOException ex) {
            Globals.logError(ex.toString(), ex);
        }
    }
}
