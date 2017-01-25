package blockfighter.client;

import java.util.concurrent.Executors;

public class Core {

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
        Executors.newSingleThreadExecutor().execute(SOUND_MODULE);
    }
}
