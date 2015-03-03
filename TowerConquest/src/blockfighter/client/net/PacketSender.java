package blockfighter.client.net;

import blockfighter.client.Globals;
import blockfighter.client.SaveData;
import blockfighter.client.entities.items.ItemEquip;
import blockfighter.client.entities.player.skills.Skill;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

/**
 *
 * @author Ken Kwan
 */
public class PacketSender {

    InetAddress address;
    DatagramSocket socket = null;

    public PacketSender(DatagramSocket socket) {
        this.socket = socket;
        address = socket.getInetAddress();
    }

    public void sendLogin(byte room, SaveData c) {
        byte[] bytes = new byte[Globals.PACKET_BYTE * 2 //Data type + room
                + 15 //Name length
                + Globals.PACKET_INT //uID
                + Globals.PACKET_INT * 8 //Stats
                + Globals.PACKET_INT * 11 //equipments
                + 12 * 2 * Globals.PACKET_BYTE //Hotkey'd skills + level
                ];
        bytes[0] = Globals.DATA_LOGIN;
        bytes[1] = room;

        byte[] temp = c.getPlayerName().getBytes(StandardCharsets.UTF_8);
        System.arraycopy(temp, 0, bytes, 2, temp.length);

        temp = Globals.intToByte(c.getUniqueID());
        bytes[17] = temp[0];
        bytes[18] = temp[1];
        bytes[19] = temp[2];
        bytes[20] = temp[3];

        double[] stats = c.getTotalStats();
        temp = Globals.intToByte((int) stats[Globals.STAT_LEVEL]);
        bytes[21] = temp[0];
        bytes[22] = temp[1];
        bytes[23] = temp[2];
        bytes[24] = temp[3];
        temp = Globals.intToByte((int) stats[Globals.STAT_POWER]);
        bytes[25] = temp[0];
        bytes[26] = temp[1];
        bytes[27] = temp[2];
        bytes[28] = temp[3];
        temp = Globals.intToByte((int) stats[Globals.STAT_DEFENSE]);
        bytes[29] = temp[0];
        bytes[30] = temp[1];
        bytes[31] = temp[2];
        bytes[32] = temp[3];
        temp = Globals.intToByte((int) stats[Globals.STAT_SPIRIT]);
        bytes[33] = temp[0];
        bytes[34] = temp[1];
        bytes[35] = temp[2];
        bytes[36] = temp[3];

        stats = c.getBonusStats();
        temp = Globals.intToByte((int) stats[Globals.STAT_ARMOR]);
        bytes[37] = temp[0];
        bytes[38] = temp[1];
        bytes[39] = temp[2];
        bytes[40] = temp[3];
        temp = Globals.intToByte((int) (stats[Globals.STAT_REGEN] * 10));
        bytes[41] = temp[0];
        bytes[42] = temp[1];
        bytes[43] = temp[2];
        bytes[44] = temp[3];
        temp = Globals.intToByte((int) (stats[Globals.STAT_CRITDMG] * 10000));
        bytes[45] = temp[0];
        bytes[46] = temp[1];
        bytes[47] = temp[2];
        bytes[48] = temp[3];
        temp = Globals.intToByte((int) (stats[Globals.STAT_CRITCHANCE] * 10000));
        bytes[49] = temp[0];
        bytes[50] = temp[1];
        bytes[51] = temp[2];
        bytes[52] = temp[3];

        ItemEquip[] equip = c.getEquip();
        for (int i = 0; i < equip.length; i++) {
            if (equip[i] == null) {
                continue;
            }
            temp = Globals.intToByte(equip[i].getItemCode());
            bytes[i * 4 + 53] = temp[0];
            bytes[i * 4 + 54] = temp[1];
            bytes[i * 4 + 55] = temp[2];
            bytes[i * 4 + 56] = temp[3];
        }

        Skill[] skills = c.getHotkeys();
        for (int i = 0; i < skills.length; i++) {
            if (skills[i] == null) {
                bytes[i * 2 + 97] = -1;
                bytes[i * 2 + 98] = 0;
                continue;
            }
            bytes[i * 2 + 97] = skills[i].getSkillCode();
            bytes[i * 2 + 98] = skills[i].getLevel();
        }
        DatagramPacket requestPacket = createPacket(bytes);
        sendPacket(requestPacket);
    }

    public void sendSetBossType(byte room, byte bossKey) {
        byte[] bytes = new byte[Globals.PACKET_BYTE * 3];
        bytes[0] = Globals.DATA_BOSS_SET_TYPE;
        bytes[1] = room;
        bytes[2] = bossKey;
        DatagramPacket requestPacket = createPacket(bytes);
        sendPacket(requestPacket);
    }

    public void sendGetBossStat(byte room, byte key, byte stat) {
        byte[] bytes = new byte[Globals.PACKET_BYTE * 4];
        bytes[0] = Globals.DATA_BOSS_GET_STAT;
        bytes[1] = room;
        bytes[2] = key;
        bytes[3] = stat;
        DatagramPacket requestPacket = createPacket(bytes);
        sendPacket(requestPacket);
    }

    public void sendGetAll(byte room, byte myKey) {
        byte[] bytes = new byte[Globals.PACKET_BYTE * 3];
        bytes[0] = Globals.DATA_PLAYER_GET_ALL;
        bytes[1] = room;
        bytes[2] = myKey;
        DatagramPacket requestPacket = createPacket(bytes);
        sendPacket(requestPacket);
    }

    public void sendMove(byte room, byte key, byte direction, boolean move) {
        byte[] bytes = new byte[Globals.PACKET_BYTE * 5];
        bytes[0] = Globals.DATA_PLAYER_SET_MOVE;
        bytes[1] = room;
        bytes[2] = key;
        bytes[3] = direction;
        bytes[4] = (byte) (move ? 1 : 0);
        DatagramPacket requestPacket = createPacket(bytes);
        sendPacket(requestPacket);
    }

    public void sendUseSkill(byte room, byte key, byte skillCode) {
        byte[] bytes = new byte[Globals.PACKET_BYTE * 4];
        bytes[0] = Globals.DATA_PLAYER_USESKILL;
        bytes[1] = room;
        bytes[2] = key;
        bytes[3] = skillCode;
        DatagramPacket requestPacket = createPacket(bytes);
        sendPacket(requestPacket);
    }

    public void sendGetPing(byte room, byte myKey, byte pID) {
        byte[] bytes = new byte[Globals.PACKET_BYTE * 4];
        bytes[0] = Globals.DATA_PING;
        bytes[1] = room;
        bytes[2] = myKey;
        bytes[3] = pID;
        DatagramPacket requestPacket = createPacket(bytes);
        sendPacket(requestPacket);
    }

    public void sendDisconnect(byte room, byte myKey) {
        byte[] bytes = new byte[Globals.PACKET_BYTE * 3];
        bytes[0] = Globals.DATA_PLAYER_DISCONNECT;
        bytes[1] = room;
        bytes[2] = myKey;
        DatagramPacket requestPacket = createPacket(bytes);
        sendPacket(requestPacket);
    }

    public void sendGetName(byte room, byte key) {
        byte[] bytes = new byte[Globals.PACKET_BYTE * 3];
        bytes[0] = Globals.DATA_PLAYER_GET_NAME;
        bytes[1] = room;
        bytes[2] = key;
        DatagramPacket requestPacket = createPacket(bytes);
        sendPacket(requestPacket);
    }

    public void sendGetStat(byte room, byte key, byte stat) {
        byte[] bytes = new byte[Globals.PACKET_BYTE * 4];
        bytes[0] = Globals.DATA_PLAYER_GET_STAT;
        bytes[1] = room;
        bytes[2] = key;
        bytes[3] = stat;
        DatagramPacket requestPacket = createPacket(bytes);
        sendPacket(requestPacket);
    }

    public void sendGetEquip(byte room, byte key) {
        byte[] bytes = new byte[Globals.PACKET_BYTE * 3];
        bytes[0] = Globals.DATA_PLAYER_GET_EQUIP;
        bytes[1] = room;
        bytes[2] = key;
        DatagramPacket requestPacket = createPacket(bytes);
        sendPacket(requestPacket);
    }

    private void sendPacket(DatagramPacket packet) {
        try {
            socket.send(packet);
        } catch (Exception ex) {
            System.err.println("sendPacket:" + ex.getMessage());
        }
    }

    private DatagramPacket createPacket(byte[] bytes) {
        return new DatagramPacket(bytes, bytes.length);
    }
}
