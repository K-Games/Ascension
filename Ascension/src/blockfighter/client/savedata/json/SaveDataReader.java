package blockfighter.client.savedata.json;

import blockfighter.client.savedata.SaveData;
import blockfighter.shared.Globals;
import java.util.Base64;

public final class SaveDataReader {

    public static SaveData readSaveData(final byte[] rawData) {
        byte[] decodedBytes = Base64.getDecoder().decode(rawData);
        String reversedJson = new String(decodedBytes);
        String json = new StringBuffer(reversedJson).reverse().toString();
        return Globals.GSON.fromJson(json, SaveData.class);
    }

}
