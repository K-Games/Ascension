package blockfighter.server.net;

import blockfighter.server.Globals;
import blockfighter.server.LogicModule;
import blockfighter.server.entities.player.Player;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.Map;

/**
 * Threads to handle incoming requests.
 *
 * @author Ken
 */
public class PacketHandler extends Thread {

    private DatagramPacket requestPacket = null;
    private final LogicModule[] logic;
    private final PacketSender packetSender;

    /**
     * Initialize request handler when a request is received by the socket.
     *
     * @param bc Reference to Server PacketSender
     * @param request Packet that is received
     * @param logic Reference to Logic module
     */
    public PacketHandler(PacketSender bc, DatagramPacket request, LogicModule[] logic) {
        requestPacket = request;
        this.packetSender = bc;
        this.logic = logic;
    }

    @Override
    public void run() {
        byte[] data = requestPacket.getData();
        byte dataType = data[0];
        byte room = data[1];
        if (room >= Globals.SERVER_ROOMS) {
            return;
        }
        InetAddress address = requestPacket.getAddress();
        int port = requestPacket.getPort();
        switch (dataType) {
            case Globals.DATA_LOGIN:
                receiveLogin(data, room, address, port);
                break;
            case Globals.DATA_GET_ALL_PLAYER:
                receiveGetAllPlayer(room, address, port);
                break;
            case Globals.DATA_SET_PLAYER_MOVE:
                receiveSetPlayerMove(data, room);
                break;
            case Globals.DATA_PING:
                receiveGetPing(address, port, data);
                break;
            case Globals.DATA_PLAYER_ACTION:
                receivePlayerAction(data, room);
                break;
            default:
                Globals.log("DATA_UNKNOWN", address.toString(), Globals.LOG_TYPE_DATA, true);
                break;
        }
    }

    private void receiveGetPing(InetAddress address, int port, byte[] data) {
        //Globals.log("DATA_PING", address.toString(), Globals.LOG_TYPE_DATA, true);
        byte[] bytes = new byte[Globals.PACKET_BYTE * 2];
        bytes[0] = Globals.DATA_PING;
        bytes[1] = data[1];
        packetSender.sendPlayer(bytes, address, port);
    }

    private void receivePlayerAction(byte[] data, byte room) {
        Globals.log("DATA_PLAYER_ACTION", "Key: " + data[2] + " Room: " + room, Globals.LOG_TYPE_DATA, true);
        logic[room].queuePlayerAction(data);
    }

    private void receiveLogin(byte[] data, byte room, InetAddress address, int port) {
        Globals.log("DATA_LOGIN", address + ":" + port + " Room: " + room, Globals.LOG_TYPE_DATA, true);
        byte freeKey = logic[room].getNextPlayerKey();

        if (freeKey == -1) {
            return;
        }
        Player newPlayer = new Player(packetSender, logic[room], freeKey, address, port, logic[room].getMap(), Math.random() * 1180.0 + 100, 0);
        logic[room].queueAddPlayer(newPlayer);
        byte[] bytes = new byte[Globals.PACKET_BYTE * 3];
        bytes[0] = Globals.DATA_LOGIN;
        bytes[1] = freeKey;
        bytes[2] = Globals.SERVER_MAX_PLAYERS;
        packetSender.sendPlayer(bytes, address, port);

        newPlayer.sendPos();
        newPlayer.sendFacing();
        newPlayer.sendState();
    }

    private void receiveGetAllPlayer(byte room, InetAddress address, int port) {
        //Globals.log("DATA_GET_ALL_PLAYER", "Room: " + room, Globals.LOG_TYPE_DATA, true);
        for (Map.Entry<Byte, Player> pEntry : logic[room].getPlayers().entrySet()) {
            Player player = pEntry.getValue();

            byte[] bytes = new byte[Globals.PACKET_BYTE * 2 + Globals.PACKET_INT * 2];
            bytes[0] = Globals.DATA_SET_PLAYER_POS;
            bytes[1] = player.getKey();

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
            packetSender.sendPlayer(bytes, address, port);

            bytes = new byte[Globals.PACKET_BYTE * 3];
            bytes[0] = Globals.DATA_SET_PLAYER_FACING;
            bytes[1] = player.getKey();
            bytes[2] = player.getFacing();
            packetSender.sendPlayer(bytes, address, port);

            bytes = new byte[Globals.PACKET_BYTE * 4];
            bytes[0] = Globals.DATA_SET_PLAYER_STATE;
            bytes[1] = player.getKey();
            bytes[2] = player.getPlayerState();
            bytes[3] = player.getFrame();
            packetSender.sendPlayer(bytes, address, port);
        }
    }

    private void receiveSetPlayerMove(byte[] data, byte room) {
        logic[room].queuePlayerMove(data);
    }
}
