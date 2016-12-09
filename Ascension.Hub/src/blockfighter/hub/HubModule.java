package blockfighter.hub;

import blockfighter.hub.net.HubSender;
import blockfighter.hub.net.HubServer;
import blockfighter.shared.Globals;
import blockfighter.shared.ServerInfo;
import com.esotericsoftware.kryonet.Connection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

public class HubModule implements Runnable {

    private static final ConcurrentHashMap<Connection, ServerInfo> CONN_SERVERINFO_MAP = new ConcurrentHashMap<>();
    private final static ScheduledExecutorService GET_SERVERSTAT_SCHEDULER = Executors.newScheduledThreadPool(5, new BasicThreadFactory.Builder()
            .namingPattern("GET_SERVERSTAT-%d")
            .daemon(true)
            .priority(Thread.NORM_PRIORITY)
            .build());

    public static void addServerInfo(final Connection c, final ServerInfo info) {
        c.setKeepAliveTCP(1000);
        c.setTimeout(10000);
        if (!CONN_SERVERINFO_MAP.containsKey(c)) {
            Globals.log(HubServer.class, "Added " + info + " to server list", Globals.LOG_TYPE_DATA, true);
        }
        CONN_SERVERINFO_MAP.put(c, info);
    }

    public static ServerInfo[] getServerInfos() {
        ServerInfo[] infos = new ServerInfo[0];
        return CONN_SERVERINFO_MAP.values().toArray(infos);
    }

    public static void removeServerInfo(final Connection c) {
        if (CONN_SERVERINFO_MAP.containsKey(c)) {
            ServerInfo info = CONN_SERVERINFO_MAP.remove(c);
            Globals.log(HubServer.class, "Removed " + info + " from server list", Globals.LOG_TYPE_DATA, true);
        }
    }

    @Override
    public void run() {
        for (final Map.Entry<Connection, ServerInfo> infoEntry : CONN_SERVERINFO_MAP.entrySet()) {
            Connection c = infoEntry.getKey();
            GET_SERVERSTAT_SCHEDULER.schedule(() -> {
                HubSender.sendGetServerInfo(c);
            }, 0, TimeUnit.SECONDS);
        }
    }

}
