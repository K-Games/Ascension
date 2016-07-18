package towerconquestperformancetest;

import java.util.concurrent.ConcurrentLinkedQueue;

public class TestPacketHandler {

    private static ConcurrentLinkedQueue<byte[]> packetQueue = new ConcurrentLinkedQueue<>();

    public static void process(final byte[] data, final TestGameClient gameClient) {
        final byte dataType = data[0];
        switch (dataType) {
            case Globals.DATA_PLAYER_LOGIN:
                receiveLogin(data, gameClient);
                break;
            case Globals.DATA_PLAYER_CREATE:
                receiveCreate(data, gameClient);
                break;
        }
    }

    private static void receiveCreate(final byte[] data, final TestGameClient gameClient) {
        final byte mapID = data[1],
                key = data[2],
                size = data[3];
        gameClient.receiveCreate(mapID, key, size);
    }

    private static void receiveLogin(final byte[] data, final TestGameClient gameClient) {
        new Thread() {
            @Override
            public void run() {
                gameClient.receiveLogin(data);
            }
        }.start();
    }

    public static void clearDataQueue() {
        packetQueue.clear();
    }
}
