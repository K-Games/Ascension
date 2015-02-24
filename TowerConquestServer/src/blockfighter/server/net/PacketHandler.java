package blockfighter.server.net;

import blockfighter.server.Globals;
import blockfighter.server.LogicModule;
import blockfighter.server.entities.player.Player;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

/**
 * Threads to handle incoming requests.
 *
 * @author Ken Kwan
 */
public class PacketHandler implements Runnable {

    private DatagramPacket requestPacket = null;
    private static LogicModule[] logic;
    private static PacketSender sender;

    /**
     * Initialize request handler when a request is received by the socket.
     *
     * @param request Packet that is received
     */
    public PacketHandler(DatagramPacket request) {
        requestPacket = request;
    }

    /**
     * Set the static logic module array
     *
     * @param l Logic Module array
     */
    public static void setLogic(LogicModule[] l) {
        logic = l;
    }

    /**
     * Set the static packet sender
     *
     * @param ps Server PacketSender
     */
    public static void setPacketSender(PacketSender ps) {
        sender = ps;
    }

    @Override
    public void run() {
        byte[] data = requestPacket.getData();
        byte dataType = data[0];
        byte room = data[1];
        InetAddress address = requestPacket.getAddress();
        int port = requestPacket.getPort();
        if (room >= Globals.SERVER_ROOMS) {
            Globals.log("DATA_INVALID_ROOM", address.toString() + " Room: " + room, Globals.LOG_TYPE_DATA, true);
            return;
        }
        switch (dataType) {
            case Globals.DATA_PLAYER_LOGIN:
                receivePlayerLogin(data, room, address, port);
                break;
            case Globals.DATA_PLAYER_GET_ALL:
                receivePlayerGetAll(data, room, address, port);
                break;
            case Globals.DATA_PLAYER_SET_MOVE:
                receivePlayerSetMove(data, room);
                break;
            case Globals.DATA_PING:
                receivePing(data, room, address, port);
                break;
            case Globals.DATA_PLAYER_USESKILL:
                receivePlayerUseSkill(data, room);
                break;
            case Globals.DATA_PLAYER_DISCONNECT:
                receivePlayerDisconnect(data, room);
                break;
            case Globals.DATA_PLAYER_GET_NAME:
                receivePlayerGetName(data, room, address, port);
                break;
            case Globals.DATA_PLAYER_GET_STAT:
                receivePlayerGetStat(data, room, address, port);
                break;
            case Globals.DATA_PLAYER_GET_EQUIP:
                receivePlayerGetEquip(data, room, address, port);
                break;
            default:
                Globals.log("DATA_UNKNOWN", address.toString(), Globals.LOG_TYPE_DATA, true);
                break;
        }
    }

    private void receivePlayerGetEquip(byte[] data, byte room, InetAddress address, int port) {
        if (!logic[room].getPlayers().containsKey(data[2])) {
            return;
        }
        byte[] bytes = new byte[Globals.PACKET_BYTE * 2 + Globals.PACKET_INT * Globals.NUM_EQUIP_SLOTS];
        bytes[0] = Globals.DATA_PLAYER_GET_EQUIP;
        bytes[1] = data[2];
        int[] e = logic[room].getPlayers().get(data[2]).getEquip();
        for (int i = 0; i < e.length; i++) {
            byte[] itemCode = Globals.intToByte(e[i]);
            System.arraycopy(itemCode, 0, bytes, i * 4 + 2, itemCode.length);
        }
        sender.sendPlayer(bytes, address, port);
    }

    private void receivePlayerGetStat(byte[] data, byte room, InetAddress address, int port) {
        if (!logic[room].getPlayers().containsKey(data[2])) {
            return;
        }
        byte[] stat = Globals.intToByte((int) logic[room].getPlayers().get(data[2]).getStats()[data[3]]);
        byte[] bytes = new byte[Globals.PACKET_BYTE * 3 + Globals.PACKET_INT];
        bytes[0] = Globals.DATA_PLAYER_GET_STAT;
        bytes[1] = data[2];
        bytes[2] = data[3];
        System.arraycopy(stat, 0, bytes, 3, stat.length);
        sender.sendPlayer(bytes, address, port);
    }

    private void receivePlayerGetName(byte[] data, byte room, InetAddress address, int port) {
        if (!logic[room].getPlayers().containsKey(data[2])) {
            return;
        }
        byte[] name = logic[room].getPlayers().get(data[2]).getPlayerName().getBytes(StandardCharsets.UTF_8);
        byte[] bytes = new byte[Globals.PACKET_BYTE * 2 + name.length];
        bytes[0] = Globals.DATA_PLAYER_GET_NAME;
        bytes[1] = data[2];
        System.arraycopy(name, 0, bytes, 2, name.length);
        sender.sendPlayer(bytes, address, port);
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
        bytes[1] = data[3];
        sender.sendPlayer(bytes, address, port);
    }

    private void receivePlayerUseSkill(byte[] data, byte room) {
        //Globals.log("DATA_PLAYER_USESKILL", "Key: " + data[2] + " Room: " + room, Globals.LOG_TYPE_DATA, true);
        logic[room].queuePlayerUseSkill(data);
    }

    private void receivePlayerLogin(byte[] data, byte room, InetAddress address, int port) {
        Globals.log("DATA_PLAYER_LOGIN", address + ":" + port + " Login Attempt Room: " + room, Globals.LOG_TYPE_DATA, true);
        byte[] temp = new byte[4];
        System.arraycopy(data, 17, temp, 0, temp.length);
        int id = Globals.bytesToInt(temp);

        if (logic[room].containsPlayerID(id)) {
        Globals.log("DATA_PLAYER_LOGIN", address + ":" + port + " uID Already In Room :" + id, Globals.LOG_TYPE_DATA, true);
        return;
        }

        byte freeKey = logic[room].getNextPlayerKey();
        if (freeKey == -1) {
            Globals.log("DATA_PLAYER_LOGIN", address + ":" + port + " Room " + room + " at max capacity", Globals.LOG_TYPE_DATA, true);
            return;
        }

        Player newPlayer = new Player(logic[room], freeKey, address, port, logic[room].getMap(), 0, 0);

        temp = new byte[Globals.MAX_NAME_LENGTH];
        System.arraycopy(data, 2, temp, 0, temp.length);
        newPlayer.setPlayerName(new String(temp, StandardCharsets.UTF_8).trim());

        temp = new byte[4];
        newPlayer.setUniqueID(id);

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

        for (int i = 0; i < Globals.NUM_EQUIP_SLOTS; i++) {
            System.arraycopy(data, i * 4 + 53, temp, 0, temp.length);
            newPlayer.setEquip(i, Globals.bytesToInt(temp));
        }

        for (int i = 0; i < 12; i++) {
            if (data[i * 2 + 97] == -1) {
                continue;
            }
            newPlayer.setSkill(data[i * 2 + 97], data[i * 2 + 98]);
        }
        logic[room].queueAddPlayer(newPlayer);

        byte[] bytes = new byte[Globals.PACKET_BYTE * 4];
        bytes[0] = Globals.DATA_PLAYER_LOGIN;
        bytes[1] = logic[room].getMap().getMapID();
        bytes[2] = freeKey;
        bytes[3] = Globals.SERVER_MAX_PLAYERS;
        sender.sendPlayer(bytes, address, port);
        Globals.log("DATA_PLAYER_LOGIN", address + ":" + port + " Logged in Room " + room + " Key: " + freeKey + " Name: " + newPlayer.getPlayerName(), Globals.LOG_TYPE_DATA, true);
        
        newPlayer.sendPos();
        newPlayer.sendName();
    }

    private void receivePlayerGetAll(byte[] data, byte room, InetAddress address, int port) {
        //Globals.log("DATA_PLAYER_GET_ALL", "Room: " + room, Globals.LOG_TYPE_DATA, true);
        if (!logic[room].getPlayers().containsKey(data[2])) {
            return;
        }
        sender.broadcastAllPlayersUpdate(room);
    }

    private void receivePlayerSetMove(byte[] data, byte room) {
        logic[room].queuePlayerDirKeydown(data);
    }
}
