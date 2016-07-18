package towerconquestperformancetest;

public class TestLogicModule {
    // Shared Data

    private TestGameClient client;
    private TestSaveData selectedChar;
    private byte selectedRoom = 0;
    private byte myKey = -1;
    private long pingTime = 0;
    private int ping = 0;
    private byte pingID = 0;

    public TestLogicModule(final byte num, final byte room) {
        this.selectedChar = new TestSaveData("TestNum" + num);
        this.selectedChar.newCharacter(room * 10 + 1);
    }

    public void run() {
        if (myKey != -1) {
            client.sendMoveKey(this.myKey, (byte) Globals.rng(4), Globals.rng(2) == 0);
            this.pingID = (byte) (Globals.rng(256));
            this.pingTime = System.currentTimeMillis();
            client.sendGetPing(this.myKey, this.pingID);
        }
    }

    public void connect(final String server, final int port, final byte r) {
        this.selectedRoom = r;

        client = new TestGameClient(this, server, port);
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

    public void setPing(final byte rID) {
        if (rID != this.pingID) {
            return;
        }
        this.ping = (int) (System.currentTimeMillis() - this.pingTime);
        if (this.ping >= 500) {
            this.ping = 9999;
        }
    }

    public int getPing() {
        return this.ping;
    }
}
