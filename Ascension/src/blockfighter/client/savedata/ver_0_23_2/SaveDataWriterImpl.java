package blockfighter.client.savedata.ver_0_23_2;

import blockfighter.client.entities.items.ItemEquip;
import blockfighter.client.savedata.SaveData;
import blockfighter.shared.Globals;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.FileUtils;

public class SaveDataWriterImpl extends blockfighter.client.savedata.ver_0_23_1.SaveDataWriterImpl {

    @Override
    public void writeSaveData(final byte saveNum, final SaveData c) {
        final byte[] data = new byte[Integer.BYTES //Save Version Number
                + Globals.MAX_NAME_LENGTH //Name in UTF-8 Character
                + Long.BYTES * 2 //UUID
                + Integer.BYTES * 6 //Main stats
                + Integer.BYTES * c.getEquip().length * 11
                + Integer.BYTES * c.getInventory().length * c.getInventory()[0].length * 11
                + Integer.BYTES * c.getUpgrades().length * 2
                + Byte.BYTES * c.getSkills().length
                + Byte.BYTES * c.getHotkeys().length
                + Integer.BYTES * 16 //Base Keybinds
                + Integer.BYTES * Globals.Emotes.values().length
                + Integer.BYTES // Scoreboard keybind
                ];

        byte[] temp;

        int pos = 0;
        temp = Globals.intToBytes(SaveData.SAVE_VERSION_0232);
        System.arraycopy(temp, 0, data, pos, temp.length);
        pos += temp.length;

        temp = c.getPlayerName().getBytes(StandardCharsets.UTF_8);
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
            Globals.STAT_SKILLPOINTS,
            Globals.STAT_EXP};

        for (final int i : statIDs) {
            temp = Globals.intToBytes((int) c.getBaseStats()[i]);
            System.arraycopy(temp, 0, data, pos, temp.length);
            pos += temp.length;
        }

        pos = saveItems(data, c.getEquip(), pos);
        for (final ItemEquip[] e : c.getInventory()) {
            pos = saveItems(data, e, pos);
        }
        pos = saveItems(data, c.getUpgrades(), pos);
        pos = saveSkills(data, c, pos);
        pos = saveHotkeys(data, c, pos);
        pos = saveKeyBind(data, c.getKeyBind(), pos);
        pos = saveEmoteKeyBind(data, c.getKeyBind(), pos);
        saveScoreboardKeyBind(data, c.getKeyBind(), pos);

        try {
            Globals.log(SaveData.class, "Writing Save Data with " + getClass().getName(), Globals.LOG_TYPE_DATA);
            FileUtils.writeByteArrayToFile(new File(saveNum + ".tcdat"), data);
        } catch (final IOException ex) {
            Globals.logError(ex.toString(), ex);
        }
    }
}
