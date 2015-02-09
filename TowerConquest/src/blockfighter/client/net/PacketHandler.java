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
    private static LogicModule logic;

    public static void setLogic(LogicModule l) {
        logic = l;
    }

    public PacketHandler(DatagramPacket response) {
        r = response;
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
        byte mapID = data[1],
                key = data[2],
                size = data[3];
        logic.receiveLogin(mapID, key, size);
    }

    private void receiveGetPing(byte[] data) {
        logic.setPing(data[1]);
    }

    private void receiveData(byte[] data) {
        logic.queueData(data);
    }

}
