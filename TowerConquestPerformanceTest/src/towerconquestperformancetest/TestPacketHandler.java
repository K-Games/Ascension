package towerconquestperformancetest;

import java.net.DatagramPacket;
import java.util.concurrent.ConcurrentLinkedQueue;

public class TestPacketHandler extends Thread {

    ConcurrentLinkedQueue<DatagramPacket> packetQueue = new ConcurrentLinkedQueue<>();
    private TestLogicModule logic;

    public TestPacketHandler(TestLogicModule l) {
        logic = l;
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
            DatagramPacket requestPacket = packetQueue.poll();
            final byte[] data = requestPacket.getData();
            final byte dataType = data[0];
            switch (dataType) {
                case Globals.DATA_PLAYER_LOGIN:
                    receiveLogin(data);
                    break;
                case Globals.DATA_PLAYER_CREATE:
                    receiveCreate(data);
                    break;
                default:
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
}
