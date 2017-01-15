package performancetest;

public class Main {

    public static boolean UDP_MODE;
    public static int ROOM, PLAYERS;

    public static void main(final String[] args) {
        TestRunner test = new TestRunner();
        if (args.length > 1) {
            UDP_MODE = Boolean.parseBoolean(args[1]);
            ROOM = Integer.parseInt(args[2]);
            PLAYERS = Integer.parseInt(args[3]);
        }
        test.runTest(args[0]);
    }

}
