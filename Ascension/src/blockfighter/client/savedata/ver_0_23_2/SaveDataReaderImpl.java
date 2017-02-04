package blockfighter.client.savedata.ver_0_23_2;

import blockfighter.client.entities.items.ItemEquip;
import blockfighter.client.savedata.SaveData;
import blockfighter.client.savedata.SaveDataReader;
import blockfighter.shared.Globals;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class SaveDataReaderImpl extends SaveDataReader {

    @Override
    public SaveData readData(final SaveData c, final byte[] data) {
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
        pos = readKeyBind(data, c.getKeyBind(), pos);
        pos = readEmoteKeyBind(data, c.getKeyBind(), pos);
        readScoreboardKeyBind(data, c.getKeyBind(), pos);

        c.calcStats();
        return c;
    }
}
