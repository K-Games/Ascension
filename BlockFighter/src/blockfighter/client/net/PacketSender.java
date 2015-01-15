package blockfighter.client.net;

import blockfighter.client.Globals;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 *
 * @author Ken
 */
public class PacketSender {

    InetAddress address;
    DatagramSocket socket = null;

    public PacketSender(DatagramSocket socket) {
        this.socket = socket;
        address = socket.getInetAddress();
    }

    public void sendLogin(byte room) {
        System.out.println("Connecting to " + Globals.SERVER_ADDRESS);
        byte[] bytes = new byte[Globals.PACKET_BYTE * 2];
        bytes[0] = Globals.DATA_LOGIN;
        bytes[1] = room;
        DatagramPacket requestPacket = createPacket(bytes);
        sendPacket(requestPacket);
    }

    public void sendGetAll(byte room) {
        byte[] bytes = new byte[Globals.PACKET_BYTE * 2];
        bytes[0] = Globals.DATA_GET_ALL_PLAYER;
        bytes[1] = room;
        DatagramPacket requestPacket = createPacket(bytes);
        sendPacket(requestPacket);
    }

    public void sendMove(byte room, byte key, byte direction, boolean move) {
        byte[] bytes = new byte[Globals.PACKET_BYTE * 5];
        bytes[0] = Globals.DATA_SET_PLAYER_MOVE;
        bytes[1] = room;
        bytes[2] = key;
        bytes[3] = direction;
        bytes[4] = (byte) (move ? 1 : 0);
        DatagramPacket requestPacket = createPacket(bytes);
        sendPacket(requestPacket);
    }

    public void sendAction(byte room, byte key) {
        byte[] bytes = new byte[Globals.PACKET_BYTE * 3];
        bytes[0] = Globals.DATA_PLAYER_ACTION;
        bytes[1] = room;
        bytes[2] = key;
        DatagramPacket requestPacket = createPacket(bytes);
        sendPacket(requestPacket);
    }

    public void sendGetPing(byte pID) {
        byte[] bytes = new byte[Globals.PACKET_BYTE * 2];
        bytes[0] = Globals.DATA_PING;
        bytes[1] = pID;
        DatagramPacket requestPacket = createPacket(bytes);
        sendPacket(requestPacket);
    }

    private void sendPacket(DatagramPacket packet) {
        try {
            socket.send(packet);
        } catch (Exception ex) {
        }
    }

    private DatagramPacket createPacket(byte[] bytes) {
        return new DatagramPacket(bytes, bytes.length);
    }
}
