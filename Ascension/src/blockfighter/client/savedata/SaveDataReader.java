package blockfighter.client.savedata;

import blockfighter.client.entities.items.ItemEquip;
import blockfighter.client.entities.items.ItemUpgrade;
import blockfighter.client.entities.player.skills.Skill;
import blockfighter.shared.Globals;

public abstract class SaveDataReader {

    public abstract SaveData readData(final SaveData c, final byte[] data);

    protected static int readEmoteKeyBind(final byte[] data, final int[] keybind, final int pos) {
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

    protected static int readHotkeys(final byte[] data, final SaveData c, final int pos) {
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

    protected static int readItems(final byte[] data, final ItemUpgrade[] e, final int pos) {
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

    protected static int readItems(final byte[] data, final ItemEquip[] e, final int pos) {
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

    protected static int readKeyBind(final byte[] data, final int[] keybind, final int pos) {
        int nextPos = pos;
        for (int i = 0; i < 16; i++) {
            final byte[] temp = new byte[4];
            System.arraycopy(data, nextPos, temp, 0, temp.length);
            keybind[i] = Globals.bytesToInt(temp);
            nextPos += temp.length;
        }
        return nextPos;
    }

    protected static int readSkills(final byte[] data, final SaveData c, final int pos) {
        int nextPos = pos;
        for (int i = 0; i < Globals.NUM_SKILLS; i++) {
            c.getSkills()[i].setLevel(data[nextPos]);
            nextPos += 1;
        }
        return nextPos;
    }

}
