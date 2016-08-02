package performancetest;

public class TestLogicModule {
    // Shared Data

    private TestGameClient client;
    private TestSaveData selectedChar;
    private byte selectedRoom = 0;
    private byte myKey = -1;
    private int ping = 0;

    public TestLogicModule(final int num, final byte room) {
        this.selectedChar = new TestSaveData("TestNum" + num);
        this.selectedChar.newCharacter(room * 10 + 1);
    }

    public void run() {
        if (myKey != -1) {
            client.sendMoveKey(this.myKey, Globals.UP, Globals.rng(2) == 0);
            client.sendMoveKey(this.myKey, (Globals.rng(2) == 0) ? Globals.LEFT : Globals.RIGHT, Globals.rng(2) == 0);
            client.sendGetPing();
            this.ping = client.getPing();
        }
    }

    public void connect(final String server, final int tcpPort, final int udpPort, final byte r) {
        this.selectedRoom = r;

        client = new TestGameClient(this, server, tcpPort, udpPort);
        client.run();
    }

    public void setKey(final byte key) {
        this.myKey = key;
    }

    public void setSelectedChar(final TestSaveData s) {
        this.selectedChar = s;
    }

    public TestSaveData getSelectedChar() {
        return this.selectedChar;
    }

    public void setSelectedRoom(final byte r) {
        this.selectedRoom = r;
    }

    public byte getSelectedRoom() {
        return this.selectedRoom;
    }

    public int getPing() {
        return this.ping;
    }
}
