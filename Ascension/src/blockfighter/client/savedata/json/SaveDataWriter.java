package blockfighter.client.savedata.json;

import blockfighter.client.savedata.SaveData;
import blockfighter.shared.Globals;
import java.util.Base64;
import org.apache.commons.lang3.ArrayUtils;

public final class SaveDataWriter {

    public static byte[] writeSaveData(final SaveData c) {
        String json = Globals.GSON.toJson(c);
        String reversedJson = new StringBuffer(json).reverse().toString();
        byte[] firstPassEncode = Base64.getEncoder().encode(reversedJson.getBytes());
        ArrayUtils.reverse(firstPassEncode);
        return Base64.getEncoder().encode(firstPassEncode);
    }
}
