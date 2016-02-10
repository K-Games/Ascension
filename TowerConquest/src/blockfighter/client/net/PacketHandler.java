package blockfighter.client.net;

import blockfighter.client.Globals;
import blockfighter.client.LogicModule;
import blockfighter.client.Main;
import java.net.DatagramPacket;

/**
 *
 * @author Ken Kwan
 */
public class PacketHandler implements Runnable {

    private DatagramPacket r = null;
    private static LogicModule logic;

    public PacketHandler(final DatagramPacket response) {
        this.r = response;
    }

    public static void init() {
        logic = Main.getLogicModule();
    }

    @Override
    public void run() {
        final byte[] data = this.r.getData();
        final byte dataType = data[0];
        switch (dataType) {
            case Globals.DATA_PLAYER_LOGIN:
                receiveLogin(data);
                break;
            case Globals.DATA_PLAYER_CREATE:
                receiveCreate(data);
                break;
            case Globals.DATA_PING:
                receiveGetPing(data);
                break;
            default:
                receiveData(data);
                break;
        }
    }

    private void receiveCreate(final byte[] data) {
        final byte mapID = data[1],
                key = data[2],
                size = data[3];
        logic.receiveCreate(mapID, key, size);
    }

    private void receiveLogin(final byte[] data) {
        logic.receiveLogin(data);
    }

    private void receiveGetPing(final byte[] data) {
        logic.setPing(data[1]);
    }

    private void receiveData(final byte[] data) {
        logic.queueData(data);
    }

}
