package towerconquestperformancetest;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.charset.StandardCharsets;

public class TestPacketSender {

    private DatagramSocket socket = null;

    public void setSocket(final DatagramSocket s) {
        socket = s;
    }

    public void sendPlayerLogin(final byte room, final TestSaveData c) {
        final byte[] bytes = new byte[Globals.PACKET_BYTE * 2 // Data type + room
                + Globals.PACKET_LONG * 2 // uID
                + Globals.PACKET_INT //Level
                ];
        bytes[0] = Globals.DATA_PLAYER_LOGIN;
        bytes[1] = room;

        byte[] temp = Globals.longToBytes(c.getUniqueID().getLeastSignificantBits());
        System.arraycopy(temp, 0, bytes, 2, temp.length);

        temp = Globals.longToBytes(c.getUniqueID().getMostSignificantBits());
        System.arraycopy(temp, 0, bytes, 10, temp.length);

        double[] stats = c.getTotalStats();
        temp = Globals.intToBytes((int) stats[Globals.STAT_LEVEL]);
        System.arraycopy(temp, 0, bytes, 18, temp.length);

        final DatagramPacket requestPacket = createPacket(bytes);
        sendPacket(requestPacket);
    }

    public void sendPlayerCreate(final byte room, final TestSaveData c) {
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

        final DatagramPacket requestPacket = createPacket(bytes);
        sendPacket(requestPacket);
    }

    public void sendGetAll(final byte room, final byte myKey) {
        final byte[] bytes = new byte[Globals.PACKET_BYTE * 3];
        bytes[0] = Globals.DATA_PLAYER_GET_ALL;
        bytes[1] = room;
        bytes[2] = myKey;
        final DatagramPacket requestPacket = createPacket(bytes);
        sendPacket(requestPacket);
    }

    public void sendMove(final byte room, final byte key, final byte direction, final boolean move) {
        final byte[] bytes = new byte[Globals.PACKET_BYTE * 5];
        bytes[0] = Globals.DATA_PLAYER_SET_MOVE;
        bytes[1] = room;
        bytes[2] = key;
        bytes[3] = direction;
        bytes[4] = (byte) (move ? 1 : 0);
        final DatagramPacket requestPacket = createPacket(bytes);
        sendPacket(requestPacket);
    }

    public void sendUseSkill(final byte room, final byte key, final byte skillCode) {
        final byte[] bytes = new byte[Globals.PACKET_BYTE * 4];
        bytes[0] = Globals.DATA_PLAYER_USESKILL;
        bytes[1] = room;
        bytes[2] = key;
        bytes[3] = skillCode;
        final DatagramPacket requestPacket = createPacket(bytes);
        sendPacket(requestPacket);
    }

    public void sendGetPing(final byte room, final byte myKey, final byte pID) {
        final byte[] bytes = new byte[Globals.PACKET_BYTE * 4];
        bytes[0] = Globals.DATA_PING;
        bytes[1] = room;
        bytes[2] = myKey;
        bytes[3] = pID;
        final DatagramPacket requestPacket = createPacket(bytes);
        sendPacket(requestPacket);
    }

    public void sendDisconnect(final byte room, final byte myKey) {
        final byte[] bytes = new byte[Globals.PACKET_BYTE * 3];
        bytes[0] = Globals.DATA_PLAYER_DISCONNECT;
        bytes[1] = room;
        bytes[2] = myKey;
        final DatagramPacket requestPacket = createPacket(bytes);
        sendPacket(requestPacket);
    }

    public void sendGetName(final byte room, final byte key) {
        final byte[] bytes = new byte[Globals.PACKET_BYTE * 3];
        bytes[0] = Globals.DATA_PLAYER_GET_NAME;
        bytes[1] = room;
        bytes[2] = key;
        final DatagramPacket requestPacket = createPacket(bytes);
        sendPacket(requestPacket);
    }

    public void sendGetStat(final byte room, final byte key, final byte stat) {
        final byte[] bytes = new byte[Globals.PACKET_BYTE * 4];
        bytes[0] = Globals.DATA_PLAYER_GET_STAT;
        bytes[1] = room;
        bytes[2] = key;
        bytes[3] = stat;
        final DatagramPacket requestPacket = createPacket(bytes);
        sendPacket(requestPacket);
    }

    public void sendGetEquip(final byte room, final byte key) {
        final byte[] bytes = new byte[Globals.PACKET_BYTE * 3];
        bytes[0] = Globals.DATA_PLAYER_GET_EQUIP;
        bytes[1] = room;
        bytes[2] = key;
        final DatagramPacket requestPacket = createPacket(bytes);
        sendPacket(requestPacket);
    }

    private void sendPacket(final DatagramPacket packet) {
        try {
            if (!socket.isClosed()) {
                socket.send(packet);
            }
        } catch (final Exception ex) {
            System.err.println(TestPacketSender.class.getCanonicalName() + ": " + ex.getLocalizedMessage() + "@" + ex.getStackTrace()[0]);
        }
    }

    private static DatagramPacket createPacket(final byte[] bytes) {
        return new DatagramPacket(bytes, bytes.length);
    }
}
