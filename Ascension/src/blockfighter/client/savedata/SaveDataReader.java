package blockfighter.client.savedata;

import blockfighter.client.entities.items.ItemEquip;
import blockfighter.client.entities.items.ItemUpgrade;

public abstract class SaveDataReader {

    public abstract SaveData readSaveData(final SaveData c, final byte[] data);

    protected abstract int readScoreboardKeyBind(final byte[] data, final int[] keybind, final int pos);

    protected abstract int readEmoteKeyBind(final byte[] data, final int[] keybind, final int pos);

    protected abstract int readHotkeys(final byte[] data, final SaveData c, final int pos);

    protected abstract int readItems(final byte[] data, final ItemUpgrade[] e, final int pos);

    protected abstract int readItems(final byte[] data, final ItemEquip[] e, final int pos);

    protected abstract int readKeyBind(final byte[] data, final int[] keybind, final int pos);

    protected abstract int readSkills(final byte[] data, final SaveData c, final int pos);

}
