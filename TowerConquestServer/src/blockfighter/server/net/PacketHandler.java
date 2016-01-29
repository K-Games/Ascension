package blockfighter.server.net;

import blockfighter.server.Globals;
import blockfighter.server.LogicModule;
import blockfighter.server.entities.boss.Boss;
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
     * Packet Handler constructor Initialized when a packet is received on the Packet Receiver.
     *
     * @param request Packet that is received
     */
    public PacketHandler(final DatagramPacket request) {
        this.requestPacket = request;
    }

    /**
     * Set the static Logic Module array
     *
     * @param l Logic Module array
     */
    public static void setLogic(final LogicModule[] l) {
        logic = l;
    }

    /**
     * Set the static Packet Sender
     *
     * @param ps Server PacketSender
     */
    public static void setPacketSender(final PacketSender ps) {
        sender = ps;
    }

    @Override
    public void run() {
        final byte[] data = this.requestPacket.getData();
        final byte dataType = data[0];
        final byte room = data[1];
        final InetAddress address = this.requestPacket.getAddress();
        final int port = this.requestPacket.getPort();
        if (room >= Globals.SERVER_ROOMS) {
            Globals.log("DATA_INVALID_ROOM", address.toString() + " Room: " + room, Globals.LOG_TYPE_DATA, true);
            return;
        }
        switch (dataType) {
            case Globals.DATA_PLAYER_LOGIN:
                receivePlayerLogin(data, room, address, port);
                break;
            case Globals.DATA_PLAYER_CREATE:
                receivePlayerCreate(data, room, address, port);
                break;
            case Globals.DATA_PLAYER_GET_ALL:
                receivePlayerGetAll(data, room);
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
            case Globals.DATA_BOSS_GET_STAT:
                receiveBossGetStat(data, room, address, port);
                break;
            case Globals.DATA_BOSS_SET_TYPE:
                receiveBossSetType(data, room, address, port);
                break;
            default:
                Globals.log("DATA_UNKNOWN", address.toString(), Globals.LOG_TYPE_DATA, true);
                break;
        }
    }

    private void receiveBossGetStat(final byte[] data, final byte room, final InetAddress address, final int port) {
        if (!logic[room].getBosses().containsKey(data[2])) {
            return;
        }
        byte[] stat;
        switch (data[3]) {
            case Boss.STAT_MAXHP:
                stat = Globals.intToByte(10000);
                break;
            case Boss.STAT_MINHP:
                final double[] bStats = logic[room].getBosses().get(data[2]).getStats();
                final double hpPercent = bStats[Boss.STAT_MINHP] / bStats[Boss.STAT_MAXHP] * 10000;
                stat = Globals.intToByte((int) hpPercent);
                break;
            default:
                stat = Globals.intToByte((int) logic[room].getBosses().get(data[2]).getStats()[data[3]]);
        }
        final byte[] bytes = new byte[Globals.PACKET_BYTE * 3 + Globals.PACKET_INT];
        bytes[0] = Globals.DATA_BOSS_GET_STAT;
        bytes[1] = data[2];
        bytes[2] = data[3];
        System.arraycopy(stat, 0, bytes, 3, stat.length);
        sender.sendPlayer(bytes, address, port);
    }

    private void receiveBossSetType(final byte[] data, final byte room, final InetAddress address, final int port) {
        if (!logic[room].getBosses().containsKey(data[2])) {
            return;
        }
        final byte[] bytes = new byte[Globals.PACKET_BYTE * 3 + Globals.PACKET_INT * 2];
        bytes[0] = Globals.DATA_BOSS_SET_TYPE;
        bytes[1] = data[2];
        bytes[2] = logic[room].getBosses().get(data[2]).getType();
        final byte[] posXInt = Globals.intToByte((int) logic[room].getBosses().get(data[2]).getX());
        bytes[3] = posXInt[0];
        bytes[4] = posXInt[1];
        bytes[5] = posXInt[2];
        bytes[6] = posXInt[3];
        final byte[] posYInt = Globals.intToByte((int) logic[room].getBosses().get(data[2]).getY());
        bytes[7] = posYInt[0];
        bytes[8] = posYInt[1];
        bytes[9] = posYInt[2];
        bytes[10] = posYInt[3];
        sender.sendPlayer(bytes, address, port);
    }

    private void receivePlayerGetEquip(final byte[] data, final byte room, final InetAddress address, final int port) {
        if (!logic[room].getPlayers().containsKey(data[2])) {
            return;
        }
        final byte[] bytes = new byte[Globals.PACKET_BYTE * 2 + Globals.PACKET_INT * Globals.NUM_EQUIP_SLOTS];
        bytes[0] = Globals.DATA_PLAYER_GET_EQUIP;
        bytes[1] = data[2];
        final int[] e = logic[room].getPlayers().get(data[2]).getEquip();
        for (int i = 0; i < e.length; i++) {
            final byte[] itemCode = Globals.intToByte(e[i]);
            System.arraycopy(itemCode, 0, bytes, i * 4 + 2, itemCode.length);
        }
        sender.sendPlayer(bytes, address, port);
    }

    private void receivePlayerGetStat(final byte[] data, final byte room, final InetAddress address, final int port) {
        if (!logic[room].getPlayers().containsKey(data[2])) {
            return;
        }
        final byte[] stat = Globals.intToByte((int) logic[room].getPlayers().get(data[2]).getStats()[data[3]]);
        final byte[] bytes = new byte[Globals.PACKET_BYTE * 3 + Globals.PACKET_INT];
        bytes[0] = Globals.DATA_PLAYER_GET_STAT;
        bytes[1] = data[2];
        bytes[2] = data[3];
        System.arraycopy(stat, 0, bytes, 3, stat.length);
        sender.sendPlayer(bytes, address, port);
    }

    private void receivePlayerGetName(final byte[] data, final byte room, final InetAddress address, final int port) {
        if (!logic[room].getPlayers().containsKey(data[2])) {
            return;
        }
        final byte[] name = logic[room].getPlayers().get(data[2]).getPlayerName().getBytes(StandardCharsets.UTF_8);
        final byte[] bytes = new byte[Globals.PACKET_BYTE * 2 + name.length];
        bytes[0] = Globals.DATA_PLAYER_GET_NAME;
        bytes[1] = data[2];
        System.arraycopy(name, 0, bytes, 2, name.length);
        sender.sendPlayer(bytes, address, port);
    }

    private void receivePlayerDisconnect(final byte[] data, final byte room) {
        if (logic[room].getPlayers().get(data[2]) == null) {
            return;
        }
        logic[room].getPlayers().get(data[2]).disconnect();
        Globals.log("DATA_PLAYER_DISCONNECT", logic[room].getPlayers().get(data[2]).getAddress() + ":"
                + logic[room].getPlayers().get(data[2]).getPort() + " Disconnected Key: " + data[2], Globals.LOG_TYPE_DATA, true);
    }

    private void receivePing(final byte[] data, final byte room, final InetAddress address, final int port) {
        // Globals.log("DATA_PING", address.toString(), Globals.LOG_TYPE_DATA, true);
        if (!logic[room].getPlayers().containsKey(data[2])) {
            return;
        }
        final byte[] bytes = new byte[Globals.PACKET_BYTE * 2];
        bytes[0] = Globals.DATA_PING;
        bytes[1] = data[3];
        sender.sendPlayer(bytes, address, port);
    }

    private void receivePlayerUseSkill(final byte[] data, final byte room) {
        logic[room].queuePlayerUseSkill(data);
    }

    private void receivePlayerCreate(final byte[] data, final byte room, final InetAddress address, final int port) {
        Globals.log("DATA_PLAYER_CREATE", address + ":" + port + " Attempting to create player. Room: " + room, Globals.LOG_TYPE_DATA,
                true);
        byte[] temp = new byte[4];
        System.arraycopy(data, 17, temp, 0, temp.length);
        final int id = Globals.bytesToInt(temp);

        if (logic[room].containsPlayerID(id)) {
            final LogicModule lm = logic[room];
            if (lm.getPlayers().get(lm.getPlayerKey(id)).getAddress().equals(address)) {
                final byte[] bytes = new byte[Globals.PACKET_BYTE * 4];
                bytes[0] = Globals.DATA_PLAYER_CREATE;
                bytes[1] = lm.getMap().getMapID();
                bytes[2] = lm.getPlayerKey(id);
                bytes[3] = Globals.SERVER_MAX_PLAYERS;
                sender.sendPlayer(bytes, address, port);
                Globals.log("DATA_PLAYER_CREATE", address + ":" + port + " Resending player creation uid: " + id, Globals.LOG_TYPE_DATA,
                        true);
            }
            return;
        }

        final byte freeKey = logic[room].getNextPlayerKey();
        if (freeKey == -1) {
            Globals.log("DATA_PLAYER_CREATE", address + ":" + port + " Room " + room + " at max capacity", Globals.LOG_TYPE_DATA, true);
            return;
        }

        final Player newPlayer = new Player(logic[room], freeKey, address, port, logic[room].getMap());

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
        String desc = "\n";
        desc += "Name: " + newPlayer.getPlayerName() + "\n";
        for (byte i = 0; i < newPlayer.getStats().length; i++) {
            desc += Globals.getStatName(i) + ": " + newPlayer.getStats()[i] + "\n";
        }

        Globals.log("DATA_PLAYER_CREATE", address + ":" + port + " Queueing new player. Room: " + room + " Key: " + freeKey + desc,
                Globals.LOG_TYPE_DATA, true);

        final byte[] bytes = new byte[Globals.PACKET_BYTE * 4];
        bytes[0] = Globals.DATA_PLAYER_CREATE;
        bytes[1] = logic[room].getMap().getMapID();
        bytes[2] = freeKey;
        bytes[3] = Globals.SERVER_MAX_PLAYERS;
        sender.sendPlayer(bytes, address, port);
        Globals.log("DATA_PLAYER_CREATE",
                address + ":" + port + " Sent Creation. Room: " + room + " Key: " + freeKey + " Name: " + newPlayer.getPlayerName(),
                Globals.LOG_TYPE_DATA, true);

        newPlayer.sendPos();
        newPlayer.sendName();
    }

    private void receivePlayerLogin(final byte[] data, final byte room, final InetAddress address, final int port) {
        Globals.log("DATA_PLAYER_LOGIN", address + ":" + port + " Login Attempt Room: " + room, Globals.LOG_TYPE_DATA, true);
        final byte[] temp = new byte[4];
        System.arraycopy(data, 17, temp, 0, temp.length);
        final int id = Globals.bytesToInt(temp);

        if (logic[room].containsPlayerID(id)) {
            Globals.log("DATA_PLAYER_LOGIN", address + ":" + port + " uID Already In Room uID: " + id, Globals.LOG_TYPE_DATA, true);
            return;
        }

        final byte[] bytes = new byte[Globals.PACKET_BYTE * 1];
        bytes[0] = Globals.DATA_PLAYER_LOGIN;
        sender.sendPlayer(bytes, address, port);
        Globals.log("DATA_PLAYER_LOGIN", address + ":" + port + " Logged in", Globals.LOG_TYPE_DATA, true);
    }

    private void receivePlayerGetAll(final byte[] data, final byte room) {
        // Globals.log("DATA_PLAYER_GET_ALL", "Room: " + room, Globals.LOG_TYPE_DATA, true);
        if (!logic[room].getPlayers().containsKey(data[2])) {
            return;
        }
        sender.broadcastAllPlayersUpdate(room);
    }

    private void receivePlayerSetMove(final byte[] data, final byte room) {
        logic[room].queuePlayerDirKeydown(data);
    }
}
