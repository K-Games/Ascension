package blockfighter.hub;

import blockfighter.hub.net.HubSender;
import blockfighter.shared.Globals;
import blockfighter.shared.ServerInfo;
import com.esotericsoftware.kryonet.Connection;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.apache.commons.net.util.SubnetUtils;
import org.json.JSONObject;

public class HubModule implements Runnable {

    private static final List<JSONObject> EC2_IPS = new LinkedList<>();
    private static final HashMap<String, SubnetUtils> IP_UTILS = new HashMap<>();
    private static final HashMap<String, String> REGION_NAMES = new HashMap<>();

    private static final ConcurrentHashMap<Connection, ServerInfo> CONN_SERVERINFO_MAP = new ConcurrentHashMap<>();
    private final static ScheduledExecutorService GET_SERVERSTAT_SCHEDULER = Executors.newScheduledThreadPool(2, new BasicThreadFactory.Builder()
            .namingPattern("GET_SERVERSTAT-%d")
            .daemon(true)
            .priority(Thread.NORM_PRIORITY)
            .build());

    static {
        long start = System.currentTimeMillis();
        try {
            URL ipURL = new URL("https://ip-ranges.amazonaws.com/ip-ranges.json");
            JSONObject json = new JSONObject(IOUtils.toString(ipURL, "UTF-8"));
            Iterator<Object> prefixes = json.getJSONArray("prefixes").iterator();
            while (prefixes.hasNext()) {
                JSONObject prefix = (JSONObject) prefixes.next();
                if (prefix.getString("service").equalsIgnoreCase("ec2")) {
                    EC2_IPS.add(prefix);
                    IP_UTILS.put(prefix.getString("ip_prefix"), new SubnetUtils(prefix.getString("ip_prefix")));
                }
            }
        } catch (IOException ex) {
            Globals.logError(ex.toString(), ex);
        }

        REGION_NAMES.put("us-east-1", "N. Virginia, US East");
        REGION_NAMES.put("us-east-2", "Ohio, US East");
        REGION_NAMES.put("us-west-1", "N. California, US West");
        REGION_NAMES.put("us-west-2", "Oregon, US West");
        REGION_NAMES.put("eu-west-1", "Ireland");
        REGION_NAMES.put("eu-central-1", "Frankfurt, Germany");
        REGION_NAMES.put("ap-northeast-1", "Tokyo, Japan");
        REGION_NAMES.put("ap-northeast-2", "Seoul, Korea");
        REGION_NAMES.put("ap-southeast-1", "Singapore");
        REGION_NAMES.put("ap-southeast-2", "Sydney, Australia");
        REGION_NAMES.put("ap-south-1", "Mumbai, India");
        REGION_NAMES.put("sa-east-1", "São Paulo, Brazil");
        REGION_NAMES.put("ca-central-1", "Canada");
        Globals.log(HubModule.class, (System.currentTimeMillis() - start) + "ms HubModule startup", Globals.LOG_TYPE_DATA);
    }

    public static void addServerInfo(final Connection c, final ServerInfo info) {
        c.setKeepAliveTCP(1000);
        c.setTimeout(10000);
        Iterator<JSONObject> ips = EC2_IPS.iterator();
        while (ips.hasNext()) {
            JSONObject ip = ips.next();
            if (IP_UTILS.get(ip.getString("ip_prefix")).getInfo().isInRange(info.getAddress())) {
                info.setRegion(REGION_NAMES.get(ip.getString("region")));
                break;
            }
        }
        if (!CONN_SERVERINFO_MAP.containsKey(c)) {
            Globals.log(HubModule.class, "Added " + info + " to server list", Globals.LOG_TYPE_DATA);
        } else {
            //Globals.log(HubSender.class, "Updated " + c + " " + info, Globals.LOG_TYPE_DATA, true);
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
            Globals.log(HubModule.class, "Removed " + info + " from server list", Globals.LOG_TYPE_DATA);
        }
    }

    @Override
    public void run() {
        CONN_SERVERINFO_MAP.entrySet().stream().map((infoEntry) -> infoEntry.getKey()).forEach((c) -> {
            GET_SERVERSTAT_SCHEDULER.submit(() -> {
                HubSender.sendGetServerInfo(c);
            });
        });
    }

}
