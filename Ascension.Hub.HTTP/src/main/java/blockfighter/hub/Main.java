package blockfighter.hub;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.json.JSONObject;
import static spark.Spark.*;

public class Main {

    public static final ScheduledExecutorService SHARED_SCHEDULED_THREADPOOL = Executors.newScheduledThreadPool(2);

    public static void main(String[] args) {
        SHARED_SCHEDULED_THREADPOOL.scheduleAtFixedRate(() -> HubModule.cleanUpServerList(), 0, 15, TimeUnit.SECONDS);
        HubModule.getServerList();
        port(getPortEnvironmentVar());
        get("/get-list", (req, res) -> HubModule.getServerList());
        post("/server-info", (request, response) -> {
            ServerInfo serverInfo = new ServerInfo(new JSONObject(request.body()));
            HubModule.addServerInfo(serverInfo);
            return "Received " + serverInfo;
        });
    }

    static int getPortEnvironmentVar() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (processBuilder.environment().get("PORT") != null) {
            return Integer.parseInt(processBuilder.environment().get("PORT"));
        }
        return 25566;
    }

}
