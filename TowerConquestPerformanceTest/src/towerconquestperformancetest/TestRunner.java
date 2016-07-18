package towerconquestperformancetest;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

public class TestRunner implements Runnable {

    private final TestLogicModule[] logic = new TestLogicModule[100];

    public void runTest(final String server) {
        final ScheduledExecutorService service = Executors.newScheduledThreadPool(1, new BasicThreadFactory.Builder()
                .namingPattern("RunScheduler-%d")
                .daemon(false)
                .priority(Thread.NORM_PRIORITY)
                .build());

        byte i = 0;
        for (byte room = 0; room < 10; room++) {
            for (byte player = 0; player < 10; player++) {
                if (i < 99) {
                    logic[i] = new TestLogicModule(i, room);
                    if (room < 3) {
                        logic[i].connect(server, 25565, room);
                    } else if (room < 6) {
                        logic[i].connect(server, 25565, room);
                    } else if (room < 7) {
                        logic[i].connect(server, 25565, room);
                    } else if (room < 10) {
                        logic[i].connect(server, 25565, room);
                    }

                    i++;
                }
            }
        }
        service.scheduleAtFixedRate(this, 0, 200, TimeUnit.MILLISECONDS);
    }

    @Override
    public void run() {
        for (TestLogicModule lm : logic) {
            if (lm != null) {
                lm.run();
            }
        }
    }
}
