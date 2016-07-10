package blockfighter.server.net;

import blockfighter.server.Globals;
import blockfighter.server.LogicModule;
import blockfighter.server.entities.mob.Mob;
import blockfighter.server.entities.player.Player;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

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
        if (room >= Globals.SERVER_ROOMS || room < 0) {
            Globals.log(PacketHandler.class, "DATA_INVALID_ROOM", address, port, "Invalid Room Number. Room: " + room);
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
            case Globals.DATA_MOB_GET_STAT:
                receiveMobGetStat(data, room, address, port);
                break;
            case Globals.DATA_MOB_SET_TYPE:
                receiveMobSetType(data, room, address, port);
                break;
            default:
                Globals.log(PacketHandler.class, "DATA_UNKNOWN", address, port, "Unknown data type.");
                break;
        }
    }

    private void receiveMobGetStat(final byte[] data, final byte room, final InetAddress address, final int port) {
        if (!logic[room].getMobs().containsKey(data[2])) {
            return;
        }
        byte[] stat;
        switch (data[3]) {
            case Mob.STAT_MAXHP:
                stat = Globals.intToBytes(10000);
                break;
            case Mob.STAT_MINHP:
                final double[] bStats = logic[room].getMobs().get(data[2]).getStats();
                final double hpPercent = bStats[Mob.STAT_MINHP] / bStats[Mob.STAT_MAXHP] * 10000;
                stat = Globals.intToBytes((int) hpPercent);
                break;
            default:
                stat = Globals.intToBytes((int) logic[room].getMobs().get(data[2]).getStats()[data[3]]);
        }
        final byte[] bytes = new byte[Globals.PACKET_BYTE * 3 + Globals.PACKET_INT];
        bytes[0] = Globals.DATA_MOB_GET_STAT;
        bytes[1] = data[2];
        bytes[2] = data[3];
        System.arraycopy(stat, 0, bytes, 3, stat.length);
        sender.sendAddress(bytes, address, port);
    }

    private void receiveMobSetType(final byte[] data, final byte room, final InetAddress address, final int port) {
        if (!logic[room].getMobs().containsKey(data[2])) {
            return;
        }
        final byte[] bytes = new byte[Globals.PACKET_BYTE * 3 + Globals.PACKET_INT * 2];
        bytes[0] = Globals.DATA_MOB_SET_TYPE;
        bytes[1] = data[2];
        bytes[2] = logic[room].getMobs().get(data[2]).getType();
        final byte[] posXInt = Globals.intToBytes((int) logic[room].getMobs().get(data[2]).getX());
        bytes[3] = posXInt[0];
        bytes[4] = posXInt[1];
        bytes[5] = posXInt[2];
        bytes[6] = posXInt[3];
        final byte[] posYInt = Globals.intToBytes((int) logic[room].getMobs().get(data[2]).getY());
        bytes[7] = posYInt[0];
        bytes[8] = posYInt[1];
        bytes[9] = posYInt[2];
        bytes[10] = posYInt[3];
        sender.sendAddress(bytes, address, port);
    }

    private void receivePlayerGetEquip(final byte[] data, final byte room, final InetAddress address, final int port) {
        if (!logic[room].getPlayers().containsKey(data[2])) {
            return;
        }
        final byte[] bytes = new byte[Globals.PACKET_BYTE * 2 + Globals.PACKET_INT * Globals.NUM_EQUIP_SLOTS];
        bytes[0] = Globals.DATA_PLAYER_GET_EQUIP;
        bytes[1] = data[2];
        final int[] e = logic[room].getPlayers().get(data[2]).getEquips();
        for (int i = 0; i < e.length; i++) {
            final byte[] itemCode = Globals.intToBytes(e[i]);
            System.arraycopy(itemCode, 0, bytes, i * 4 + 2, itemCode.length);
        }
        sender.sendAddress(bytes, address, port);
    }

    private void receivePlayerGetStat(final byte[] data, final byte room, final InetAddress address, final int port) {
        if (!logic[room].getPlayers().containsKey(data[2])) {
            return;
        }
        final byte[] stat = Globals.intToBytes((int) logic[room].getPlayers().get(data[2]).getStats()[data[3]]);
        final byte[] bytes = new byte[Globals.PACKET_BYTE * 3 + Globals.PACKET_INT];
        bytes[0] = Globals.DATA_PLAYER_GET_STAT;
        bytes[1] = data[2];
        bytes[2] = data[3];
        System.arraycopy(stat, 0, bytes, 3, stat.length);
        sender.sendAddress(bytes, address, port);
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
        sender.sendAddress(bytes, address, port);
    }

    private void receivePlayerDisconnect(final byte[] data, final byte room) {
        if (logic[room].getPlayers().get(data[2]) == null) {
            return;
        }
        logic[room].getPlayers().get(data[2]).disconnect();
        Globals.log(PacketHandler.class, "DATA_PLAYER_DISCONNECT",
                logic[room].getPlayers().get(data[2]).getAddress(),
                logic[room].getPlayers().get(data[2]).getPort(),
                "Disconnected <" + logic[room].getPlayers().get(data[2]).getPlayerName() + "> Key: " + data[2]);
    }

    private void receivePing(final byte[] data, final byte room, final InetAddress address, final int port) {
        // Globals.log("DATA_PING", address.toString(), Globals.LOG_TYPE_DATA, true);
        if (!logic[room].getPlayers().containsKey(data[2])) {
            return;
        }
        final byte[] bytes = new byte[Globals.PACKET_BYTE * 2];
        bytes[0] = Globals.DATA_PING;
        bytes[1] = data[3];
        sender.sendAddress(bytes, address, port);
    }

    private void receivePlayerUseSkill(final byte[] data, final byte room) {
        logic[room].queuePlayerUseSkill(data);
    }

    private void receivePlayerCreate(final byte[] data, final byte room, final InetAddress address, final int port) {
        byte[] temp = new byte[8];
        long leastSigBit, mostSigBit;
        int pos = 17;
        final LogicModule lm = logic[room];

        System.arraycopy(data, pos, temp, 0, temp.length);
        pos += temp.length;
        leastSigBit = Globals.bytesToLong(temp);

        System.arraycopy(data, pos, temp, 0, temp.length);
        pos += temp.length;
        mostSigBit = Globals.bytesToLong(temp);

        final UUID id = new UUID(mostSigBit, leastSigBit);

        if (lm.containsPlayerID(id)) {
            if (lm.getPlayers().get(lm.getPlayerKey(id)).getAddress().equals(address)
                    && lm.getPlayers().get(lm.getPlayerKey(id)).getPort() == port) {
                final byte[] bytes = new byte[Globals.PACKET_BYTE * 4];
                bytes[0] = Globals.DATA_PLAYER_CREATE;
                bytes[1] = lm.getMap().getMapID();
                bytes[2] = lm.getPlayerKey(id);
                bytes[3] = Globals.SERVER_MAX_PLAYERS;
                sender.sendAddress(bytes, address, port);
                Globals.log(PacketHandler.class, "DATA_PLAYER_CREATE", address, port, "Resending player <" + lm.getPlayers().get(lm.getPlayerKey(id)).getPlayerName() + "> creation confirmation. uid: " + id);
                return;
            }
        }

        if (lm.containsPlayerID(id)) {
            Globals.log(PacketHandler.class, "DATA_PLAYER_LOGIN", address, port, "<" + lm.getPlayers().get(lm.getPlayerKey(id)).getPlayerName() + "> uID already in room. uID: " + id);
            return;
        }

        final byte freeKey = lm.getNextPlayerKey();
        if (freeKey == -1) {
            Globals.log(PacketHandler.class, "DATA_PLAYER_CREATE", address, port, "Room " + room + " at max capacity.");
            return;
        }

        Globals.log(PacketHandler.class, "DATA_PLAYER_CREATE", address, port, "Creating a new player. Room: " + room);

        final Player newPlayer = new Player(lm, freeKey, address, port, lm.getMap());

        temp = new byte[Globals.MAX_NAME_LENGTH];
        System.arraycopy(data, 2, temp, 0, temp.length);
        newPlayer.setPlayerName(new String(temp, StandardCharsets.UTF_8).trim());

        newPlayer.setUniqueID(id);

        temp = new byte[4];
        System.arraycopy(data, pos, temp, 0, temp.length);
        pos += temp.length;
        newPlayer.setStat(Globals.STAT_LEVEL, Globals.bytesToInt(temp));
        System.arraycopy(data, pos, temp, 0, temp.length);
        pos += temp.length;
        newPlayer.setStat(Globals.STAT_POWER, Globals.bytesToInt(temp));
        System.arraycopy(data, pos, temp, 0, temp.length);
        pos += temp.length;
        newPlayer.setStat(Globals.STAT_DEFENSE, Globals.bytesToInt(temp));
        System.arraycopy(data, pos, temp, 0, temp.length);
        pos += temp.length;
        newPlayer.setStat(Globals.STAT_SPIRIT, Globals.bytesToInt(temp));

        System.arraycopy(data, pos, temp, 0, temp.length);
        pos += temp.length;
        newPlayer.setBonusStat(Globals.STAT_ARMOR, Globals.bytesToInt(temp));
        System.arraycopy(data, pos, temp, 0, temp.length);
        pos += temp.length;
        newPlayer.setBonusStat(Globals.STAT_REGEN, Globals.bytesToInt(temp) / 10D);
        System.arraycopy(data, pos, temp, 0, temp.length);
        pos += temp.length;
        newPlayer.setBonusStat(Globals.STAT_CRITDMG, Globals.bytesToInt(temp) / 10000D);
        System.arraycopy(data, pos, temp, 0, temp.length);
        pos += temp.length;
        newPlayer.setBonusStat(Globals.STAT_CRITCHANCE, Globals.bytesToInt(temp) / 10000D);

        for (int i = 0; i < Globals.NUM_EQUIP_SLOTS; i++) {
            System.arraycopy(data, pos, temp, 0, temp.length);
            pos += temp.length;
            newPlayer.setEquip(i, Globals.bytesToInt(temp));
        }

        for (int i = 0; i < 12; i++) {
            temp = new byte[2];
            System.arraycopy(data, pos, temp, 0, temp.length);
            pos += temp.length;
            if (temp[0] == -1) {
                continue;
            }
            newPlayer.setSkill(temp[0], temp[1]);
        }

        lm.queueAddPlayer(newPlayer);
        String desc = "\n";
        desc += "Name: " + newPlayer.getPlayerName() + "\n";
        desc += "ID: " + newPlayer.getUniqueID() + "\n";
        for (byte i = 0; i < newPlayer.getStats().length; i++) {
            desc += Globals.getStatName(i) + ": " + newPlayer.getStats()[i] + "\n";
        }
        desc += "Equips=[";
        for (byte i = 0; i < newPlayer.getEquips().length; i++) {
            desc += newPlayer.getEquips()[i] + ",";
        }
        desc += "]\n";
        Globals.log(PacketHandler.class, "DATA_PLAYER_CREATE", address, port, "Queueing new player. Room: " + room + " Key: " + freeKey + desc);

        final byte[] bytes = new byte[Globals.PACKET_BYTE * 4];
        bytes[0] = Globals.DATA_PLAYER_CREATE;
        bytes[1] = lm.getMap().getMapID();
        bytes[2] = freeKey;
        bytes[3] = Globals.SERVER_MAX_PLAYERS;
        sender.sendAddress(bytes, address, port);
        Globals.log(PacketHandler.class, "DATA_PLAYER_CREATE",
                address, port, "Sent <" + newPlayer.getPlayerName() + "> creation confirmation. Room: " + room + " Key: " + freeKey);
        newPlayer.sendPos();
        newPlayer.sendName();
        newPlayer.sendStat(Globals.STAT_MAXHP);
    }

    private void receivePlayerLogin(final byte[] data, final byte room, final InetAddress address, final int port) {
        Globals.log(PacketHandler.class, "DATA_PLAYER_LOGIN", address, port, "Login Attempt Room: " + room);

        final byte[] bytes = new byte[Globals.PACKET_BYTE * 3];
        bytes[0] = Globals.DATA_PLAYER_LOGIN;
        bytes[1] = Globals.GAME_MAJOR_VERSION;
        bytes[2] = Globals.GAME_MINOR_VERSION;
        sender.sendAddress(bytes, address, port);
        Globals.log(PacketHandler.class, "DATA_PLAYER_LOGIN", address, port, "Logged in. Sent Version Data: " + Globals.GAME_MAJOR_VERSION + "." + Globals.GAME_MINOR_VERSION);
    }

    private void receivePlayerGetAll(final byte[] data, final byte room) {
        // Globals.log(PacketHandler.class,"DATA_PLAYER_GET_ALL", "Room: " + room, Globals.LOG_TYPE_DATA, true);
        if (!logic[room].getPlayers().containsKey(data[2])) {
            return;
        }
        sender.broadcastAllPlayersUpdate(room);
    }

    private void receivePlayerSetMove(final byte[] data, final byte room) {
        logic[room].queuePlayerDirKeydown(data);
    }
}
