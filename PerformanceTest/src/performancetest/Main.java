package performancetest;

import com.esotericsoftware.minlog.Log;

public class Main {

    public static boolean UDP_MODE;
    public static int ROOM, PLAYERS;

    public static void main(final String[] args) {
        Log.set(Log.LEVEL_NONE);
        TestRunner test = new TestRunner();
        if (args.length > 1) {
            UDP_MODE = Boolean.parseBoolean(args[1]);
            ROOM = Integer.parseInt(args[2]);
            PLAYERS = Integer.parseInt(args[3]);
        }
        test.runTest(args[0]);
    }

}
