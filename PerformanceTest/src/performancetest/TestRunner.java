package performancetest;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

public class TestRunner implements Runnable {

    private final TestLogicModule[] logic = new TestLogicModule[200];

    public void runTest(final String server) {
        final ScheduledExecutorService service = Executors.newScheduledThreadPool(1, new BasicThreadFactory.Builder()
                .namingPattern("RunScheduler-%d")
                .daemon(false)
                .priority(Thread.NORM_PRIORITY)
                .build());

        for (byte player = 0; player < Globals.PLAYERS; player++) {
            logic[player] = new TestLogicModule(player, (byte) Globals.ROOM);
            logic[player].connect(server, Globals.SERVER_TCP_PORT, Globals.SERVER_UDP_PORT, (byte) Globals.ROOM);
        }

        service.scheduleAtFixedRate(this, 0, 300, TimeUnit.MILLISECONDS);
    }

    @Override
    public void run() {
        int avgPing = 0;
        int testers = 0;
        for (TestLogicModule lm : logic) {
            if (lm != null) {
                lm.run();
                avgPing += lm.getPing();
                testers++;
            }
        }
        System.out.println("Avg Ping: " + (avgPing / testers));
    }
}
