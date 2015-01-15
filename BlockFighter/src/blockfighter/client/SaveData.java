package blockfighter.client;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author Ken
 */
public class SaveData {

    private int[] stats = new int[3];
    private int uniqueID;
    private String name;

    public SaveData() {
        name = "";
        uniqueID = new Random().nextInt(Integer.MAX_VALUE);
        stats[Globals.STAT_POWER] = 0;
        stats[Globals.STAT_DEFENSE] = 0;
        stats[Globals.STAT_SPIRIT] = 0;
    }

    public static void saveData(byte saveNum, SaveData character) {
        byte[] data = new byte[15 + 4 * 3 + 4];

        byte[] temp = character.name.getBytes(StandardCharsets.UTF_8);
        System.arraycopy(temp, 0, data, 0, temp.length);

        temp = Globals.intToByte(character.uniqueID);
        System.arraycopy(temp, 0, data, 15, 4);

        temp = Globals.intToByte(character.stats[Globals.STAT_POWER]);
        System.arraycopy(temp, 0, data, 19, 4);

        temp = Globals.intToByte(character.stats[Globals.STAT_DEFENSE]);
        System.arraycopy(temp, 0, data, 23, 4);

        temp = Globals.intToByte(character.stats[Globals.STAT_SPIRIT]);
        System.arraycopy(temp, 0, data, 27, 4);

        try {
            FileUtils.writeByteArrayToFile(new File(saveNum + ".tcdat"), data);
        } catch (IOException ex) {
            Logger.getLogger(SaveData.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static SaveData readData(byte saveNum) {

        SaveData character = new SaveData();

        byte[] data, temp = new byte[15];

        try {
            data = FileUtils.readFileToByteArray(new File(saveNum + ".tcdat"));
        } catch (IOException ex) {
            return null;
        }

        System.arraycopy(data, 0, temp, 0, 15);
        character.name = new String(temp, StandardCharsets.UTF_8).trim();

        temp = new byte[4];
        System.arraycopy(data, 15, temp, 0, 4);
        character.uniqueID = Globals.bytesToInt(temp);

        System.arraycopy(data, 19, temp, 0, 4);
        character.stats[Globals.STAT_POWER] = Globals.bytesToInt(temp);

        System.arraycopy(data, 23, temp, 0, 4);
        character.stats[Globals.STAT_DEFENSE] = Globals.bytesToInt(temp);

        System.arraycopy(data, 27, temp, 0, 4);
        character.stats[Globals.STAT_SPIRIT] = Globals.bytesToInt(temp);

        return character;
    }
}
