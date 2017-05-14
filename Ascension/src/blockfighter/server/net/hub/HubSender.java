package blockfighter.server.net.hub;

import blockfighter.server.AscensionServer;
import blockfighter.shared.Globals;
import blockfighter.shared.ServerInfo;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import org.apache.commons.io.IOUtils;

public class HubSender {

    private static String COUNTRY = null, AREA = null, PUBLIC_IP = null;

    private static void fetchServerInfo() {
        BufferedReader in = null;
        try {
            URL ipURL = new URL("http://checkip.amazonaws.com");
            in = new BufferedReader(new InputStreamReader(ipURL.openStream()));
            PUBLIC_IP = in.readLine();
            try {
                URL regionURL = new URL("http://ip-api.com/line/" + PUBLIC_IP + "?fields=country,regionName");
                in = new BufferedReader(new InputStreamReader(regionURL.openStream()));
                COUNTRY = in.readLine();
                AREA = in.readLine();
            } catch (IOException ex) {
                Globals.logError("Failed to get country name.", ex);
            }
        } catch (IOException ex) {
            Globals.logError(ex.toString(), ex);
        } finally {
            IOUtils.closeQuietly(in);
        }
    }

    public static void sendServerInfo() {
        if (PUBLIC_IP == null || COUNTRY == null || AREA == null) {
            fetchServerInfo();
        }
        ServerInfo info = new ServerInfo(PUBLIC_IP, COUNTRY + ", " + AREA, AscensionServer.getServerCapacityStatus());
        HubClient.getClient().sendTCP(info);
    }
}
