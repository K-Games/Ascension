package blockfighter.client.savedata.ver_0_23_1;

import blockfighter.client.entities.items.ItemEquip;
import blockfighter.client.entities.items.ItemUpgrade;
import blockfighter.client.entities.player.skills.Skill;
import blockfighter.client.savedata.SaveData;
import blockfighter.client.savedata.SaveDataWriter;
import blockfighter.shared.Globals;

public class SaveDataWriterImpl extends SaveDataWriter {

    @Override
    public void writeSaveData(final byte saveNum, final SaveData c) {

    }

    @Override
    protected int saveKeyBind(final byte[] data, final int[] keybind, final int pos) {
        int nextPos = pos;
        for (int i = 0; i < 16; i++) {
            byte[] temp;
            temp = Globals.intToBytes(keybind[i]);
            System.arraycopy(temp, 0, data, nextPos, temp.length);
            nextPos += temp.length;
        }
        return nextPos;
    }

    @Override
    protected int saveEmoteKeyBind(final byte[] data, final int[] keybind, final int pos) {
        int nextPos = pos;
        for (int i = 16; i < 16 + Globals.Emotes.values().length; i++) {
            byte[] temp;
            temp = Globals.intToBytes(keybind[i]);
            System.arraycopy(temp, 0, data, nextPos, temp.length);
            nextPos += temp.length;
        }
        return nextPos;
    }

    @Override
    protected int saveScoreboardKeyBind(final byte[] data, final int[] keybind, final int pos) {
        int nextPos = pos;
        byte[] temp = Globals.intToBytes(keybind[Globals.KEYBIND_SCOREBOARD]);
        System.arraycopy(temp, 0, data, nextPos, temp.length);
        nextPos += temp.length;
        return nextPos;
    }

    @Override
    protected int saveItems(final byte[] data, final ItemUpgrade[] e, final int pos) {
        int nextPos = pos;
        for (final ItemUpgrade item : e) {
            if (item == null) {
                nextPos += 2 * 4;
                continue;
            }
            byte[] temp;

            temp = Globals.intToBytes(item.getItemCode());
            System.arraycopy(temp, 0, data, nextPos, temp.length);
            nextPos += temp.length;

            temp = Globals.intToBytes(item.getLevel());
            System.arraycopy(temp, 0, data, nextPos, temp.length);
            nextPos += temp.length;
        }
        return nextPos;
    }

    @Override
    protected int saveSkills(final byte[] data, final SaveData c, final int pos) {
        int nextPos = pos;
        for (int i = 0; i < Globals.NUM_SKILLS; i++) {
            data[nextPos] = c.getSkills()[i].getLevel();
            nextPos += 1;
        }
        return nextPos;
    }

    @Override
    protected int saveHotkeys(final byte[] data, final SaveData c, final int pos) {
        int nextPos = pos;
        for (final Skill hotkey : c.getHotkeys()) {
            if (hotkey == null) {
                data[nextPos] = -1;
            } else {
                data[nextPos] = hotkey.getSkillCode();
            }
            nextPos += 1;
        }
        return nextPos;
    }

    @Override
    protected int saveItems(final byte[] data, final ItemEquip[] e, final int pos) {
        int nextPos = pos;
        for (final ItemEquip item : e) {
            if (item == null) {
                nextPos += 11 * 4;
                continue;
            }
            byte[] temp;
            temp = Globals.intToBytes(item.getItemCode());
            System.arraycopy(temp, 0, data, nextPos, temp.length);
            nextPos += temp.length;

            final int[] statIDs = {Globals.STAT_LEVEL,
                Globals.STAT_POWER,
                Globals.STAT_DEFENSE,
                Globals.STAT_SPIRIT,
                Globals.STAT_ARMOR,
                Globals.STAT_REGEN,
                Globals.STAT_CRITDMG,
                Globals.STAT_CRITCHANCE};

            for (final int i : statIDs) {
                switch (i) {
                    case Globals.STAT_REGEN:
                        temp = Globals.intToBytes((int) (item.getBaseStats()[i] * 10));
                        break;
                    case Globals.STAT_CRITDMG:
                        temp = Globals.intToBytes((int) (item.getBaseStats()[i] * 10000));
                        break;
                    case Globals.STAT_CRITCHANCE:
                        temp = Globals.intToBytes((int) (item.getBaseStats()[i] * 10000));
                        break;
                    default:
                        temp = Globals.intToBytes((int) item.getBaseStats()[i]);
                }
                System.arraycopy(temp, 0, data, nextPos, temp.length);
                nextPos += temp.length;
            }

            temp = Globals.intToBytes(item.getUpgrades());
            System.arraycopy(temp, 0, data, nextPos, temp.length);
            nextPos += temp.length;

            temp = Globals.intToBytes((int) (item.getBonusMult() * 100));
            System.arraycopy(temp, 0, data, nextPos, temp.length);
            nextPos += temp.length;
        }
        return nextPos;
    }

}
