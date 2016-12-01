package blockfighter.shared;

import blockfighter.client.AscensionClient;
import blockfighter.server.AscensionServer;
import java.util.Arrays;
import java.util.HashSet;

public class Main {

    public static AscensionServer asc;

    public static void main(final String[] args) {
        final HashSet<String> arguments = new HashSet<>();
        arguments.addAll(Arrays.asList(args));
        if (arguments.contains("-server")) {
            asc = new AscensionServer();
            asc.launch(args);
        }
        if (!arguments.contains("-noclient")) {
            AscensionClient.launch(args);
        }

    }
}
