package performancetest;

public class Main {

    public static void main(final String[] args) {
        TestRunner test = new TestRunner();
        if (args.length > 1) {
            Globals.UDP_MODE = Boolean.parseBoolean(args[1]);
            Globals.ROOM = Integer.parseInt(args[2]);
            Globals.PLAYERS = Integer.parseInt(args[3]);
        }
        test.runTest(args[0]);
    }

}
