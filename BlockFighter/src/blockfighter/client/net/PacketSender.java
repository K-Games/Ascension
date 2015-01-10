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

    public void sendLogin() {
        System.out.println("Connecting to " + Globals.SERVER_ADDRESS);
        byte[] bytes = new byte[Globals.PACKET_BYTE];
        bytes[0] = Globals.DATA_LOGIN;

        DatagramPacket requestPacket = createPacket(bytes);
        sendPacket(requestPacket);
    }

    public void sendGetAll() {
        byte[] bytes = new byte[Globals.PACKET_BYTE];
        bytes[0] = Globals.DATA_GET_ALL_PLAYER;

        DatagramPacket requestPacket = createPacket(bytes);
        sendPacket(requestPacket);
    }

    public void sendMove(byte index, byte direction, boolean move) {
        byte[] bytes = new byte[Globals.PACKET_BYTE + Globals.PACKET_BYTE + Globals.PACKET_BYTE + Globals.PACKET_BYTE];
        bytes[0] = Globals.DATA_SET_PLAYER_MOVE;
        bytes[1] = index;
        bytes[2] = direction;
        bytes[3] = (byte) (move ? 1 : 0);
        DatagramPacket requestPacket = createPacket(bytes);
        sendPacket(requestPacket);
    }

    public void sendAction(byte index) {
        byte[] bytes = new byte[Globals.PACKET_BYTE + Globals.PACKET_BYTE];
        bytes[0] = Globals.DATA_PLAYER_ACTION;
        bytes[1] = index;
        DatagramPacket requestPacket = createPacket(bytes);
        sendPacket(requestPacket);
    }

    public void sendGetPing(byte pID) {
        byte[] bytes = new byte[Globals.PACKET_BYTE + Globals.PACKET_BYTE];
        bytes[0] = Globals.DATA_PING;
        bytes[1] = pID;
        DatagramPacket requestPacket = createPacket(bytes);
        sendPacket(requestPacket);
    }

    private void sendPacket(DatagramPacket packet) {
        try {
            socket.send(packet);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private DatagramPacket createPacket(byte[] bytes) {
        return new DatagramPacket(bytes, bytes.length);
    }
}
