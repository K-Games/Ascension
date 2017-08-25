package blockfighter.client.net;

import blockfighter.client.entities.items.ItemEquip;
import blockfighter.client.entities.player.skills.Skill;
import blockfighter.client.savedata.SaveData;
import blockfighter.shared.Globals;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class PacketSender {

    public static void sendPlayerLogin(final byte room, final SaveData c) {
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

        sendPacket(bytes);
    }

    public static void sendPlayerCreate(final byte room, final SaveData c) {
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

        double[] stats = c.getBaseStats();
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

        stats = c.getBonusStats();
        temp = Globals.intToBytes((int) stats[Globals.STAT_POWER]);
        System.arraycopy(temp, 0, bytes, pos, temp.length);
        pos += temp.length;

        temp = Globals.intToBytes((int) stats[Globals.STAT_DEFENSE]);
        System.arraycopy(temp, 0, bytes, pos, temp.length);
        pos += temp.length;

        temp = Globals.intToBytes((int) stats[Globals.STAT_SPIRIT]);
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

        final ItemEquip[] equip = c.getEquip();
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

        final HashMap<Byte, Skill> skills = c.getHotkeys();
        for (Skill skill : skills.values()) {
            temp = new byte[2];
            if (skill == null) {
                temp[0] = -1;
                temp[1] = 0;
                System.arraycopy(temp, 0, bytes, pos, temp.length);
                pos += temp.length;
                continue;
            }
            temp[0] = skill.getSkillCode();
            temp[1] = skill.getLevel();
            System.arraycopy(temp, 0, bytes, pos, temp.length);
            pos += temp.length;
        }
        sendPacket(bytes);
    }

    public static void sendSetMobType(final byte room, final int key) {
        final byte[] bytes = new byte[Globals.PACKET_BYTE * 2 + Globals.PACKET_INT];
        bytes[0] = Globals.DATA_MOB_SET_TYPE;
        bytes[1] = room;
        final byte[] intKey = Globals.intToBytes(key);
        System.arraycopy(intKey, 0, bytes, 2, intKey.length);
        sendPacket(bytes);
    }

    public static void sendGetMobStat(final byte room, final int key, final byte stat) {
        final byte[] bytes = new byte[Globals.PACKET_BYTE * 3 + Globals.PACKET_INT];
        bytes[0] = Globals.DATA_MOB_GET_STAT;
        bytes[1] = room;
        final byte[] intKey = Globals.intToBytes(key);
        System.arraycopy(intKey, 0, bytes, 2, intKey.length);
        bytes[6] = stat;
        sendPacket(bytes);
    }

    public static void sendGetAll(final byte room, final byte myKey) {
        final byte[] bytes = new byte[Globals.PACKET_BYTE * 3];
        bytes[0] = Globals.DATA_PLAYER_GET_ALL;
        bytes[1] = room;
        bytes[2] = myKey;
        sendPacket(bytes);
    }

    public static void sendMove(final byte room, final byte key, final byte direction, final boolean move) {
        final byte[] bytes = new byte[Globals.PACKET_BYTE * 5];
        bytes[0] = Globals.DATA_PLAYER_SET_MOVE;
        bytes[1] = room;
        bytes[2] = key;
        bytes[3] = direction;
        bytes[4] = (byte) (move ? 1 : 0);
        sendPacket(bytes);
    }

    public static void sendUseSkill(final byte room, final byte key, final byte skillCode) {
        final byte[] bytes = new byte[Globals.PACKET_BYTE * 4];
        bytes[0] = Globals.DATA_PLAYER_USESKILL;
        bytes[1] = room;
        bytes[2] = key;
        bytes[3] = skillCode;
        sendPacket(bytes);
    }

    public static void sendUseEmote(final byte room, final byte key, final byte emoteID) {
        final byte[] bytes = new byte[Globals.PACKET_BYTE * 4];
        bytes[0] = Globals.DATA_PLAYER_EMOTE;
        bytes[1] = room;
        bytes[2] = key;
        bytes[3] = emoteID;
        sendPacket(bytes);
    }

    public static void sendGetPing() {
        GameClient.getClient().updateReturnTripTime();
    }

    public static void sendDisconnect(final byte room, final byte myKey) {
        final byte[] bytes = new byte[Globals.PACKET_BYTE * 3];
        bytes[0] = Globals.DATA_PLAYER_DISCONNECT;
        bytes[1] = room;
        bytes[2] = myKey;
        sendPacket(bytes);
    }

    public static void sendGetName(final byte room, final byte key) {
        final byte[] bytes = new byte[Globals.PACKET_BYTE * 3];
        bytes[0] = Globals.DATA_PLAYER_GET_NAME;
        bytes[1] = room;
        bytes[2] = key;
        sendPacket(bytes);
    }

    public static void sendGetStat(final byte room, final byte key, final byte stat) {
        final byte[] bytes = new byte[Globals.PACKET_BYTE * 4];
        bytes[0] = Globals.DATA_PLAYER_GET_STAT;
        bytes[1] = room;
        bytes[2] = key;
        bytes[3] = stat;
        sendPacket(bytes);
    }

    public static void sendGetEquip(final byte room, final byte key) {
        final byte[] bytes = new byte[Globals.PACKET_BYTE * 3];
        bytes[0] = Globals.DATA_PLAYER_GET_EQUIP;
        bytes[1] = room;
        bytes[2] = key;
        sendPacket(bytes);
    }

    private static void sendPacket(final byte[] packet) {
        if (!(Boolean) Globals.ServerConfig.UDP_MODE.getValue()) {
            GameClient.getClient().sendTCP(packet);
        } else {
            GameClient.getClient().sendUDP(packet);
        }
    }

}
