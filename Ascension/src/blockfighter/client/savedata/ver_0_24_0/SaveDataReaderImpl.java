package blockfighter.client.savedata.ver_0_24_0;

import blockfighter.client.entities.items.ItemEquip;
import blockfighter.client.savedata.SaveData;
import blockfighter.shared.Globals;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class SaveDataReaderImpl extends blockfighter.client.savedata.ver_0_23_2.SaveDataReaderImpl {

    private static final int NUM_KEYBINDS = 27;

    @Override
    public SaveData readSaveData(final SaveData c, final byte[] data) {
        byte[] temp;

        int pos = 0;
        temp = new byte[Integer.BYTES];
        System.arraycopy(temp, 0, data, pos, temp.length);
        pos += temp.length;

        temp = new byte[Globals.MAX_NAME_LENGTH];
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

        final int[] statIDs = {Globals.STAT_LEVEL,
            Globals.STAT_POWER,
            Globals.STAT_DEFENSE,
            Globals.STAT_SPIRIT,
            Globals.STAT_SKILLPOINTS,
            Globals.STAT_EXP};

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
        readKeyBind(data, c.getKeyBind(), pos);

        c.calcStats();
        return c;
    }

    @Override
    protected int readKeyBind(final byte[] data, final int[] keybind, final int pos) {
        int nextPos = pos;
        for (int i = 0; i < NUM_KEYBINDS; i++) {
            final byte[] temp = new byte[4];
            System.arraycopy(data, nextPos, temp, 0, temp.length);
            keybind[i] = Globals.bytesToInt(temp);
            nextPos += temp.length;
        }
        return nextPos;
    }
}
