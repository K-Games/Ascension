package blockfighter.hub;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.IOUtils;
import org.apache.commons.net.util.SubnetUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public class HubModule {

    private static final Logger LOGGER = Logger.getLogger(HubModule.class.getSimpleName());

    private static final List<JSONObject> EC2_IPS = new LinkedList<>();
    private static final HashMap<String, SubnetUtils> IP_UTILS = new HashMap<>();
    private static final HashMap<String, String> REGION_NAMES = new HashMap<>();

    private static final HashMap<String, ServerInfo> SERVER_INFO_MAP = new HashMap<>();

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
            LOGGER.log(Level.INFO, ex.toString());
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

        LOGGER.log(Level.INFO, "{0}ms HubModule startup", System.currentTimeMillis() - start);
    }

    public static void addServerInfo(final ServerInfo info) {
        Iterator<JSONObject> ips = EC2_IPS.iterator();
        while (ips.hasNext()) {
            JSONObject ip = ips.next();
            if (IP_UTILS.get(ip.getString("ip_prefix")).getInfo().isInRange(info.getAddress())) {
                info.setRegion(REGION_NAMES.get(ip.getString("region")));
                break;
            }
        }
        if (!SERVER_INFO_MAP.containsKey(info.getAddress())) {
            LOGGER.log(Level.INFO, "Added Key={0} to server list", info);
        } else {
            LOGGER.log(Level.INFO, "Updated Key={0} {1}", new String[]{info.getAddress(), info.toString()});
        }
        SERVER_INFO_MAP.put(info.getAddress(), info);
    }

    public static String getServerList() {
        JSONArray json = new JSONArray(SERVER_INFO_MAP.values());
        return json.toString();
    }

    public static void cleanUpServerList() {
        SERVER_INFO_MAP.forEach((key, serverInfo) -> {
            if (serverInfo.getTimeAlive() >= 10000) {
                SERVER_INFO_MAP.remove(key);
                LOGGER.log(Level.INFO, "Removed {0} from server list.", key);
            }
        });
    }
}
