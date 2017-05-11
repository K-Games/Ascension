package blockfighter.client.savedata.ver_0_23_1;

import blockfighter.client.entities.items.ItemEquip;
import blockfighter.client.entities.items.ItemUpgrade;
import blockfighter.client.entities.player.skills.Skill;
import blockfighter.client.savedata.SaveData;
import blockfighter.client.savedata.SaveDataReader;
import blockfighter.shared.Globals;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class SaveDataReaderImpl extends SaveDataReader {

    @Override
    public SaveData readSaveData(final SaveData c, final byte[] data) {
        byte[] temp = new byte[Globals.MAX_NAME_LENGTH];
        int pos = 0;
        System.arraycopy(data, pos, temp, 0, temp.length);
        c.setPlayerName(new String(temp, StandardCharsets.UTF_8).trim());
        pos += Globals.MAX_NAME_LENGTH;
        long leastSigBits;
        long mostSigBits;
        temp = new byte[8];
        System.arraycopy(data, pos, temp, 0, temp.length);
        leastSigBits = Globals.bytesToLong(temp);
        pos += temp.length;
        System.arraycopy(data, pos, temp, 0, temp.length);
        mostSigBits = Globals.bytesToLong(temp);
        pos += temp.length;
        c.setUniqueID(new UUID(mostSigBits, leastSigBits));
        final int[] statIDs = {Globals.STAT_LEVEL, Globals.STAT_POWER, Globals.STAT_DEFENSE, Globals.STAT_SPIRIT, Globals.STAT_SKILLPOINTS};
        temp = new byte[4];
        for (final int i : statIDs) {
            System.arraycopy(data, pos, temp, 0, temp.length);
            c.getBaseStats()[i] = Globals.bytesToInt(temp);
            pos += temp.length;
        }
        pos = readItems(data, c.getEquip(), pos);
        for (final ItemEquip[] e : c.getInventory()) {
            pos = readItems(data, e, pos);
        }
        pos = readItems(data, c.getUpgrades(), pos);
        pos = readSkills(data, c, pos);
        pos = readHotkeys(data, c, pos);
        pos = readKeyBind(data, c.getKeyBind(), pos);
        System.arraycopy(data, pos, temp, 0, temp.length);
        c.getBaseStats()[Globals.STAT_EXP] = Globals.bytesToInt(temp);
        pos += temp.length;
        readEmoteKeyBind(data, c.getKeyBind(), pos);

        c.calcStats();
        return c;
    }

    @Override
    protected int readScoreboardKeyBind(final byte[] data, final int[] keybind, final int pos) {
        int nextPos = pos;
        final byte[] temp = new byte[4];
        try {
            System.arraycopy(data, nextPos, temp, 0, temp.length);
            if (Globals.bytesToInt(temp) == 0) {
                keybind[Globals.KEYBIND_SCOREBOARD] = -1;
            } else {
                keybind[Globals.KEYBIND_SCOREBOARD] = Globals.bytesToInt(temp);
            }
        } catch (Exception e) {
            keybind[Globals.KEYBIND_SCOREBOARD] = -1;
        }
        nextPos += temp.length;
        return nextPos;
    }

    @Override
    protected int readEmoteKeyBind(final byte[] data, final int[] keybind, final int pos) {
        int nextPos = pos;
        for (int i = 16; i < 16 + Globals.Emotes.values().length; i++) {
            final byte[] temp = new byte[4];
            try {
                System.arraycopy(data, nextPos, temp, 0, temp.length);
                if (Globals.bytesToInt(temp) == 0) {
                    keybind[i] = -1;
                } else {
                    keybind[i] = Globals.bytesToInt(temp);
                }
            } catch (Exception e) {
                keybind[i] = -1;
            }
            nextPos += temp.length;
        }
        return nextPos;
    }

    @Override
    protected int readHotkeys(final byte[] data, final SaveData c, final int pos) {
        int nextPos = pos;
        final Skill[] e = c.getHotkeys();
        for (int i = 0; i < e.length; i++) {
            final byte skillCode = data[nextPos];
            if (skillCode != -1) {
                e[i] = c.getSkills()[skillCode];
            }
            nextPos += 1;
        }
        return nextPos;
    }

    @Override
    protected int readItems(final byte[] data, final ItemUpgrade[] e, final int pos) {
        int nextPos = pos;
        for (int i = 0; i < e.length; i++) {
            final byte[] temp = new byte[4];
            int itemCode;
            int level;
            System.arraycopy(data, nextPos, temp, 0, temp.length);
            itemCode = Globals.bytesToInt(temp);
            nextPos += temp.length;
            System.arraycopy(data, nextPos, temp, 0, temp.length);
            level = Globals.bytesToInt(temp);
            nextPos += temp.length;
            if (!ItemUpgrade.isValidItem(itemCode)) {
                e[i] = null;
            } else {
                e[i] = new ItemUpgrade(itemCode, level);
            }
        }
        return nextPos;
    }

    @Override
    protected int readItems(final byte[] data, final ItemEquip[] e, final int pos) {
        int nextPos = pos;
        for (int i = 0; i < e.length; i++) {
            final double[] bs = new double[Globals.NUM_STATS];
            final byte[] temp = new byte[4];
            int itemCode;
            int upgrades;
            double bMult;
            System.arraycopy(data, nextPos, temp, 0, temp.length);
            itemCode = Globals.bytesToInt(temp);
            nextPos += temp.length;
            final int[] statIDs = {Globals.STAT_LEVEL, Globals.STAT_POWER, Globals.STAT_DEFENSE, Globals.STAT_SPIRIT, Globals.STAT_ARMOR, Globals.STAT_REGEN, Globals.STAT_CRITDMG, Globals.STAT_CRITCHANCE};
            for (final int s : statIDs) {
                System.arraycopy(data, nextPos, temp, 0, temp.length);
                switch (s) {
                    case Globals.STAT_REGEN:
                        bs[s] = Globals.bytesToInt(temp) / 10.0;
                        break;
                    case Globals.STAT_CRITDMG:
                        bs[s] = Globals.bytesToInt(temp) / 10000.0;
                        break;
                    case Globals.STAT_CRITCHANCE:
                        bs[s] = Globals.bytesToInt(temp) / 10000.0;
                        break;
                    default:
                        bs[s] = Globals.bytesToInt(temp);
                }
                nextPos += temp.length;
            }
            System.arraycopy(data, nextPos, temp, 0, temp.length);
            upgrades = Globals.bytesToInt(temp);
            nextPos += temp.length;
            System.arraycopy(data, nextPos, temp, 0, temp.length);
            bMult = Globals.bytesToInt(temp) / 100.0;
            nextPos += temp.length;
            if (!ItemEquip.isValidItem(itemCode)) {
                e[i] = null;
            } else {
                e[i] = new ItemEquip(bs, upgrades, bMult, itemCode);
            }
        }
        return nextPos;
    }

    @Override
    protected int readKeyBind(final byte[] data, final int[] keybind, final int pos) {
        int nextPos = pos;
        for (int i = 0; i < 16; i++) {
            final byte[] temp = new byte[4];
            System.arraycopy(data, nextPos, temp, 0, temp.length);
            keybind[i] = Globals.bytesToInt(temp);
            nextPos += temp.length;
        }
        return nextPos;
    }

    @Override
    protected int readSkills(final byte[] data, final SaveData c, final int pos) {
        int nextPos = pos;
        for (int i = 0; i < Globals.NUM_SKILLS; i++) {
            c.getSkills()[i].setLevel(data[nextPos]);
            nextPos += 1;
        }
        return nextPos;
    }
}
