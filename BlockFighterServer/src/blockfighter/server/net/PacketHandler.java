package blockfighter.server.net;

import blockfighter.server.Globals;
import blockfighter.server.LogicModule;
import blockfighter.server.entities.Player;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.Arrays;

/**
 * Threads to handle incoming requests.
 *
 * @author Ken
 */
public class PacketHandler extends Thread {

    private DatagramPacket requestPacket = null;
    private final LogicModule logic;
    private final Broadcaster broadcaster;

    /**
     * Initialize request handler when a request is received by the socket.
     *
     * @param bc Reference to Server Broadcaster
     * @param request Packet that is received
     * @param logic Reference to Logic module
     */
    public PacketHandler(Broadcaster bc, DatagramPacket request, LogicModule logic) {
        requestPacket = request;
        this.broadcaster = bc;
        this.logic = logic;
    }

    @Override
    public void run() {
        byte[] data = requestPacket.getData();
        byte dataType = data[0];
        InetAddress address = requestPacket.getAddress();
        int port = requestPacket.getPort();
        switch (dataType) {
            case Globals.DATA_LOGIN:
                receiveLogin(address, port);
                break;
            case Globals.DATA_GET_ALL_PLAYER:
                receiveGetAllPlayer(address, port);
                break;
            case Globals.DATA_SET_PLAYER_MOVE:
                receiveSetPlayerMove(data);
                break;
            case Globals.DATA_PING:
                receiveGetPing(address, port, data);
                break;
            case Globals.DATA_PLAYER_ACTION:
                receivePlayerAction(data);
                break;
            default:
                Globals.log("DATA_UNKNOWN", address.toString(), Globals.LOG_TYPE_DATA, true);
                break;
        }
    }

    private void receiveGetPing(InetAddress address, int port, byte[] data) {
        //Globals.log("DATA_PING", address.toString(), Globals.LOG_TYPE_DATA, true);
        byte[] bytes = new byte[Globals.PACKET_BYTE + Globals.PACKET_BYTE];
        bytes[0] = Globals.DATA_PING;
        bytes[1] = data[1];
        broadcaster.sendPlayer(bytes, address, port);
    }

    private void receivePlayerAction(byte[] data) {
        Globals.log("DATA_PLAYER_ACTION", "Index: " + data[1], Globals.LOG_TYPE_DATA, false);
        logic.queuePlayerAction(data);
    }

    private void receiveLogin(InetAddress address, int port) {
        Globals.log("DATA_LOGIN", address + ":" + port, Globals.LOG_TYPE_DATA, true);

        byte freeIndex = logic.getNextIndex();

        if (freeIndex == -1) {
            return;
        }
        Player newPlayer = new Player(broadcaster, logic, freeIndex, address, port, logic.getMap(), Math.random() * 1180.0 + 100, 0);
        logic.queueAddPlayer(newPlayer);
        byte[] bytes = new byte[Globals.PACKET_BYTE + Globals.PACKET_BYTE + Globals.PACKET_BYTE];
        bytes[0] = Globals.DATA_LOGIN;
        bytes[1] = freeIndex;
        bytes[2] = Globals.MAX_PLAYERS;
        broadcaster.sendPlayer(bytes, address, port);

        bytes = new byte[Globals.PACKET_BYTE + Globals.PACKET_BYTE + Globals.PACKET_INT + Globals.PACKET_INT];
        bytes[0] = Globals.DATA_GET_PLAYER_POS;
        bytes[1] = newPlayer.getIndex();

        byte[] posXInt = Globals.intToByte((int) newPlayer.getX());
        bytes[2] = posXInt[0];
        bytes[3] = posXInt[1];
        bytes[4] = posXInt[2];
        bytes[5] = posXInt[3];

        byte[] posYInt = Globals.intToByte((int) newPlayer.getY());
        bytes[6] = posYInt[0];
        bytes[7] = posYInt[1];
        bytes[8] = posYInt[2];
        bytes[9] = posYInt[3];
        broadcaster.sendAll(bytes);

        bytes = new byte[Globals.PACKET_BYTE + Globals.PACKET_BYTE + Globals.PACKET_BYTE];
        bytes[0] = Globals.DATA_SET_PLAYER_FACING;
        bytes[1] = newPlayer.getIndex();
        bytes[2] = newPlayer.getFacing();
        broadcaster.sendAll(bytes);
    }

    private void receiveGetAllPlayer(InetAddress address, int port) {
        Player[] players = Arrays.copyOf(logic.getPlayers(), logic.getPlayers().length);
        for (Player player : players) {
            if (player == null) {
                continue;
            }

            byte[] bytes = new byte[Globals.PACKET_BYTE + Globals.PACKET_INT + Globals.PACKET_INT + Globals.PACKET_INT];
            bytes[0] = Globals.DATA_GET_PLAYER_POS;
            bytes[1] = player.getIndex();

            byte[] posXInt = Globals.intToByte((int) player.getX());
            bytes[2] = posXInt[0];
            bytes[3] = posXInt[1];
            bytes[4] = posXInt[2];
            bytes[5] = posXInt[3];
            byte[] posYInt = Globals.intToByte((int) player.getY());
            bytes[6] = posYInt[0];
            bytes[7] = posYInt[1];
            bytes[8] = posYInt[2];
            bytes[9] = posYInt[3];
            broadcaster.sendPlayer(bytes, address, port);

            bytes = new byte[Globals.PACKET_BYTE + Globals.PACKET_BYTE + Globals.PACKET_BYTE];
            bytes[0] = Globals.DATA_SET_PLAYER_FACING;
            bytes[1] = player.getIndex();
            bytes[2] = player.getFacing();
            broadcaster.sendPlayer(bytes, address, port);

            bytes = new byte[Globals.PACKET_BYTE + Globals.PACKET_BYTE + Globals.PACKET_BYTE + Globals.PACKET_BYTE];
            bytes[0] = Globals.DATA_SET_PLAYER_STATE;
            bytes[1] = player.getIndex();
            bytes[2] = player.getPlayerState();
            bytes[3] = player.getFrame();
            broadcaster.sendPlayer(bytes, address, port);
        }
    }

    private void receiveSetPlayerMove(byte[] data) {
        logic.queuePlayerMove(data);
    }
}
