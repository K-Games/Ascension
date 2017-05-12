package blockfighter.client.savedata;

import blockfighter.client.entities.items.ItemEquip;
import blockfighter.client.entities.items.ItemUpgrade;

public abstract class SaveDataWriter {

    public abstract void writeSaveData(final byte saveNum, final SaveData c);

    protected abstract int saveKeyBind(final byte[] data, final int[] keybind, final int pos, final int numKeybinds);

    protected abstract int saveEmoteKeyBind(final byte[] data, final int[] keybind, final int pos);

    protected abstract int saveScoreboardKeyBind(final byte[] data, final int[] keybind, final int pos);

    protected abstract int saveItems(final byte[] data, final ItemUpgrade[] e, final int pos, final int numSlots);

    protected abstract int saveSkills(final byte[] data, final SaveData c, final int pos, final int numSkills);

    protected abstract int saveHotkeys(final byte[] data, final SaveData c, final int pos, final int numHotkeys);

    protected abstract int saveItems(final byte[] data, final ItemEquip[] e, final int pos, final int numSlots);

}
