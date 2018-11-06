package blockfighter.client.savedata.json;

import blockfighter.client.savedata.SaveData;
import blockfighter.shared.Globals;
import java.util.Base64;
import org.apache.commons.lang3.ArrayUtils;

public final class SaveDataReader {

    public static SaveData readSaveData(final byte[] rawData) {
        byte[] firstPassDecode = Base64.getDecoder().decode(rawData);
        ArrayUtils.reverse(firstPassDecode);
        byte[] decodedBytes = Base64.getDecoder().decode(firstPassDecode);
        String reversedJson = new String(decodedBytes);
        String json = new StringBuffer(reversedJson).reverse().toString();
        return Globals.GSON.fromJson(json, SaveData.class);
    }
}
