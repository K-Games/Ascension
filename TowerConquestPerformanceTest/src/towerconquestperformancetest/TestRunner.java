package towerconquestperformancetest;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

public class TestRunner {

    public void runTest(final String server) {
        final ScheduledExecutorService service = Executors.newScheduledThreadPool(2, new BasicThreadFactory.Builder()
                .namingPattern("RunScheduler-%d")
                .daemon(false)
                .priority(Thread.NORM_PRIORITY)
                .build());

        byte i = 0;
        for (byte room = 0; room < 10; room++) {
            for (byte player = 0; player < 10; player++) {
                if (i < 99) {
                    TestLogicModule logic = new TestLogicModule(i, room);
                    if (room < 3) {
                        logic.connect(server, 25568, room);
                    } else if (room < 6) {
                        logic.connect(server, 25568, room);
                    } else if (room < 7) {
                        logic.connect(server, 25568, room);
                    } else if (room < 10) {
                        logic.connect(server, 25568, room);
                    }
                    service.scheduleAtFixedRate(logic, 0, 1000, TimeUnit.MILLISECONDS);
                    i++;
                }
            }
        }
//        for (byte player = 0; player < 49; player++) {
//            TestLogicModule logic = new TestLogicModule(player, (byte) 9);
//            logic.connect(server, 25568, (byte) 9);
//            service.scheduleAtFixedRate(logic, 0, 100, TimeUnit.MILLISECONDS);
//        }
    }
}
