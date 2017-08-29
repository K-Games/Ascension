package blockfighter.client.savedata.ver_0_25_0;

import blockfighter.client.entities.items.ItemEquip;
import blockfighter.client.savedata.SaveData;
import blockfighter.shared.Globals;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SaveDataWriterImpl extends blockfighter.client.savedata.ver_0_24_0.SaveDataWriterImpl {

    private static final int NUM_EQUIP_SLOTS = 11;
    private static final int NUM_INVENTORY_TABS = 10;
    private static final int NUM_INVENTORY_SLOTS = 100;
    private static final int NUM_UPGRADE_INV_SLOTS = 100;
    private static final int NUM_SKILLS = 30;
    private static final int NUM_HOTKEYS = 12;
    private static final int NUM_KEYBINDS = 27;
    private static final int NUM_STATS = 6;

    @Override
    public byte[] writeSaveData(final SaveData c) {
        final byte[] data = new byte[Integer.BYTES //Save Version Number
                + Globals.MAX_NAME_LENGTH //Name in UTF-8 Character
                + Long.BYTES * 2 //UUID
                + Integer.BYTES * NUM_STATS //Main stats
                + Integer.BYTES * NUM_EQUIP_SLOTS * 11
                + Integer.BYTES * NUM_INVENTORY_TABS * NUM_INVENTORY_SLOTS * 11
                + Integer.BYTES * NUM_UPGRADE_INV_SLOTS * 2
                + Byte.BYTES * NUM_SKILLS
                + Byte.BYTES * NUM_HOTKEYS
                + Integer.BYTES * NUM_KEYBINDS
                + Byte.BYTES * 16]; // MD5 Hash

        byte[] temp;

        int pos = 0;
        temp = Globals.intToBytes(SaveData.SAVE_VERSION_0250);
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

        pos = saveItems(data, c.getEquip(), pos, NUM_EQUIP_SLOTS);
        for (final ItemEquip[] e : c.getInventory()) {
            pos = saveItems(data, e, pos, NUM_INVENTORY_SLOTS);
        }
        pos = saveItems(data, c.getUpgrades(), pos, NUM_UPGRADE_INV_SLOTS);
        pos = saveSkills(data, c, pos, NUM_SKILLS);
        pos = saveHotkeys(data, c, pos, NUM_HOTKEYS);
        pos = saveKeyBind(data, c.getKeyBind(), pos, NUM_KEYBINDS);
        saveHash(data, pos);

        return data;
    }

    @Override
    protected int saveHash(final byte[] data, final int pos) {
        int nextPos = pos;
        byte[] digest = new byte[16];
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            digest = md.digest(data);
        } catch (NoSuchAlgorithmException ex) {
            Globals.logError(ex.toString(), ex);
        }
        System.arraycopy(digest, 0, data, pos, digest.length);
        nextPos += digest.length;
        return nextPos;
    }
}
