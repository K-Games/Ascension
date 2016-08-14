package blockfighter.server.net;

import blockfighter.server.Globals;
import blockfighter.server.LogicModule;
import blockfighter.server.Room;
import blockfighter.server.entities.mob.Mob;
import blockfighter.server.entities.player.Player;
import com.esotericsoftware.kryonet.Connection;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PacketHandler {

    private static LogicModule[] logic;

    public static void setLogic(final LogicModule[] l) {
        logic = l;
    }

    public static void process(byte[] data, Connection c) {
        final byte dataType = data[0];
        final byte roomNum = data[1];

        if (!Globals.SERVER_ROOMNUM_TO_ROOMINDEX.containsKey(roomNum)) {
            Globals.log(PacketHandler.class, "DATA_INVALID_ROOM " + c + " Invalid Room Number. Room: " + roomNum, Globals.LOG_TYPE_DATA, true);
            return;
        }
        final byte roomIndex = Globals.SERVER_ROOMNUM_TO_ROOMINDEX.get(roomNum);
        switch (dataType) {
            case Globals.DATA_PLAYER_LOGIN:
                try {
                    receivePlayerLogin(data, roomIndex, c);
                } catch (Exception e) {
                    Globals.logError(e.getStackTrace()[0].toString(), e, true);
                    c.close();
                }
                break;
            case Globals.DATA_PLAYER_CREATE:
                try {
                    receivePlayerCreate(data, roomIndex, c);
                } catch (Exception e) {
                    Globals.logError(e.getStackTrace()[0].toString(), e, true);
                    c.close();
                }
                break;
            case Globals.DATA_PLAYER_GET_ALL:
                receivePlayerGetAll(data, roomIndex, c);
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
                Globals.log(PacketHandler.class, "DATA_UNKNOWN " + c + " Unknown data type: " + dataType, Globals.LOG_TYPE_DATA, true);
                break;
        }
    }

    private static void receiveMobGetStat(final byte[] data, final byte roomIndex, final Connection c) {
        final ConcurrentHashMap<Integer, Mob> mobs = logic[roomIndex].getRoom().getMobs();
        final int key = Globals.bytesToInt(Arrays.copyOfRange(data, 2, 2 + Globals.PACKET_INT));
        final byte statID = data[6];
        if (!mobs.containsKey(key)) {
            return;
        }
        byte[] stat;
        switch (statID) {
            case Mob.STAT_MAXHP:
                stat = Globals.intToBytes(10000);
                break;
            case Mob.STAT_MINHP:
                final double[] bStats = mobs.get(key).getStats();
                final double hpPercent = bStats[Mob.STAT_MINHP] / bStats[Mob.STAT_MAXHP] * 10000;
                stat = Globals.intToBytes((int) hpPercent);
                break;
            default:
                stat = Globals.intToBytes((int) mobs.get(key).getStats()[statID]);
        }
        final byte[] bytes = new byte[Globals.PACKET_BYTE * 2 + Globals.PACKET_INT * 2];
        bytes[0] = Globals.DATA_MOB_GET_STAT;
        final byte[] intKey = Globals.intToBytes(key);
        System.arraycopy(intKey, 0, bytes, 1, intKey.length);
        bytes[5] = statID;
        System.arraycopy(stat, 0, bytes, 6, stat.length);
        PacketSender.sendConnection(bytes, c);
    }

    private static void receiveMobSetType(final byte[] data, final byte roomIndex, final Connection c) {
        final ConcurrentHashMap<Integer, Mob> mobs = logic[roomIndex].getRoom().getMobs();
        final int key = Globals.bytesToInt(Arrays.copyOfRange(data, 2, 2 + Globals.PACKET_INT));
        if (!mobs.containsKey(key)) {
            return;
        }
        final byte[] bytes = new byte[Globals.PACKET_BYTE * 2 + Globals.PACKET_INT * 3];
        bytes[0] = Globals.DATA_MOB_SET_TYPE;
        final byte[] intKey = Globals.intToBytes(key);
        System.arraycopy(intKey, 0, bytes, 1, intKey.length);
        bytes[5] = mobs.get(key).getType();
        final byte[] posXInt = Globals.intToBytes((int) mobs.get(key).getX());
        System.arraycopy(posXInt, 0, bytes, 6, posXInt.length);
        final byte[] posYInt = Globals.intToBytes((int) mobs.get(key).getY());
        System.arraycopy(posYInt, 0, bytes, 10, posYInt.length);
        PacketSender.sendConnection(bytes, c);
    }

    private static void receivePlayerGetEquip(final byte[] data, final byte roomIndex, final Connection c) {
        final ConcurrentHashMap<Byte, Player> players = logic[roomIndex].getRoom().getPlayers();
        if (!players.containsKey(data[2])) {
            return;
        }
        final byte[] bytes = new byte[Globals.PACKET_BYTE * 2 + Globals.PACKET_INT * Globals.NUM_EQUIP_SLOTS];
        bytes[0] = Globals.DATA_PLAYER_GET_EQUIP;
        bytes[1] = data[2];
        final int[] e = players.get(data[2]).getEquips();
        for (int i = 0; i < e.length; i++) {
            final byte[] itemCode = Globals.intToBytes(e[i]);
            System.arraycopy(itemCode, 0, bytes, i * 4 + 2, itemCode.length);
        }
        PacketSender.sendConnection(bytes, c);
    }

    private static void receivePlayerGetStat(final byte[] data, final byte roomIndex, final Connection c) {
        final ConcurrentHashMap<Byte, Player> players = logic[roomIndex].getRoom().getPlayers();
        if (!players.containsKey(data[2])) {
            return;
        }
        final byte[] stat = Globals.intToBytes((int) players.get(data[2]).getStats()[data[3]]);
        final byte[] bytes = new byte[Globals.PACKET_BYTE * 3 + Globals.PACKET_INT];
        bytes[0] = Globals.DATA_PLAYER_GET_STAT;
        bytes[1] = data[2];
        bytes[2] = data[3];
        System.arraycopy(stat, 0, bytes, 3, stat.length);
        PacketSender.sendConnection(bytes, c);
    }

    private static void receivePlayerGetName(final byte[] data, final byte roomIndex, final Connection c) {
        final ConcurrentHashMap<Byte, Player> players = logic[roomIndex].getRoom().getPlayers();
        if (!players.containsKey(data[2])) {
            return;
        }
        final byte[] name = players.get(data[2]).getPlayerName().getBytes(StandardCharsets.UTF_8);
        final byte[] bytes = new byte[Globals.PACKET_BYTE * 2 + name.length];
        bytes[0] = Globals.DATA_PLAYER_GET_NAME;
        bytes[1] = data[2];
        System.arraycopy(name, 0, bytes, 2, name.length);
        PacketSender.sendConnection(bytes, c);
    }

    private static void receivePlayerDisconnect(final byte[] data, final byte roomIndex, final Connection c) {
        final ConcurrentHashMap<Byte, Player> players = logic[roomIndex].getRoom().getPlayers();
        final Player p = players.get(data[2]);
        if (p != null && p.getConnection() == c) {
            Globals.log(PacketHandler.class, "DATA_PLAYER_DISCONNECT", "Disconnecting <" + p.getPlayerName() + "> Key: " + data[2], Globals.LOG_TYPE_DATA, true);
            p.disconnect();
        }
    }

    private static void receivePing(final byte[] data, final byte roomIndex, final Connection c) {
        // Globals.log("DATA_PING", address.toString(), Globals.LOG_TYPE_DATA, true);
        final ConcurrentHashMap<Byte, Player> players = logic[roomIndex].getRoom().getPlayers();
        if (!players.containsKey(data[2])) {
            return;
        }
        final byte[] bytes = new byte[Globals.PACKET_BYTE * 2];
        bytes[0] = Globals.DATA_PING;
        bytes[1] = data[3];
        PacketSender.sendConnection(bytes, c);
    }

    private static void receivePlayerUseSkill(final byte[] data, final byte roomIndex, final Connection c) {
        final ConcurrentHashMap<Byte, Player> players = logic[roomIndex].getRoom().getPlayers();
        Player p = players.get(data[2]);
        if (p != null && p.getConnection() == c) {
            logic[roomIndex].queuePlayerUseSkill(data);
        }
    }

    private static void receivePlayerCreate(final byte[] data, final byte roomIndex, final Connection c) {
        long leastSigBit, mostSigBit;
        int pos = 17;
        final Room room = logic[roomIndex].getRoom();

        leastSigBit = Globals.bytesToLong(Arrays.copyOfRange(data, pos, pos + Globals.PACKET_LONG));
        pos += Globals.PACKET_LONG;

        mostSigBit = Globals.bytesToLong(Arrays.copyOfRange(data, pos, pos + Globals.PACKET_LONG));
        pos += Globals.PACKET_LONG;
        final UUID id = new UUID(mostSigBit, leastSigBit);

        if (room.containsPlayerID(id)) {
            if (room.getPlayers().get(room.getPlayerKey(id)).getConnection() == c) {
                final byte[] bytes = new byte[Globals.PACKET_BYTE * 4];
                bytes[0] = Globals.DATA_PLAYER_CREATE;
                bytes[1] = room.getMap().getMapID();
                bytes[2] = room.getPlayerKey(id);
                bytes[3] = Globals.SERVER_MAX_PLAYERS;
                PacketSender.sendConnection(bytes, c);
                Globals.log(PacketHandler.class, "DATA_PLAYER_CREATE " + c + " Resending player <" + room.getPlayers().get(room.getPlayerKey(id)).getPlayerName() + "> creation confirmation. uid: " + id, Globals.LOG_TYPE_DATA, true);
                return;
            }
        }

        final byte freeKey = room.getNextPlayerKey();
        if (freeKey == -1) {
            c.close();
            Globals.log(PacketHandler.class, "DATA_PLAYER_CREATE " + c + " Room " + room.getRoomNumber() + " has no player keys left.", Globals.LOG_TYPE_DATA, true);
            return;
        }

        Globals.log(PacketHandler.class, "DATA_PLAYER_CREATE " + c + " Creating a new player. Room: " + room.getRoomNumber(), Globals.LOG_TYPE_DATA, true);

        final Player newPlayer = new Player(logic[roomIndex], freeKey, c, room.getMap());

        newPlayer.setPlayerName(new String(Arrays.copyOfRange(data, 2, 2 + Globals.MAX_NAME_LENGTH), StandardCharsets.UTF_8).trim());
        newPlayer.setUniqueID(id);

        newPlayer.setStat(Globals.STAT_LEVEL, Globals.bytesToInt(Arrays.copyOfRange(data, pos, pos + Globals.PACKET_INT)));
        pos += Globals.PACKET_INT;
        newPlayer.setStat(Globals.STAT_POWER, Globals.bytesToInt(Arrays.copyOfRange(data, pos, pos + Globals.PACKET_INT)));
        pos += Globals.PACKET_INT;
        newPlayer.setStat(Globals.STAT_DEFENSE, Globals.bytesToInt(Arrays.copyOfRange(data, pos, pos + Globals.PACKET_INT)));
        pos += Globals.PACKET_INT;
        newPlayer.setStat(Globals.STAT_SPIRIT, Globals.bytesToInt(Arrays.copyOfRange(data, pos, pos + Globals.PACKET_INT)));
        pos += Globals.PACKET_INT;

        newPlayer.setBonusStat(Globals.STAT_ARMOR, Globals.bytesToInt(Arrays.copyOfRange(data, pos, pos + Globals.PACKET_INT)));
        pos += Globals.PACKET_INT;
        newPlayer.setBonusStat(Globals.STAT_REGEN, Globals.bytesToInt(Arrays.copyOfRange(data, pos, pos + Globals.PACKET_INT)) / 10D);
        pos += Globals.PACKET_INT;
        newPlayer.setBonusStat(Globals.STAT_CRITDMG, Globals.bytesToInt(Arrays.copyOfRange(data, pos, pos + Globals.PACKET_INT)) / 10000D);
        pos += Globals.PACKET_INT;
        newPlayer.setBonusStat(Globals.STAT_CRITCHANCE, Globals.bytesToInt(Arrays.copyOfRange(data, pos, pos + Globals.PACKET_INT)) / 10000D);
        pos += Globals.PACKET_INT;

        for (int i = 0; i < Globals.NUM_EQUIP_SLOTS; i++) {
            newPlayer.setEquip(i, Globals.bytesToInt(Arrays.copyOfRange(data, pos, pos + Globals.PACKET_INT)));
            pos += Globals.PACKET_INT;
        }

        for (int i = 0; i < 12; i++) {
            final byte[] temp = Arrays.copyOfRange(data, pos, pos + Globals.PACKET_BYTE * 2);
            pos += Globals.PACKET_BYTE * 2;
            if (temp[0] == -1) {
                continue;
            }
            newPlayer.setSkill(temp[0], temp[1]);
        }

        logic[roomIndex].queueAddPlayer(newPlayer);
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
        bytes[1] = room.getMap().getMapID();
        bytes[2] = freeKey;
        bytes[3] = Globals.SERVER_MAX_PLAYERS;
        PacketSender.sendConnection(bytes, c);
        GameServer.addPlayerConnection(c, newPlayer);

        Globals.log(PacketHandler.class, "DATA_PLAYER_CREATE " + c + " Sent <" + newPlayer.getPlayerName() + "> creation confirmation.  Room: " + room.getRoomNumber() + " Key: " + freeKey, Globals.LOG_TYPE_DATA, true);
        newPlayer.sendPos();
        newPlayer.sendName();
        newPlayer.sendStat(Globals.STAT_MAXHP);
    }

    private static void receivePlayerLogin(final byte[] data, final byte roomIndex, final Connection c) {
        long leastSigBit, mostSigBit;
        int pos = 2;
        final Room room = logic[roomIndex].getRoom();
        Globals.log(PacketHandler.class, "DATA_PLAYER_LOGIN " + c + " Login Attempt Room: " + room.getRoomNumber(), Globals.LOG_TYPE_DATA, true);

        leastSigBit = Globals.bytesToLong(Arrays.copyOfRange(data, pos, pos + Globals.PACKET_LONG));
        pos += Globals.PACKET_LONG;

        mostSigBit = Globals.bytesToLong(Arrays.copyOfRange(data, pos, pos + Globals.PACKET_LONG));
        pos += Globals.PACKET_LONG;

        final UUID id = new UUID(mostSigBit, leastSigBit);
        if (room.containsPlayerID(id)) {
            final byte[] bytes = new byte[Globals.PACKET_BYTE * 2];
            bytes[0] = Globals.DATA_PLAYER_LOGIN;
            bytes[1] = Globals.LOGIN_FAIL_UID_IN_ROOM;
            PacketSender.sendConnection(bytes, c);
            Globals.log(PacketHandler.class, "DATA_PLAYER_LOGIN " + c + " Failed to login - <" + room.getPlayers().get(room.getPlayerKey(id)).getPlayerName() + "> is already in room. uID: " + id, Globals.LOG_TYPE_DATA, true);
            return;
        }

        if (room.isFull()) {
            final byte[] bytes = new byte[Globals.PACKET_BYTE * 2];
            bytes[0] = Globals.DATA_PLAYER_LOGIN;
            bytes[1] = Globals.LOGIN_FAIL_FULL_ROOM;
            PacketSender.sendConnection(bytes, c);
            Globals.log(PacketHandler.class, "DATA_PLAYER_LOGIN " + c + " Failed to login - Room " + room.getRoomNumber() + " is at max capacity", Globals.LOG_TYPE_DATA, true);
            return;
        }

        final int level = Globals.bytesToInt(Arrays.copyOfRange(data, pos, pos + Globals.PACKET_INT));

        if (!room.isInLevelRange(level)) {
            final byte[] bytes = new byte[Globals.PACKET_BYTE * 2];
            bytes[0] = Globals.DATA_PLAYER_LOGIN;
            bytes[1] = Globals.LOGIN_FAIL_OUTSIDE_LEVEL_RANGE;
            PacketSender.sendConnection(bytes, c);
            Globals.log(PacketHandler.class, "DATA_PLAYER_LOGIN " + c + " Failed to login - Level " + level + " is outside room " + room.getRoomNumber() + "[Index=" + roomIndex + "] level range.", Globals.LOG_TYPE_DATA, true);
            return;
        }

        final byte[] bytes = new byte[Globals.PACKET_BYTE * 5];
        bytes[0] = Globals.DATA_PLAYER_LOGIN;
        bytes[1] = Globals.LOGIN_SUCCESS;
        bytes[2] = Globals.GAME_MAJOR_VERSION;
        bytes[3] = Globals.GAME_MINOR_VERSION;
        bytes[4] = Globals.GAME_UPDATE_NUMBER;
        PacketSender.sendConnection(bytes, c);
        Globals.log(PacketHandler.class, "DATA_PLAYER_LOGIN " + c + " Logged in. Sent Version Data: " + Globals.GAME_MAJOR_VERSION + "." + Globals.GAME_MINOR_VERSION, Globals.LOG_TYPE_DATA, true);
    }

    private static void receivePlayerGetAll(final byte[] data, final byte roomIndex, final Connection c) {
        final ConcurrentHashMap<Byte, Player> players = logic[roomIndex].getRoom().getPlayers();
        Player p = players.get(data[2]);
        if (p != null && p.getConnection() == c) {
            for (final Map.Entry<Byte, Player> pEntry : players.entrySet()) {
                final Player player = pEntry.getValue();
                player.sendData(p);
            }
        }
    }

    private static void receivePlayerSetMove(final byte[] data, final byte roomIndex, final Connection c) {
        final ConcurrentHashMap<Byte, Player> players = logic[roomIndex].getRoom().getPlayers();
        Player p = players.get(data[2]);
        if (p != null && p.getConnection() == c) {
            logic[roomIndex].queuePlayerDirKeydown(data);
        }
    }
}
