package blockfighter.client;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

public class Core {

    public static final FocusHandler FOCUS_HANDLER = new FocusHandler();
    public static final KeyHandler KEY_HANDLER = new KeyHandler();
    public static final MouseHandler MOUSE_HANDLER = new MouseHandler();

    public static final ExecutorService SHARED_THREADPOOL = Executors.newFixedThreadPool(3,
            new BasicThreadFactory.Builder()
                    .namingPattern("Client-Shared-Thread-%d")
                    .daemon(true)
                    .priority(Thread.NORM_PRIORITY)
                    .build());

    public static final ScheduledExecutorService SHARED_SCHEDULED_THREADPOOL = Executors.newScheduledThreadPool(2, new BasicThreadFactory.Builder()
            .namingPattern("Client-Shared-Scheduled-Thread-%d")
            .daemon(true)
            .priority(Thread.NORM_PRIORITY)
            .build());

    private static LogicModule LOGIC_MODULE;
    private static SoundModule SOUND_MODULE;

    public static SoundModule getSoundModule() {
        if (SOUND_MODULE == null) {
            throw new NullPointerException("Core has not started");
        }
        return SOUND_MODULE;
    }

    public static LogicModule getLogicModule() {
        if (LOGIC_MODULE == null) {
            throw new NullPointerException("Core has not started");
        }
        return LOGIC_MODULE;
    }

    public static void setup() {
        LOGIC_MODULE = new LogicModule();
        SOUND_MODULE = new SoundModule();
        SHARED_THREADPOOL.execute(SOUND_MODULE);
    }
}
