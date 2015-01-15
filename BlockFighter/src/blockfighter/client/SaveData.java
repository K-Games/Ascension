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

    private double[] stats = new double[Globals.NUM_STATS];
    private int uniqueID;
    private String name;

    public SaveData(String n) {
        name = n;
        Random rng = new Random();
        uniqueID = rng.nextInt(Integer.MAX_VALUE);
        stats[Globals.STAT_LEVEL] = rng.nextInt(100);
        stats[Globals.STAT_POWER] = rng.nextInt(700);
        stats[Globals.STAT_DEFENSE] = rng.nextInt(700);
        stats[Globals.STAT_SPIRIT] = rng.nextInt(700);
    }

    public static void saveData(byte saveNum, SaveData character) {
        byte[] data = new byte[Globals.MAX_NAME_LENGTH + Globals.PACKET_INT * 5];
        byte[] temp = character.name.getBytes(StandardCharsets.UTF_8);

        int pos = 0;
        System.arraycopy(temp, 0, data, pos, temp.length);
        pos += Globals.MAX_NAME_LENGTH;

        temp = Globals.intToByte(character.uniqueID);
        System.arraycopy(temp, 0, data, pos, 4);
        pos += temp.length;

        temp = Globals.intToByte((int) character.stats[Globals.STAT_LEVEL]);
        System.arraycopy(temp, 0, data, pos, 4);
        pos += temp.length;

        temp = Globals.intToByte((int) character.stats[Globals.STAT_POWER]);
        System.arraycopy(temp, 0, data, pos, 4);
        pos += temp.length;

        temp = Globals.intToByte((int) character.stats[Globals.STAT_DEFENSE]);
        System.arraycopy(temp, 0, data, pos, 4);
        pos += temp.length;

        temp = Globals.intToByte((int) character.stats[Globals.STAT_SPIRIT]);
        System.arraycopy(temp, 0, data, pos, 4);
        pos += temp.length;

        try {
            FileUtils.writeByteArrayToFile(new File(saveNum + ".tcdat"), data);
        } catch (IOException ex) {
            Logger.getLogger(SaveData.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static SaveData readData(byte saveNum) {
        SaveData character = new SaveData("");
        byte[] data, temp = new byte[Globals.MAX_NAME_LENGTH];

        try {
            data = FileUtils.readFileToByteArray(new File(saveNum + ".tcdat"));
        } catch (IOException ex) {
            return null;
        }

        int pos = 0;
        System.arraycopy(data, pos, temp, 0, temp.length);
        character.name = new String(temp, StandardCharsets.UTF_8).trim();
        pos += Globals.MAX_NAME_LENGTH;

        temp = new byte[4];
        System.arraycopy(data, pos, temp, 0, temp.length);
        character.uniqueID = Globals.bytesToInt(temp);
        pos += temp.length;

        System.arraycopy(data, pos, temp, 0, temp.length);
        character.stats[Globals.STAT_LEVEL] = Globals.bytesToInt(temp);
        pos += temp.length;

        System.arraycopy(data, pos, temp, 0, temp.length);
        character.stats[Globals.STAT_POWER] = Globals.bytesToInt(temp);
        pos += temp.length;

        System.arraycopy(data, pos, temp, 0, temp.length);
        character.stats[Globals.STAT_DEFENSE] = Globals.bytesToInt(temp);
        pos += temp.length;

        System.arraycopy(data, pos, temp, 0, temp.length);
        character.stats[Globals.STAT_SPIRIT] = Globals.bytesToInt(temp);
        pos += temp.length;

        return character;
    }

    public String getPlayerName() {
        return name;
    }

    public double[] getStats() {
        return stats;
    }

    public int getUniqueID() {
        return uniqueID;
    }

}
