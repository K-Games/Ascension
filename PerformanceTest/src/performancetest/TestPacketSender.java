package performancetest;

import blockfighter.shared.Globals;
import com.esotericsoftware.kryonet.Client;
import java.nio.charset.StandardCharsets;

public class TestPacketSender {

    public static void sendPlayerLogin(final TestSaveData c, final Client client) {
        final byte[] bytes = new byte[Globals.PACKET_BYTE * 1 // Data type + room
                + Globals.PACKET_LONG * 2 // uID
                + Globals.PACKET_INT //Level
                ];
        bytes[0] = Globals.DATA_PLAYER_LOGIN;

        byte[] temp = Globals.longToBytes(c.getUniqueID().getLeastSignificantBits());
        System.arraycopy(temp, 0, bytes, 1, temp.length);

        temp = Globals.longToBytes(c.getUniqueID().getMostSignificantBits());
        System.arraycopy(temp, 0, bytes, 9, temp.length);

        double[] stats = c.getTotalStats();
        temp = Globals.intToBytes((int) stats[Globals.STAT_LEVEL]);
        System.arraycopy(temp, 0, bytes, 17, temp.length);

        sendPacket(bytes, client);
    }

    public static void sendPlayerCreate(final byte room, final TestSaveData c, final Client client) {
        final byte[] bytes = new byte[Globals.PACKET_BYTE * 2 // Data type + room
                + Globals.MAX_NAME_LENGTH // Name length
                + Globals.PACKET_LONG * 2 // uID
                + Globals.PACKET_INT * 8 // Stats
                + Globals.PACKET_INT * 11 // equipments
                + 12 * 2 * Globals.PACKET_BYTE // Hotkey'd skills + level
                ];
        bytes[0] = Globals.DATA_PLAYER_CREATE;
        bytes[1] = room;
        int pos = 2;

        byte[] temp = c.getPlayerName().getBytes(StandardCharsets.UTF_8);
        System.arraycopy(temp, 0, bytes, pos, temp.length);
        pos += Globals.MAX_NAME_LENGTH;

        temp = Globals.longToBytes(c.getUniqueID().getLeastSignificantBits());
        System.arraycopy(temp, 0, bytes, pos, temp.length);
        pos += temp.length;

        temp = Globals.longToBytes(c.getUniqueID().getMostSignificantBits());
        System.arraycopy(temp, 0, bytes, pos, temp.length);
        pos += temp.length;

        double[] stats = c.getTotalStats();
        temp = Globals.intToBytes((int) stats[Globals.STAT_LEVEL]);
        System.arraycopy(temp, 0, bytes, pos, temp.length);
        pos += temp.length;

        temp = Globals.intToBytes((int) stats[Globals.STAT_POWER]);
        System.arraycopy(temp, 0, bytes, pos, temp.length);
        pos += temp.length;

        temp = Globals.intToBytes((int) stats[Globals.STAT_DEFENSE]);
        System.arraycopy(temp, 0, bytes, pos, temp.length);
        pos += temp.length;

        temp = Globals.intToBytes((int) stats[Globals.STAT_SPIRIT]);
        System.arraycopy(temp, 0, bytes, pos, temp.length);
        pos += temp.length;

        stats = c.getTotalStats();
        temp = Globals.intToBytes((int) stats[Globals.STAT_ARMOR]);
        System.arraycopy(temp, 0, bytes, pos, temp.length);
        pos += temp.length;

        temp = Globals.intToBytes((int) (stats[Globals.STAT_REGEN] * 10));
        System.arraycopy(temp, 0, bytes, pos, temp.length);
        pos += temp.length;

        temp = Globals.intToBytes((int) (stats[Globals.STAT_CRITDMG] * 10000));
        System.arraycopy(temp, 0, bytes, pos, temp.length);
        pos += temp.length;

        temp = Globals.intToBytes((int) (stats[Globals.STAT_CRITCHANCE] * 10000));
        System.arraycopy(temp, 0, bytes, pos, temp.length);
        pos += temp.length;

        sendPacket(bytes, client);
    }

    public static void sendMove(final byte room, final byte key, final byte direction, final boolean move, final Client client) {
        final byte[] bytes = new byte[Globals.PACKET_BYTE * 5];
        bytes[0] = Globals.DATA_PLAYER_SET_MOVE;
        bytes[1] = room;
        bytes[2] = key;
        bytes[3] = direction;
        bytes[4] = (byte) (move ? 1 : 0);
        sendPacket(bytes, client);
    }

    public static void sendDisconnect(final byte room, final byte myKey, final Client client) {
        final byte[] bytes = new byte[Globals.PACKET_BYTE * 3];
        bytes[0] = Globals.DATA_PLAYER_DISCONNECT;
        bytes[1] = room;
        bytes[2] = myKey;
        sendPacket(bytes, client);
    }

    public static void sendGetName(final byte room, final byte key, final Client client) {
        final byte[] bytes = new byte[Globals.PACKET_BYTE * 3];
        bytes[0] = Globals.DATA_PLAYER_GET_NAME;
        bytes[1] = room;
        bytes[2] = key;
        sendPacket(bytes, client);
    }

    public static void sendGetPing(final byte room, final byte myKey, final byte pID, final Client client) {
        final byte[] bytes = new byte[Globals.PACKET_BYTE * 4];
        bytes[0] = Globals.DATA_PING;
        bytes[1] = room;
        bytes[2] = myKey;
        bytes[3] = pID;
        sendPacket(bytes, client);
    }

    private static void sendPacket(final byte[] packet, final Client client) {
        if (Globals.UDP_MODE) {
            client.sendUDP(packet);
        } else {
            client.sendTCP(packet);
        }
    }
}
