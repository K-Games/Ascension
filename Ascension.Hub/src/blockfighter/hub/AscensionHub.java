package blockfighter.hub;

import blockfighter.hub.net.HubServer;
import blockfighter.shared.Globals;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

public class AscensionHub {

    private final static String RELEASE_VERSION = "0.1.1";

    private final static ScheduledExecutorService HUB_SCHEDULER = Executors.newSingleThreadScheduledExecutor(new BasicThreadFactory.Builder()
            .namingPattern("HUB-%d")
            .daemon(false)
            .priority(Thread.NORM_PRIORITY)
            .build());

    public static void main(String[] args) {
        Globals.LOGGING = true;
        Globals.createLogDirectory();
        if (args.length > 0) {
            for (int i = 0; i < args.length; i++) {
                switch (args[i].toLowerCase()) {
                    case "-port":
                        try {
                            int port = Integer.parseInt(args[i + 1]);
                            if (port > 0 && port <= 65535) {
                                Globals.ServerConfig.HUB_SERVER_TCP_PORT.setValue(args[i + 1]);
                                Globals.log(AscensionHub.class, "Setting Hub Server TCP port to " + (Integer) Globals.ServerConfig.HUB_SERVER_TCP_PORT.getValue(), Globals.LOG_TYPE_DATA);
                            } else {
                                System.err.println("-port Specify a valid port between 1 to 65535");
                                System.exit(7);
                            }
                        } catch (Exception e) {
                            System.err.println("-port Specify a valid port between 1 to 65535");
                            System.exit(8);
                        }
                        break;
                }
            }
        }
        Globals.log(AscensionHub.class, "Ascension Server Hub Version " + RELEASE_VERSION, Globals.LOG_TYPE_DATA);
        new HubServer().start();
        HUB_SCHEDULER.scheduleAtFixedRate(new HubModule(), 0, 8, TimeUnit.SECONDS);
    }

}
