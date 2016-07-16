package blockfighter.client.net;

import blockfighter.client.Globals;
import blockfighter.client.LogicModule;
import blockfighter.client.Main;
import java.net.DatagramPacket;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 *
 * @author Ken Kwan
 */
public class PacketHandler implements Runnable {

    ConcurrentLinkedQueue<DatagramPacket> packetQueue = new ConcurrentLinkedQueue<>();
    private static LogicModule logic;

    public static void init() {
        logic = Main.getLogicModule();
    }

    public void queuePacket(DatagramPacket data) {
        packetQueue.add(data);
    }

    @Override
    public void run() {
        process();
    }

    public void process() {
        while (!packetQueue.isEmpty()) {
            DatagramPacket r = packetQueue.poll();
            final byte[] data = r.getData();
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
    }

    private void receiveCreate(final byte[] data) {
        final byte mapID = data[1],
                key = data[2],
                size = data[3];
        logic.receiveCreate(mapID, key, size);
    }

    private void receiveLogin(final byte[] data) {
        new Thread() {
            @Override
            public void run() {
                logic.receiveLogin(data);
            }
        }.start();
    }

    private void receiveGetPing(final byte[] data) {
        logic.setPing(data[1]);
    }

    private void receiveData(final byte[] data) {
        logic.queueData(data);
    }

    public void clearDataQueue() {
        this.packetQueue.clear();
    }

}
