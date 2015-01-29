package blockfighter.client.net;

import blockfighter.client.Globals;
import blockfighter.client.LogicModule;
import java.net.DatagramPacket;

/**
 *
 * @author Ken Kwan
 */
public class PacketHandler extends Thread {

    private DatagramPacket r = null;
    private final LogicModule logic;

    public PacketHandler(DatagramPacket response, LogicModule logic) {
        r = response;
        this.logic = logic;
    }

    @Override
    public void run() {
        byte[] data = r.getData();
        byte dataType = data[0];
        switch (dataType) {
            case Globals.DATA_LOGIN:
                receiveLogin(data);
                break;
            case Globals.DATA_PING:
                receiveGetPing(data);
                break;
            default:
                receiveData(data);
                break;
        }
    }

    private void receiveLogin(byte[] data) {
        byte key = data[1];
        byte size = data[2];
        logic.receiveLogin(key, size);
    }

    private void receiveGetPing(byte[] data) {
        logic.setPing(data[1]);
    }

    private void receiveData(byte[] data) {
        logic.queueData(data);
    }

}
