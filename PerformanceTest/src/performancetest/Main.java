package performancetest;

import com.esotericsoftware.minlog.Log;

public class Main {

    public static boolean UDP_MODE;
    public static byte MIN_LEVEL, MAX_LEVEL, PLAYERS;

    public static void main(final String[] args) {
        Log.set(Log.LEVEL_NONE);
        TestRunner test = new TestRunner();
        if (args.length > 1) {
            UDP_MODE = Boolean.parseBoolean(args[1]);
            MIN_LEVEL = Byte.parseByte(args[2]);
            MAX_LEVEL = Byte.parseByte(args[3]);
            PLAYERS = Byte.parseByte(args[4]);
        }
        test.runTest(args[0]);
    }

}
