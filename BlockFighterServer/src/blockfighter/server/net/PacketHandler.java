package blockfighter.server.net;

import blockfighter.server.Globals;
import blockfighter.server.LogicModule;
import blockfighter.server.entities.player.Player;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
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
                receiveGetAllPlayer(data, room, address, port);
                break;
            case Globals.DATA_SET_PLAYER_MOVE:
                receiveSetPlayerMove(data, room);
                break;
            case Globals.DATA_PING:
                receivePing(data, room, address, port);
                break;
            case Globals.DATA_PLAYER_ACTION:
                receivePlayerAction(data, room);
                break;
            case Globals.DATA_PLAYER_DISCONNECT:
                receivePlayerDisconnect(data, room);
                break;
            case Globals.DATA_PLAYER_GET_NAME:
                receivePlayerGetName(data, room, address, port);
                break;
            default:
                Globals.log("DATA_UNKNOWN", address.toString(), Globals.LOG_TYPE_DATA, true);
                break;
        }
    }

    private void receivePlayerGetName(byte[] data, byte room, InetAddress address, int port) {
        if (logic[room].getPlayers().get(data[2]) == null) {
            return;
        }
        byte[] name = logic[room].getPlayers().get(data[2]).getPlayerName().getBytes(StandardCharsets.UTF_8);
        byte[] bytes = new byte[Globals.PACKET_BYTE * 2 + name.length];
        bytes[0] = Globals.DATA_PLAYER_GET_NAME;
        bytes[1] = data[2];
        System.arraycopy(name, 0, bytes, 2, name.length);
        packetSender.sendPlayer(bytes, address, port);
    }

    private void receivePlayerDisconnect(byte[] data, byte room) {
        if (logic[room].getPlayers().get(data[2]) == null) {
            return;
        }
        logic[room].getPlayers().get(data[2]).disconnect();
        Globals.log("DATA_DISCONNECT", logic[room].getPlayers().get(data[2]).getAddress() + ":" + logic[room].getPlayers().get(data[2]).getPort() + " Disconnected Key: " + data[2], Globals.LOG_TYPE_DATA, true);
    }

    private void receivePing(byte[] data, byte room, InetAddress address, int port) {
        //Globals.log("DATA_PING", address.toString(), Globals.LOG_TYPE_DATA, true);
        if (!logic[room].getPlayers().containsKey(data[2])) {
            return;
        }
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
        Globals.log("DATA_LOGIN", address + ":" + port + " Attempt Room: " + room, Globals.LOG_TYPE_DATA, true);
        byte[] temp = new byte[4];
        System.arraycopy(data, 17, temp, 0, temp.length);
        int id = Globals.bytesToInt(temp);

        if (logic[room].containsPlayerID(id)) {
            Globals.log("DATA_LOGIN", address + ":" + port + " uID Already In Room :" + id, Globals.LOG_TYPE_DATA, true);
            return;
        }

        byte freeKey = logic[room].getNextPlayerKey();
        if (freeKey == -1) {
            Globals.log("DATA_LOGIN", address + ":" + port + " Room " + room + " at max capacity", Globals.LOG_TYPE_DATA, true);
            return;
        }

        Globals.log("DATA_LOGIN", address + ":" + port + " Logged in Room " + room + " Key: " + freeKey, Globals.LOG_TYPE_DATA, true);
        Player newPlayer = new Player(packetSender, logic[room], freeKey, address, port, logic[room].getMap(), Math.random() * 1180.0 + 100, 0);

        temp = new byte[Globals.MAX_NAME_LENGTH];
        System.arraycopy(data, 2, temp, 0, temp.length);
        newPlayer.setPlayerName(new String(temp, StandardCharsets.UTF_8).trim());

        temp = new byte[4];
        System.arraycopy(data, 17, temp, 0, temp.length);
        newPlayer.setUniqueID(Globals.bytesToInt(temp));

        System.arraycopy(data, 21, temp, 0, temp.length);
        newPlayer.setStat(Globals.STAT_LEVEL, Globals.bytesToInt(temp));
        System.arraycopy(data, 25, temp, 0, temp.length);
        newPlayer.setStat(Globals.STAT_POWER, Globals.bytesToInt(temp));
        System.arraycopy(data, 29, temp, 0, temp.length);
        newPlayer.setStat(Globals.STAT_DEFENSE, Globals.bytesToInt(temp));
        System.arraycopy(data, 33, temp, 0, temp.length);
        newPlayer.setStat(Globals.STAT_SPIRIT, Globals.bytesToInt(temp));

        System.arraycopy(data, 37, temp, 0, temp.length);
        newPlayer.setBonusStat(Globals.STAT_ARMOR, Globals.bytesToInt(temp));
        System.arraycopy(data, 41, temp, 0, temp.length);
        newPlayer.setBonusStat(Globals.STAT_REGEN, Globals.bytesToInt(temp) / 10D);
        System.arraycopy(data, 45, temp, 0, temp.length);
        newPlayer.setBonusStat(Globals.STAT_CRITDMG, Globals.bytesToInt(temp) / 10000D);
        System.arraycopy(data, 49, temp, 0, temp.length);
        newPlayer.setBonusStat(Globals.STAT_CRITCHANCE, Globals.bytesToInt(temp) / 10000D);

        System.out.println("Name: " + newPlayer.getPlayerName());
        System.out.println("uID: " + newPlayer.getUniqueID());
        double[] stats = newPlayer.getStats();
        System.out.println("Lvl: " + stats[Globals.STAT_LEVEL]);
        System.out.println("PWR: " + stats[Globals.STAT_POWER]);
        System.out.println("DEF: " + stats[Globals.STAT_DEFENSE]);
        System.out.println("SPT: " + stats[Globals.STAT_SPIRIT]);
        stats = newPlayer.getBonusStats();
        System.out.println("Item Bonus Stats");
        System.out.println("AMR: " + stats[Globals.STAT_ARMOR]);
        System.out.println("REG: " + stats[Globals.STAT_REGEN]);
        System.out.println("CRITDMG: " + stats[Globals.STAT_CRITDMG]);
        System.out.println("CRITCHC: " + stats[Globals.STAT_CRITCHANCE]);
        logic[room].queueAddPlayer(newPlayer);

        byte[] bytes = new byte[Globals.PACKET_BYTE * 3];
        bytes[0] = Globals.DATA_LOGIN;
        bytes[1] = freeKey;
        bytes[2] = Globals.SERVER_MAX_PLAYERS;
        packetSender.sendPlayer(bytes, address, port);

        newPlayer.sendPos();
        newPlayer.sendFacing();
        newPlayer.sendState();
        newPlayer.sendName();
    }

    private void receiveGetAllPlayer(byte[] data, byte room, InetAddress address, int port) {
        //Globals.log("DATA_GET_ALL_PLAYER", "Room: " + room, Globals.LOG_TYPE_DATA, true);
        if (!logic[room].getPlayers().containsKey(data[2])) {
            return;
        }
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
