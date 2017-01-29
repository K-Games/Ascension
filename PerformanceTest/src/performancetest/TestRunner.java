package performancetest;

import blockfighter.shared.Globals;
import com.esotericsoftware.kryonet.KryoNetException;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

public class TestRunner implements Runnable {

    private TestLogicModule[] logic = new TestLogicModule[0];

    public void runTest(final String server) {
        ScheduledExecutorService service = Executors.newScheduledThreadPool(2, new BasicThreadFactory.Builder()
                .namingPattern("RunScheduler-%d")
                .daemon(false)
                .priority(Thread.NORM_PRIORITY)
                .build());
        ExecutorService tasks = Executors.newFixedThreadPool(5, new BasicThreadFactory.Builder()
                .namingPattern("RunScheduler-%d")
                .daemon(false)
                .priority(Thread.NORM_PRIORITY)
                .build());
        logic = new TestLogicModule[Main.PLAYERS];
        for (int player = 0; player < Main.PLAYERS; player++) {
            logic[player] = new TestLogicModule(player, (byte) Main.ROOM);
            tasks.submit(logic[player].connect(server, Globals.SERVER_TCP_PORT, Globals.SERVER_UDP_PORT, (byte) Main.ROOM));
        }

        service.scheduleAtFixedRate(this, 0, 300, TimeUnit.MILLISECONDS);
        service.scheduleAtFixedRate(() -> {
            update();
        }, 0, 1, TimeUnit.MILLISECONDS);

    }

    public void update() {
        for (TestLogicModule lm : logic) {
            if (lm != null) {
                try {
                    lm.getGC().update();
                } catch (IOException ex) {
                    ex.printStackTrace();
                    lm.getGC().shutdownClient();
                } catch (KryoNetException ex) {
                    lm.getGC().shutdownClient();
                    ex.printStackTrace();
                }
            }
        }
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
        //System.out.println("Avg Ping: " + (avgPing / testers));
    }
}
