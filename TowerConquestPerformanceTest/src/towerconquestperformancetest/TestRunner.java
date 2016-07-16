package towerconquestperformancetest;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
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
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(TestRunner.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    TestLogicModule logic = new TestLogicModule();
                    TestPacketHandler ph = new TestPacketHandler(logic);
                    logic.setPH(ph);
                    if (room < 3) {
                        logic.sendLogin(server, 25565, room, i);
                    } else if (room < 6) {
                        logic.sendLogin(server, 25566, room, i);
                    } else if (room < 7) {
                        logic.sendLogin(server, 25567, room, i);
                    } else if (room < 10) {
                        logic.sendLogin(server, 25568, room, i);
                    }
                    service.scheduleAtFixedRate(logic, 0, 100, TimeUnit.MILLISECONDS);
                    service.scheduleAtFixedRate(ph, 0, 10, TimeUnit.MILLISECONDS);
                    i++;
                }
            }
        }
    }
}
