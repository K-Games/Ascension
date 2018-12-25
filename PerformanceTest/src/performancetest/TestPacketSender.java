package performancetest;

import blockfighter.client.entities.items.ItemEquip;
import blockfighter.shared.Globals;
import com.esotericsoftware.kryonet.Client;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

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
                + Globals.PACKET_INT * 11 // Stats
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

        temp = Globals.intToBytes(0);
        System.arraycopy(temp, 0, bytes, pos, temp.length);
        pos += temp.length;

        temp = Globals.intToBytes(0);
        System.arraycopy(temp, 0, bytes, pos, temp.length);
        pos += temp.length;

        temp = Globals.intToBytes(0);
        System.arraycopy(temp, 0, bytes, pos, temp.length);
        pos += temp.length;

        temp = Globals.intToBytes((int) stats[Globals.STAT_ARMOUR]);
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

        final ItemEquip[] equip = new ItemEquip[Globals.NUM_EQUIP_SLOTS];
        equip[Globals.EQUIP_WEAPON] = new ItemEquip((c.getAI() == 0) ? 120000 : 100000);
        equip[Globals.EQUIP_OFFHAND] = new ItemEquip((c.getAI() == 0) ? 0 : 110000);
        for (ItemEquip equip1 : equip) {
            if (equip1 == null) {
                temp = Globals.intToBytes(0);
                System.arraycopy(temp, 0, bytes, pos, temp.length);
                pos += temp.length;
                continue;
            }
            temp = Globals.intToBytes(equip1.getItemCode());
            System.arraycopy(temp, 0, bytes, pos, temp.length);
            pos += temp.length;
        }

        final HashMap<Byte, Byte> skills = new HashMap<>(12);
        for (byte i = 0; i < Main.TEST_SKILLS[c.getAI()].length; i++) {
            skills.put(i, Main.TEST_SKILLS[c.getAI()][i]);
        }

        for (byte i = 0; i < Globals.NUM_HOTKEYS; i++) {
            Byte skill = skills.get(i);
            temp = new byte[2];
            if (skill == null) {
                temp[0] = -1;
                temp[1] = 0;
                System.arraycopy(temp, 0, bytes, pos, temp.length);
                pos += temp.length;
                continue;
            }
            temp[0] = skill;
            temp[1] = (byte) (Math.random() * 31);
            System.arraycopy(temp, 0, bytes, pos, temp.length);
            pos += temp.length;
        }
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

    public static void sendUseSkill(final byte room, final byte key, final byte skillCode, final Client client) {
        final byte[] bytes = new byte[Globals.PACKET_BYTE * 4];
        bytes[0] = Globals.DATA_PLAYER_USESKILL;
        bytes[1] = room;
        bytes[2] = key;
        bytes[3] = skillCode;
        sendPacket(bytes, client);
    }

    private static void sendPacket(final byte[] packet, final Client client) {
        if ((Boolean) Globals.ServerConfig.UDP_MODE.getValue()) {
            client.sendUDP(packet);
        } else {
            client.sendTCP(packet);
        }
    }
}
