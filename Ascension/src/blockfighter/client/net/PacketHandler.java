package blockfighter.client.net;

import blockfighter.shared.Globals;

public class PacketHandler {

    private static GameClient gameClient;

    public static void setGameClient(final GameClient cl) {
        gameClient = cl;
    }

    public static void process(final byte[] data) {
        final byte dataType = data[0];
        switch (dataType) {
            case Globals.DATA_PLAYER_LOGIN:
                receiveLogin(data);
                break;
            case Globals.DATA_PLAYER_CREATE:
                receiveCreate(data);
                break;
            default:
                receiveData(data);
                break;
        }
    }

    private static void receiveCreate(final byte[] data) {
        final byte mapID = data[1],
                key = data[2],
                size = data[3];
        gameClient.receiveCreate(mapID, key, size);
    }

    private static void receiveLogin(final byte[] data) {
        new Thread() {
            @Override
            public void run() {
                try {
                    gameClient.receiveLogin(data);
                } catch (Exception e) {
                    gameClient.shutdownClient((byte) -1);
                }
            }
        }.start();
    }

    private static void receiveData(final byte[] data) {
        gameClient.queueData(data);
    }

}
