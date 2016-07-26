package blockfighter.server.net;

import blockfighter.server.Globals;
import blockfighter.server.LogicModule;
import blockfighter.server.entities.mob.Mob;
import blockfighter.server.entities.player.Player;
import com.esotericsoftware.kryonet.Connection;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.UUID;

public class PacketHandler {

    private static LogicModule[] logic;

    /**
     * Set the static Logic Module array
     *
     * @param l Logic Module array
     */
    public static void setLogic(final LogicModule[] l) {
        logic = l;
    }

    public static void process(byte[] data, Connection c) {
        final byte dataType = data[0];
        final byte room = data[1];

        if (!Globals.SERVER_ROOMS.containsKey(room)) {
            Globals.log(PacketHandler.class, "DATA_INVALID_ROOM " + c + " Invalid Room Number. Room: " + room, Globals.LOG_TYPE_DATA, true);
            return;
        }
        final byte roomIndex = Globals.SERVER_ROOMS.get(room);
        switch (dataType) {
            case Globals.DATA_PLAYER_LOGIN:
                receivePlayerLogin(data, roomIndex, c);
                break;
            case Globals.DATA_PLAYER_CREATE:
                receivePlayerCreate(data, roomIndex, c);
                break;
            case Globals.DATA_PLAYER_GET_ALL:
                receivePlayerGetAll(data, roomIndex);
                break;
            case Globals.DATA_PLAYER_SET_MOVE:
                receivePlayerSetMove(data, roomIndex, c);
                break;
            case Globals.DATA_PING:
                receivePing(data, roomIndex, c);
                break;
            case Globals.DATA_PLAYER_USESKILL:
                receivePlayerUseSkill(data, roomIndex, c);
                break;
            case Globals.DATA_PLAYER_DISCONNECT:
                receivePlayerDisconnect(data, roomIndex, c);
                break;
            case Globals.DATA_PLAYER_GET_NAME:
                receivePlayerGetName(data, roomIndex, c);
                break;
            case Globals.DATA_PLAYER_GET_STAT:
                receivePlayerGetStat(data, roomIndex, c);
                break;
            case Globals.DATA_PLAYER_GET_EQUIP:
                receivePlayerGetEquip(data, roomIndex, c);
                break;
            case Globals.DATA_MOB_GET_STAT:
                receiveMobGetStat(data, roomIndex, c);
                break;
            case Globals.DATA_MOB_SET_TYPE:
                receiveMobSetType(data, roomIndex, c);
                break;
            default:
                Globals.log(PacketHandler.class, "DATA_UNKNOWN " + c + " Unknown data type.", Globals.LOG_TYPE_DATA, true);
                break;
        }
    }

    private static void receiveMobGetStat(final byte[] data, final byte roomIndex, final Connection c) {
        if (!logic[roomIndex].getMobs().containsKey(data[2])) {
            return;
        }
        byte[] stat;
        switch (data[3]) {
            case Mob.STAT_MAXHP:
                stat = Globals.intToBytes(10000);
                break;
            case Mob.STAT_MINHP:
                final double[] bStats = logic[roomIndex].getMobs().get(data[2]).getStats();
                final double hpPercent = bStats[Mob.STAT_MINHP] / bStats[Mob.STAT_MAXHP] * 10000;
                stat = Globals.intToBytes((int) hpPercent);
                break;
            default:
                stat = Globals.intToBytes((int) logic[roomIndex].getMobs().get(data[2]).getStats()[data[3]]);
        }
        final byte[] bytes = new byte[Globals.PACKET_BYTE * 3 + Globals.PACKET_INT];
        bytes[0] = Globals.DATA_MOB_GET_STAT;
        bytes[1] = data[2];
        bytes[2] = data[3];
        System.arraycopy(stat, 0, bytes, 3, stat.length);
        PacketSender.sendConnection(bytes, c);
    }

    private static void receiveMobSetType(final byte[] data, final byte roomIndex, final Connection c) {
        if (!logic[roomIndex].getMobs().containsKey(data[2])) {
            return;
        }
        final byte[] bytes = new byte[Globals.PACKET_BYTE * 3 + Globals.PACKET_INT * 2];
        bytes[0] = Globals.DATA_MOB_SET_TYPE;
        bytes[1] = data[2];
        bytes[2] = logic[roomIndex].getMobs().get(data[2]).getType();
        final byte[] posXInt = Globals.intToBytes((int) logic[roomIndex].getMobs().get(data[2]).getX());
        bytes[3] = posXInt[0];
        bytes[4] = posXInt[1];
        bytes[5] = posXInt[2];
        bytes[6] = posXInt[3];
        final byte[] posYInt = Globals.intToBytes((int) logic[roomIndex].getMobs().get(data[2]).getY());
        bytes[7] = posYInt[0];
        bytes[8] = posYInt[1];
        bytes[9] = posYInt[2];
        bytes[10] = posYInt[3];
        PacketSender.sendConnection(bytes, c);
    }

    private static void receivePlayerGetEquip(final byte[] data, final byte roomIndex, final Connection c) {
        if (!logic[roomIndex].getPlayers().containsKey(data[2])) {
            return;
        }
        final byte[] bytes = new byte[Globals.PACKET_BYTE * 2 + Globals.PACKET_INT * Globals.NUM_EQUIP_SLOTS];
        bytes[0] = Globals.DATA_PLAYER_GET_EQUIP;
        bytes[1] = data[2];
        final int[] e = logic[roomIndex].getPlayers().get(data[2]).getEquips();
        for (int i = 0; i < e.length; i++) {
            final byte[] itemCode = Globals.intToBytes(e[i]);
            System.arraycopy(itemCode, 0, bytes, i * 4 + 2, itemCode.length);
        }
        PacketSender.sendConnection(bytes, c);
    }

    private static void receivePlayerGetStat(final byte[] data, final byte roomIndex, final Connection c) {
        if (!logic[roomIndex].getPlayers().containsKey(data[2])) {
            return;
        }
        final byte[] stat = Globals.intToBytes((int) logic[roomIndex].getPlayers().get(data[2]).getStats()[data[3]]);
        final byte[] bytes = new byte[Globals.PACKET_BYTE * 3 + Globals.PACKET_INT];
        bytes[0] = Globals.DATA_PLAYER_GET_STAT;
        bytes[1] = data[2];
        bytes[2] = data[3];
        System.arraycopy(stat, 0, bytes, 3, stat.length);
        PacketSender.sendConnection(bytes, c);
    }

    private static void receivePlayerGetName(final byte[] data, final byte roomIndex, final Connection c) {
        if (!logic[roomIndex].getPlayers().containsKey(data[2])) {
            return;
        }
        final byte[] name = logic[roomIndex].getPlayers().get(data[2]).getPlayerName().getBytes(StandardCharsets.UTF_8);
        final byte[] bytes = new byte[Globals.PACKET_BYTE * 2 + name.length];
        bytes[0] = Globals.DATA_PLAYER_GET_NAME;
        bytes[1] = data[2];
        System.arraycopy(name, 0, bytes, 2, name.length);
        PacketSender.sendConnection(bytes, c);
    }

    private static void receivePlayerDisconnect(final byte[] data, final byte roomIndex, final Connection c) {
        Player p = logic[roomIndex].getPlayers().get(data[2]);
        if (p != null && p.getConnection() == c) {
            Globals.log(PacketHandler.class, "DATA_PLAYER_DISCONNECT", "Disconnecting <" + p.getPlayerName() + "> Key: " + data[2], Globals.LOG_TYPE_DATA, true);
            p.disconnect();
        }
    }

    private static void receivePing(final byte[] data, final byte roomIndex, final Connection c) {
        // Globals.log("DATA_PING", address.toString(), Globals.LOG_TYPE_DATA, true);
        if (!logic[roomIndex].getPlayers().containsKey(data[2])) {
            return;
        }
        final byte[] bytes = new byte[Globals.PACKET_BYTE * 2];
        bytes[0] = Globals.DATA_PING;
        bytes[1] = data[3];
        PacketSender.sendConnection(bytes, c);
    }

    private static void receivePlayerUseSkill(final byte[] data, final byte roomIndex, final Connection c) {
        Player p = logic[roomIndex].getPlayers().get(data[2]);
        if (p != null && p.getConnection() == c) {
            logic[roomIndex].queuePlayerUseSkill(data);
        }
    }

    private static void receivePlayerCreate(final byte[] data, final byte roomIndex, final Connection c) {
        byte[] temp = new byte[8];
        long leastSigBit, mostSigBit;
        int pos = 17;
        final LogicModule lm = logic[roomIndex];

        System.arraycopy(data, pos, temp, 0, temp.length);
        pos += temp.length;
        leastSigBit = Globals.bytesToLong(temp);

        System.arraycopy(data, pos, temp, 0, temp.length);
        pos += temp.length;
        mostSigBit = Globals.bytesToLong(temp);

        final UUID id = new UUID(mostSigBit, leastSigBit);

        if (lm.containsPlayerID(id)) {
            if (lm.getPlayers().get(lm.getPlayerKey(id)).getConnection().getID() == c.getID()) {
                final byte[] bytes = new byte[Globals.PACKET_BYTE * 4];
                bytes[0] = Globals.DATA_PLAYER_CREATE;
                bytes[1] = lm.getMap().getMapID();
                bytes[2] = lm.getPlayerKey(id);
                bytes[3] = Globals.SERVER_MAX_PLAYERS;
                PacketSender.sendConnection(bytes, c);
                Globals.log(PacketHandler.class, "DATA_PLAYER_CREATE " + c + " Resending player <" + lm.getPlayers().get(lm.getPlayerKey(id)).getPlayerName() + "> creation confirmation. uid: " + id, Globals.LOG_TYPE_DATA, true);
                return;
            }
        }

        final byte freeKey = lm.getNextPlayerKey();
        if (freeKey == -1) {
            Globals.log(PacketHandler.class, "DATA_PLAYER_CREATE " + c + " Room " + lm.getRoom() + " at max capacity.", Globals.LOG_TYPE_DATA, true);
            return;
        }

        Globals.log(PacketHandler.class, "DATA_PLAYER_CREATE " + c + " Creating a new player. Room: " + lm.getRoom(), Globals.LOG_TYPE_DATA, true);

        final Player newPlayer = new Player(lm, freeKey, c, lm.getMap());

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
        desc += "Equips=" + Arrays.toString(newPlayer.getEquips()) + "\n";
        Globals.log(PacketHandler.class, "DATA_PLAYER_CREATE " + c + " Queueing new player <" + newPlayer.getPlayerName() + "> into room " + roomIndex + ". Key: " + freeKey + desc, Globals.LOG_TYPE_DATA, true);

        final byte[] bytes = new byte[Globals.PACKET_BYTE * 4];
        bytes[0] = Globals.DATA_PLAYER_CREATE;
        bytes[1] = lm.getMap().getMapID();
        bytes[2] = freeKey;
        bytes[3] = Globals.SERVER_MAX_PLAYERS;
        PacketSender.sendConnection(bytes, c);
        GameServer.addPlayerConnection(c, newPlayer);

        Globals.log(PacketHandler.class, "DATA_PLAYER_CREATE " + c + " Sent <" + newPlayer.getPlayerName() + "> creation confirmation.  Room: " + lm.getRoom() + " Key: " + freeKey, Globals.LOG_TYPE_DATA, true);
        newPlayer.sendPos();
        newPlayer.sendName();
        newPlayer.sendStat(Globals.STAT_MAXHP);
    }

    private static void receivePlayerLogin(final byte[] data, final byte roomIndex, final Connection c) {
        byte[] temp = new byte[8];
        long leastSigBit, mostSigBit;
        int pos = 2;
        final LogicModule lm = logic[roomIndex];
        Globals.log(PacketHandler.class, "DATA_PLAYER_LOGIN " + c + " Login Attempt Room: " + lm.getRoom(), Globals.LOG_TYPE_DATA, true);

        System.arraycopy(data, pos, temp, 0, temp.length);
        pos += temp.length;
        leastSigBit = Globals.bytesToLong(temp);

        System.arraycopy(data, pos, temp, 0, temp.length);
        pos += temp.length;
        mostSigBit = Globals.bytesToLong(temp);

        final UUID id = new UUID(mostSigBit, leastSigBit);
        if (lm.containsPlayerID(id)) {
            final byte[] bytes = new byte[Globals.PACKET_BYTE * 2];
            bytes[0] = Globals.DATA_PLAYER_LOGIN;
            bytes[1] = Globals.LOGIN_FAIL_UID_IN_ROOM;
            PacketSender.sendConnection(bytes, c);
            Globals.log(PacketHandler.class, "DATA_PLAYER_LOGIN " + c + " Failed to login - <" + lm.getPlayers().get(lm.getPlayerKey(id)).getPlayerName() + "> is already in room. uID: " + id, Globals.LOG_TYPE_DATA, true);
            return;
        }

        if (lm.isFull()) {
            final byte[] bytes = new byte[Globals.PACKET_BYTE * 2];
            bytes[0] = Globals.DATA_PLAYER_LOGIN;
            bytes[1] = Globals.LOGIN_FAIL_FULL_ROOM;
            PacketSender.sendConnection(bytes, c);
            Globals.log(PacketHandler.class, "DATA_PLAYER_LOGIN " + c + " Failed to login - Room " + lm.getRoom() + " is at max capacity", Globals.LOG_TYPE_DATA, true);
            return;
        }

        temp = new byte[4];
        System.arraycopy(data, pos, temp, 0, temp.length);
        final int level = Globals.bytesToInt(temp);

        if (!lm.isInLevelRange(level)) {
            final byte[] bytes = new byte[Globals.PACKET_BYTE * 2];
            bytes[0] = Globals.DATA_PLAYER_LOGIN;
            bytes[1] = Globals.LOGIN_FAIL_OUTSIDE_LEVEL_RANGE;
            PacketSender.sendConnection(bytes, c);
            Globals.log(PacketHandler.class, "DATA_PLAYER_LOGIN " + c + " Failed to login - Level " + level + " is outside room " + roomIndex + " level range.", Globals.LOG_TYPE_DATA, true);
            return;
        }

        final byte[] bytes = new byte[Globals.PACKET_BYTE * 4];
        bytes[0] = Globals.DATA_PLAYER_LOGIN;
        bytes[1] = Globals.LOGIN_SUCCESS;
        bytes[2] = Globals.GAME_MAJOR_VERSION;
        bytes[3] = Globals.GAME_MINOR_VERSION;
        PacketSender.sendConnection(bytes, c);
        Globals.log(PacketHandler.class, "DATA_PLAYER_LOGIN " + c + " Logged in. Sent Version Data: " + Globals.GAME_MAJOR_VERSION + "." + Globals.GAME_MINOR_VERSION, Globals.LOG_TYPE_DATA, true);
    }

    private static void receivePlayerGetAll(final byte[] data, final byte roomIndex) {
        if (!logic[roomIndex].getPlayers().containsKey(data[2])) {
            return;
        }
        PacketSender.broadcastAllPlayersUpdate(roomIndex);
    }

    private static void receivePlayerSetMove(final byte[] data, final byte roomIndex, final Connection c) {
        Player p = logic[roomIndex].getPlayers().get(data[2]);
        if (p != null && p.getConnection() == c) {
            logic[roomIndex].queuePlayerDirKeydown(data);
        }
    }
}
