package performancetest;

import com.esotericsoftware.minlog.Log;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

public class Main {

    public final static ExecutorService SHARED_THREADS = Executors.newFixedThreadPool(2, new BasicThreadFactory.Builder()
            .namingPattern("SHARED_THREADS-%d")
            .daemon(true)
            .priority(Thread.NORM_PRIORITY)
            .build());
    public final static ScheduledExecutorService SCHEDULE_SHARED_THREADS = Executors.newScheduledThreadPool(2, new BasicThreadFactory.Builder()
            .namingPattern("SCHEDULE_SHARED_THREADS-%d")
            .daemon(false)
            .priority(Thread.NORM_PRIORITY)
            .build());

    public static boolean UDP_MODE;
    public static byte MIN_LEVEL, MAX_LEVEL;
    public static int PLAYERS;

    public static void main(final String[] args) {
        Log.set(Log.LEVEL_NONE);
        TestRunner test = new TestRunner();
        if (args.length > 1) {
            UDP_MODE = Boolean.parseBoolean(args[1]);
            MIN_LEVEL = Byte.parseByte(args[2]);
            MAX_LEVEL = Byte.parseByte(args[3]);
            PLAYERS = Integer.parseInt(args[4]);
        }
        test.runTest(args[0]);
    }

}
