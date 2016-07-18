package towerconquestperformancetest;

public class TestLogicModule extends Thread {
    // Shared Data

    private TestGameClient client;
    private TestSaveData selectedChar;
    private byte selectedRoom = 0;
    private byte myKey = -1;

    public TestLogicModule(final byte num, final byte room) {
        this.selectedChar = new TestSaveData("TestNum" + num);
        this.selectedChar.newCharacter(room * 10 + 1);
    }

    @Override
    public void run() {
        if (myKey != -1) {
            client.sendMoveKey(this.myKey, Globals.UP, true);
        }
    }

    public void connect(final String server, final int port, final byte r) {
        this.selectedRoom = r;

        client = new TestGameClient(this, server, port);
        client.setName("GameClient");
        client.setDaemon(true);
        client.start();
    }

    public void setKey(final byte key) {
        this.myKey = key;
    }

    public void disconnect() {

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

}
