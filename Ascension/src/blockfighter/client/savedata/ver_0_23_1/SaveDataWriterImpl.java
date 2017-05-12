package blockfighter.client.savedata.ver_0_23_1;

import blockfighter.client.entities.items.ItemEquip;
import blockfighter.client.entities.items.ItemUpgrade;
import blockfighter.client.entities.player.skills.Skill;
import blockfighter.client.savedata.SaveData;
import blockfighter.client.savedata.SaveDataWriter;
import blockfighter.shared.Globals;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.FileUtils;

public class SaveDataWriterImpl extends SaveDataWriter {

    private static final int NUM_EQUIP_SLOTS = 11;
    private static final int NUM_INVENTORY_TABS = 10;
    private static final int NUM_INVENTORY_SLOTS = 100;
    private static final int NUM_UPGRADE_INV_SLOTS = 100;
    private static final int NUM_SKILLS = 30;
    private static final int NUM_HOTKEYS = 12;
    private static final int NUM_KEYBINDS = 16;
    private static final int NUM_EMOTES = 10;
    private static final int NUM_STATS = 6;

    @Override
    public void writeSaveData(final byte saveNum, final SaveData c) {
        final byte[] data = new byte[Globals.MAX_NAME_LENGTH //Name in UTF-8 Character
                + Long.BYTES * 2 //UUID
                + Integer.BYTES * NUM_STATS //Main stats
                + Integer.BYTES * NUM_EQUIP_SLOTS * 11
                + Integer.BYTES * NUM_INVENTORY_TABS * NUM_INVENTORY_SLOTS * 11
                + Integer.BYTES * NUM_UPGRADE_INV_SLOTS * 2
                + Byte.BYTES * NUM_SKILLS
                + Byte.BYTES * NUM_HOTKEYS
                + Integer.BYTES * (NUM_KEYBINDS + NUM_EMOTES)];

        byte[] temp = c.getPlayerName().getBytes(StandardCharsets.UTF_8);

        int pos = 0;
        System.arraycopy(temp, 0, data, pos, temp.length);
        pos += Globals.MAX_NAME_LENGTH;

        temp = Globals.longToBytes(c.getUniqueID().getLeastSignificantBits());
        System.arraycopy(temp, 0, data, pos, temp.length);
        pos += temp.length;

        temp = Globals.longToBytes(c.getUniqueID().getMostSignificantBits());
        System.arraycopy(temp, 0, data, pos, temp.length);
        pos += temp.length;

        final int[] statIDs = {Globals.STAT_LEVEL,
            Globals.STAT_POWER,
            Globals.STAT_DEFENSE,
            Globals.STAT_SPIRIT,
            Globals.STAT_SKILLPOINTS};

        for (final int i : statIDs) {
            temp = Globals.intToBytes((int) c.getBaseStats()[i]);
            System.arraycopy(temp, 0, data, pos, temp.length);
            pos += temp.length;
        }

        pos = saveItems(data, c.getEquip(), pos, NUM_EQUIP_SLOTS);
        for (int i = 0; i < NUM_INVENTORY_TABS; i++) {
            ItemEquip[] e = c.getInventory()[i];
            pos = saveItems(data, e, pos, NUM_INVENTORY_SLOTS);
        }
        pos = saveItems(data, c.getUpgrades(), pos, NUM_UPGRADE_INV_SLOTS);
        pos = saveSkills(data, c, pos, NUM_SKILLS);
        pos = saveHotkeys(data, c, pos, NUM_HOTKEYS);
        pos = saveKeyBind(data, c.getKeyBind(), pos, NUM_KEYBINDS);

        temp = Globals.intToBytes((int) c.getBaseStats()[Globals.STAT_EXP]);
        System.arraycopy(temp, 0, data, pos, temp.length);
        pos += temp.length;

        saveEmoteKeyBind(data, c.getKeyBind(), pos);

        try {
            Globals.log(SaveData.class, "Writing Save Data with " + getClass().getName(), Globals.LOG_TYPE_DATA);
            FileUtils.writeByteArrayToFile(new File(saveNum + ".tcdat"), data);
        } catch (final IOException ex) {
            Globals.logError(ex.toString(), ex);
        }
    }

    @Override
    protected int saveKeyBind(final byte[] data, final int[] keybind, final int pos, final int numKeybinds) {
        int nextPos = pos;
        for (int i = 0; i < numKeybinds; i++) {
            byte[] temp;
            temp = Globals.intToBytes(keybind[i]);
            System.arraycopy(temp, 0, data, nextPos, temp.length);
            nextPos += temp.length;
        }
        return nextPos;
    }

    @Override
    @Deprecated
    protected int saveEmoteKeyBind(final byte[] data, final int[] keybind, final int pos) {
        int nextPos = pos;
        for (int i = 16; i < 16 + 10; i++) {
            byte[] temp;
            temp = Globals.intToBytes(keybind[i]);
            System.arraycopy(temp, 0, data, nextPos, temp.length);
            nextPos += temp.length;
        }
        return nextPos;
    }

    @Override
    @Deprecated
    protected int saveScoreboardKeyBind(final byte[] data, final int[] keybind, final int pos) {
        int nextPos = pos;
        byte[] temp = Globals.intToBytes(keybind[Globals.KEYBIND_SCOREBOARD]);
        System.arraycopy(temp, 0, data, nextPos, temp.length);
        nextPos += temp.length;
        return nextPos;
    }

    @Override
    protected int saveItems(final byte[] data, final ItemUpgrade[] e, final int pos, final int numSlots) {
        int nextPos = pos;
        for (int i = 0; i < numSlots; i++) {
            ItemUpgrade item = e[i];
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
    protected int saveSkills(final byte[] data, final SaveData c, final int pos, final int numSkills) {
        int nextPos = pos;
        for (int i = 0; i < numSkills; i++) {
            data[nextPos] = c.getSkills()[i].getLevel();
            nextPos += 1;
        }
        return nextPos;
    }

    @Override
    protected int saveHotkeys(final byte[] data, final SaveData c, final int pos, final int numHotkeys) {
        int nextPos = pos;
        for (int i = 0; i < numHotkeys; i++) {
            Skill hotkey = c.getHotkeys()[i];
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
    protected int saveItems(final byte[] data, final ItemEquip[] e, final int pos, final int numSlots) {
        int nextPos = pos;
        for (int index = 0; index < numSlots; index++) {
            ItemEquip item = e[index];
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
