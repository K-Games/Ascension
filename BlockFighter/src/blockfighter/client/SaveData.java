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
    private byte saveFile;

    public void createSave(byte saveNum, String name) {
        this.name = name.trim();
        saveFile = saveNum;
        uniqueID = new Random().nextInt(Integer.MAX_VALUE);
        stats[Globals.STAT_POWER] = 0;
        stats[Globals.STAT_DEFENSE] = 0;
        stats[Globals.STAT_SPIRIT] = 0;
    }

    public void saveData() {
        byte[] data = new byte[15 + 4 * 3 + 4];
        
        byte[] temp = name.getBytes(StandardCharsets.UTF_8);
        System.arraycopy(temp, 0, data, 0, temp.length);
        
        temp = Globals.intToByte(uniqueID);
        System.arraycopy(temp, 0, data, 15, 4);
        
        temp = Globals.intToByte(stats[Globals.STAT_POWER]);
        System.arraycopy(temp, 0, data, 19, 4);
        
        temp = Globals.intToByte(stats[Globals.STAT_DEFENSE]);
        System.arraycopy(temp, 0, data, 23, 4);
        
        temp = Globals.intToByte(stats[Globals.STAT_SPIRIT]);
        System.arraycopy(temp, 0, data, 27, 4);
        
        try {
            FileUtils.writeByteArrayToFile(new File(saveFile + ".tcdat"), data);
        } catch (IOException ex) {
            Logger.getLogger(SaveData.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void readData(byte saveNum) {
        byte[] data = new byte[15 + 4 * 3 + 4];
        byte[] temp = new byte[15];

        try {
            data = FileUtils.readFileToByteArray(new File(saveNum + ".tcdat"));
        } catch (IOException ex) {
            Logger.getLogger(SaveData.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.arraycopy(data, 0, temp, 0, 15);
        this.name = new String(temp, StandardCharsets.UTF_8).trim();

        temp = new byte[4];
        System.arraycopy(data, 15, temp, 0, 4);
        uniqueID = Globals.bytesToInt(temp);

        System.arraycopy(data, 19, temp, 0, 4);
        stats[Globals.STAT_POWER] = Globals.bytesToInt(temp);

        System.arraycopy(data, 23, temp, 0, 4);
        stats[Globals.STAT_DEFENSE] = Globals.bytesToInt(temp);

        System.arraycopy(data, 27, temp, 0, 4);
        stats[Globals.STAT_SPIRIT] = Globals.bytesToInt(temp);
    }
}
