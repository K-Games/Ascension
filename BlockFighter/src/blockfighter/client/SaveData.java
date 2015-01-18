package blockfighter.client;

import blockfighter.client.entities.items.ItemEquip;
import blockfighter.client.entities.items.ItemUpgrade;

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

    private double[] baseStats = new double[Globals.NUM_STATS],
            totalStats = new double[Globals.NUM_STATS],
            bonusStats = new double[Globals.NUM_STATS];

    private int uniqueID;
    private String name;

    private ItemEquip[][] inventory = new ItemEquip[Globals.NUM_ITEM_TYPES][];

    private ItemUpgrade[] upgrades = new ItemUpgrade[100];

    private ItemEquip[] equipment = new ItemEquip[Globals.NUM_EQUIP_SLOTS];

    public SaveData(String n) {
        name = n;
        Random rng = new Random();
        uniqueID = rng.nextInt(Integer.MAX_VALUE);
        baseStats[Globals.STAT_LEVEL] = 100;
        baseStats[Globals.STAT_POWER] = 2000;
        baseStats[Globals.STAT_DEFENSE] = 2000;
        baseStats[Globals.STAT_SPIRIT] = 2000;

        for (int i = 0; i < inventory.length; i++) {
            inventory[i] = new ItemEquip[100];
            for (int j = 0; i < 4 && j < 100; j++) {
                double[] stats = new double[Globals.NUM_STATS];
                stats[Globals.STAT_LEVEL] = 100;
                stats[Globals.STAT_POWER] = 112;
                stats[Globals.STAT_DEFENSE] = 112;
                stats[Globals.STAT_SPIRIT] = 112;
                stats[Globals.STAT_REGEN] = 300;
                stats[Globals.STAT_CRITDMG] = 0.90;
                stats[Globals.STAT_CRITCHANCE] = 0.20;
                inventory[i][j] = new ItemEquip(stats, j, 0, 100001);
                upgrades[j] = new ItemUpgrade(1, i + 1);
            }
        }

        for (int i = 0; i < 11; i++) {
            double[] stats = new double[Globals.NUM_STATS];
            stats[Globals.STAT_LEVEL] = 100;
            stats[Globals.STAT_POWER] = 112;
            stats[Globals.STAT_DEFENSE] = 112;
            stats[Globals.STAT_SPIRIT] = 112;
            stats[Globals.STAT_REGEN] = 300;
            stats[Globals.STAT_CRITDMG] = 0.90;
            stats[Globals.STAT_CRITCHANCE] = 0.20;
            equipment[i] = new ItemEquip(stats, i, i * 0.01, 100001);
        }
    }

    public static void saveData(byte saveNum, SaveData c) {
        byte[] data = new byte[45319];
        byte[] temp = c.name.getBytes(StandardCharsets.UTF_8);

        int pos = 0;
        System.arraycopy(temp, 0, data, pos, temp.length);
        pos += Globals.MAX_NAME_LENGTH;

        temp = Globals.intToByte(c.uniqueID);
        System.arraycopy(temp, 0, data, pos, temp.length);
        pos += temp.length;

        temp = Globals.intToByte((int) c.baseStats[Globals.STAT_LEVEL]);
        System.arraycopy(temp, 0, data, pos, temp.length);
        pos += temp.length;

        temp = Globals.intToByte((int) c.baseStats[Globals.STAT_POWER]);
        System.arraycopy(temp, 0, data, pos, temp.length);
        pos += temp.length;

        temp = Globals.intToByte((int) c.baseStats[Globals.STAT_DEFENSE]);
        System.arraycopy(temp, 0, data, pos, temp.length);
        pos += temp.length;

        temp = Globals.intToByte((int) c.baseStats[Globals.STAT_SPIRIT]);
        System.arraycopy(temp, 0, data, pos, temp.length);
        pos += temp.length;

        pos = saveItems(data, c.equipment, pos);
        for (ItemEquip[] e : c.inventory) {
            pos = saveItems(data, e, pos);
        }
        saveItems(data, c.upgrades, pos);
        try {
            FileUtils.writeByteArrayToFile(new File(saveNum + ".tcdat"), data);
        } catch (IOException ex) {
            Logger.getLogger(SaveData.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static int saveItems(byte[] data, ItemUpgrade[] e, int pos) {
        for (ItemUpgrade item : e) {
            if (item == null) {
                pos += 2 * 4;
                continue;
            }
            byte[] temp;
            temp = Globals.intToByte(item.getItemCode());
            System.arraycopy(temp, 0, data, pos, temp.length);
            pos += temp.length;
            temp = Globals.intToByte((int) item.getLevel());
            System.arraycopy(temp, 0, data, pos, temp.length);
            pos += temp.length;
        }
        return pos;
    }

    private static int saveItems(byte[] data, ItemEquip[] e, int pos) {
        for (ItemEquip item : e) {
            if (item == null) {
                pos += 11 * 4;
                continue;
            }
            byte[] temp;
            temp = Globals.intToByte(item.getItemCode());
            System.arraycopy(temp, 0, data, pos, temp.length);
            pos += temp.length;
            temp = Globals.intToByte((int) item.getBaseStats()[Globals.STAT_LEVEL]);
            System.arraycopy(temp, 0, data, pos, temp.length);
            pos += temp.length;
            temp = Globals.intToByte((int) item.getBaseStats()[Globals.STAT_POWER]);
            System.arraycopy(temp, 0, data, pos, temp.length);
            pos += temp.length;
            temp = Globals.intToByte((int) item.getBaseStats()[Globals.STAT_DEFENSE]);
            System.arraycopy(temp, 0, data, pos, temp.length);
            pos += temp.length;
            temp = Globals.intToByte((int) item.getBaseStats()[Globals.STAT_SPIRIT]);
            System.arraycopy(temp, 0, data, pos, temp.length);
            pos += temp.length;
            temp = Globals.intToByte((int) item.getBaseStats()[Globals.STAT_ARMOR]);
            System.arraycopy(temp, 0, data, pos, temp.length);
            pos += temp.length;
            temp = Globals.intToByte((int) (item.getBaseStats()[Globals.STAT_REGEN] * 10));
            System.arraycopy(temp, 0, data, pos, temp.length);
            pos += temp.length;
            temp = Globals.intToByte((int) (item.getBaseStats()[Globals.STAT_CRITDMG] * 10000));
            System.arraycopy(temp, 0, data, pos, temp.length);
            pos += temp.length;
            temp = Globals.intToByte((int) (item.getBaseStats()[Globals.STAT_CRITCHANCE] * 10000));
            System.arraycopy(temp, 0, data, pos, temp.length);
            pos += temp.length;
            temp = Globals.intToByte(item.getUpgrades());
            System.arraycopy(temp, 0, data, pos, temp.length);
            pos += temp.length;
            temp = Globals.intToByte((int) (item.getBonusMult() * 100));
            System.arraycopy(temp, 0, data, pos, temp.length);
            pos += temp.length;
        }
        return pos;
    }

    public static SaveData readData(byte saveNum) {
        SaveData c = new SaveData("");
        byte[] data, temp = new byte[Globals.MAX_NAME_LENGTH];

        try {
            data = FileUtils.readFileToByteArray(new File(saveNum + ".tcdat"));
        } catch (IOException ex) {
            return null;
        }

        int pos = 0;
        System.arraycopy(data, pos, temp, 0, temp.length);
        c.name = new String(temp, StandardCharsets.UTF_8).trim();
        pos += Globals.MAX_NAME_LENGTH;

        temp = new byte[4];
        System.arraycopy(data, pos, temp, 0, temp.length);
        c.uniqueID = Globals.bytesToInt(temp);
        pos += temp.length;

        System.arraycopy(data, pos, temp, 0, temp.length);
        c.baseStats[Globals.STAT_LEVEL] = Globals.bytesToInt(temp);
        pos += temp.length;

        System.arraycopy(data, pos, temp, 0, temp.length);
        c.baseStats[Globals.STAT_POWER] = Globals.bytesToInt(temp);
        pos += temp.length;

        System.arraycopy(data, pos, temp, 0, temp.length);
        c.baseStats[Globals.STAT_DEFENSE] = Globals.bytesToInt(temp);
        pos += temp.length;

        System.arraycopy(data, pos, temp, 0, temp.length);
        c.baseStats[Globals.STAT_SPIRIT] = Globals.bytesToInt(temp);
        pos += temp.length;

        pos = readItems(data, c.equipment, pos);
        for (ItemEquip[] e : c.inventory) {
            pos = readItems(data, e, pos);
        }
        readItems(data, c.upgrades, pos);
        c.calcStats();
        return c;
    }

    private static int readItems(byte[] data, ItemUpgrade[] e, int pos) {
        for (int i = 0; i < e.length; i++) {
            byte[] temp = new byte[4];
            int itemCode;
            int level;

            System.arraycopy(data, pos, temp, 0, temp.length);
            itemCode = Globals.bytesToInt(temp);
            pos += temp.length;

            System.arraycopy(data, pos, temp, 0, temp.length);
            level = Globals.bytesToInt(temp);
            pos += temp.length;

            if (!ItemEquip.isValidItem(itemCode)) {
                e[i] = null;
            } else {
                e[i] = new ItemUpgrade(itemCode, level);
            }
        }
        return pos;
    }

    private static int readItems(byte[] data, ItemEquip[] e, int pos) {
        for (int i = 0; i < e.length; i++) {
            double[] bs = new double[Globals.NUM_STATS];
            byte[] temp = new byte[4];
            int itemCode;
            int upgrades;
            double bMult;

            System.arraycopy(data, pos, temp, 0, temp.length);
            itemCode = Globals.bytesToInt(temp);
            pos += temp.length;

            System.arraycopy(data, pos, temp, 0, temp.length);
            bs[Globals.STAT_LEVEL] = Globals.bytesToInt(temp);
            pos += temp.length;

            System.arraycopy(data, pos, temp, 0, temp.length);
            bs[Globals.STAT_POWER] = Globals.bytesToInt(temp);
            pos += temp.length;

            System.arraycopy(data, pos, temp, 0, temp.length);
            bs[Globals.STAT_DEFENSE] = Globals.bytesToInt(temp);
            pos += temp.length;

            System.arraycopy(data, pos, temp, 0, temp.length);
            bs[Globals.STAT_SPIRIT] = Globals.bytesToInt(temp);
            pos += temp.length;

            System.arraycopy(data, pos, temp, 0, temp.length);
            bs[Globals.STAT_ARMOR] = Globals.bytesToInt(temp);
            pos += temp.length;
            System.arraycopy(data, pos, temp, 0, temp.length);
            bs[Globals.STAT_REGEN] = Globals.bytesToInt(temp) / 10D;
            pos += temp.length;

            System.arraycopy(data, pos, temp, 0, temp.length);
            bs[Globals.STAT_CRITDMG] = Globals.bytesToInt(temp) / 10000D;
            pos += temp.length;

            System.arraycopy(data, pos, temp, 0, temp.length);
            bs[Globals.STAT_CRITCHANCE] = Globals.bytesToInt(temp) / 10000D;
            pos += temp.length;

            System.arraycopy(data, pos, temp, 0, temp.length);
            upgrades = Globals.bytesToInt(temp);
            pos += temp.length;

            System.arraycopy(data, pos, temp, 0, temp.length);
            bMult = Globals.bytesToInt(temp) / 100D;
            pos += temp.length;

            if (!ItemEquip.isValidItem(itemCode)) {
                e[i] = null;
            } else {
                e[i] = new ItemEquip(bs, upgrades, bMult, itemCode);
            }

        }
        return pos;
    }

    public String getPlayerName() {
        return name;
    }

    public double[] getBaseStats() {
        return baseStats;
    }

    public double[] getStats() {
        return totalStats;
    }

    public int getUniqueID() {
        return uniqueID;
    }

    private void calcStats() {

        for (int i = 0; i < bonusStats.length; i++) {
            for (ItemEquip e : equipment) {
                if (i != Globals.STAT_LEVEL && e != null) {
                    bonusStats[i] += e.getStats()[i];
                }
            }
        }

        System.arraycopy(baseStats, 0, totalStats, 0, baseStats.length);

        totalStats[Globals.STAT_POWER] = baseStats[Globals.STAT_POWER] + bonusStats[Globals.STAT_POWER];
        totalStats[Globals.STAT_DEFENSE] = baseStats[Globals.STAT_DEFENSE] + bonusStats[Globals.STAT_DEFENSE];
        totalStats[Globals.STAT_SPIRIT] = baseStats[Globals.STAT_SPIRIT] + bonusStats[Globals.STAT_SPIRIT];

        totalStats[Globals.STAT_MAXHP] = Globals.calcMaxHP(baseStats[Globals.STAT_DEFENSE] + bonusStats[Globals.STAT_DEFENSE]);
        totalStats[Globals.STAT_MINHP] = baseStats[Globals.STAT_MAXHP];
        totalStats[Globals.STAT_MINDMG] = Globals.calcMinDmg(baseStats[Globals.STAT_POWER] + bonusStats[Globals.STAT_POWER]);
        totalStats[Globals.STAT_MAXDMG] = Globals.calcMaxDmg(baseStats[Globals.STAT_POWER] + bonusStats[Globals.STAT_POWER]);

        baseStats[Globals.STAT_ARMOR] = Globals.calcArmor(baseStats[Globals.STAT_DEFENSE] + bonusStats[Globals.STAT_DEFENSE]);
        baseStats[Globals.STAT_REGEN] = Globals.calcRegen(baseStats[Globals.STAT_SPIRIT] + bonusStats[Globals.STAT_SPIRIT]);
        baseStats[Globals.STAT_CRITCHANCE] = Globals.calcCritChance(baseStats[Globals.STAT_SPIRIT] + bonusStats[Globals.STAT_SPIRIT]);
        baseStats[Globals.STAT_CRITDMG] = Globals.calcCritDmg(baseStats[Globals.STAT_SPIRIT] + bonusStats[Globals.STAT_SPIRIT]);

        totalStats[Globals.STAT_ARMOR] = baseStats[Globals.STAT_ARMOR] + bonusStats[Globals.STAT_ARMOR];
        totalStats[Globals.STAT_REGEN] = baseStats[Globals.STAT_REGEN] + bonusStats[Globals.STAT_REGEN];
        totalStats[Globals.STAT_CRITCHANCE] = baseStats[Globals.STAT_CRITCHANCE] + bonusStats[Globals.STAT_CRITCHANCE];
        totalStats[Globals.STAT_CRITDMG] = baseStats[Globals.STAT_CRITDMG] + bonusStats[Globals.STAT_CRITDMG];
    }

    public ItemEquip[] getInventory(byte type) {
        return inventory[type];
    }

    public ItemEquip[] getEquip() {
        return equipment;
    }

    public double[] getBonusStats() {
        return bonusStats;
    }
}
