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
            URL regionURL = new URL("http://ip-api.com/line/" + ip + "?fields=country,regionName");
            in = new BufferedReader(new InputStreamReader(regionURL.openStream()));
            String country = in.readLine(), area = in.readLine();
            ServerInfo info = new ServerInfo(ip, area + ", " + country, AscensionServer.getServerCapacityStatus());
            HubClient.getClient().sendTCP(info);
        } catch (IOException ex) {
            Globals.logError(ex.toString(), ex);
        }
    }
}
