package blockfighter.client.entities.items;

import blockfighter.client.Globals;
import java.awt.Graphics;
import java.util.HashMap;

/**
 *
 * @author Ken
 */
public class ItemEquip implements PlayerItem {

    public final static int TEMP_WEAP = 100000,
            TEMP_OFF = 100001;
    public final static int TEMP_HEAD = 200000;
    public final static int TEMP_CHEST = 300000;
    public final static int TEMP_PANTS = 400000;
    public final static int TEMP_SHOULDER = 500000;
    public final static int TEMP_GLOVE = 600000;
    public final static int TEMP_SHOE = 700000;
    public final static int TEMP_BELT = 800000;
    public final static int TEMP_RING = 900000;
    public final static int TEMP_AMULET = 1000000;

    public final static double UPGRADE_CRITCHANCE = 0.0005,
            UPGRADE_CRITDMG = 0.02,
            UPGRADE_REGEN = 3,
            UPGRADE_ARMOR = 6;
    private final static int[] ITEM_CODES = {
        TEMP_WEAP,
        TEMP_HEAD,
        TEMP_CHEST,
        TEMP_PANTS,
        TEMP_SHOULDER,
        TEMP_GLOVE,
        TEMP_SHOE,
        TEMP_BELT,
        TEMP_RING,
        TEMP_AMULET,
        TEMP_OFF};

    private final static HashMap<Integer, String> ITEM_NAMES = new HashMap<>(ITEM_CODES.length);

    public final static byte TIER_COMMON = 0,
            TIER_UNCOMMON = 1,
            TIER_RARE = 2, //.15(15%)-.5(50%) bonus
            TIER_RUNIC = 3,//.51-.8
            TIER_LEGENDARY = 4,//.81-.95
            TIER_ARCHAIC = 5,//.96-1.1
            TIER_DIVINE = 6;//1.1+

    protected double[] baseStats = new double[Globals.NUM_STATS],
            totalStats = new double[Globals.NUM_STATS];
    protected int upgrades;
    protected double bonusMult;
    protected byte tier = TIER_COMMON;
    protected int itemCode;

    public static void loadItemNames() {
        ITEM_NAMES.put(TEMP_WEAP, "Sword");
        ITEM_NAMES.put(TEMP_HEAD, "Head");
        ITEM_NAMES.put(TEMP_CHEST, "Chest");
        ITEM_NAMES.put(TEMP_PANTS, "Pants");
        ITEM_NAMES.put(TEMP_SHOULDER, "Shoulder");
        ITEM_NAMES.put(TEMP_GLOVE, "Gloves");
        ITEM_NAMES.put(TEMP_SHOE, "Shoes");
        ITEM_NAMES.put(TEMP_BELT, "Belt");
        ITEM_NAMES.put(TEMP_RING, "Ring");
        ITEM_NAMES.put(TEMP_AMULET, "Amulet");
        ITEM_NAMES.put(TEMP_OFF, "Offhander");
    }

    public double[] getStats() {
        return totalStats;
    }

    public double[] getBaseStats() {
        return baseStats;
    }

    public ItemEquip(double[] bs, int u, double mult, int ic) {
        itemCode = ic;
        baseStats = bs;
        upgrades = u;
        bonusMult = mult;
        update();
    }

    @Override
    public void draw(Graphics g, int x, int y) {
        drawMenu(g, x, y);
    }

    private void drawMenu(Graphics g, int x, int y) {
        //Draw Icon at location x, y
        g.setFont(Globals.ARIAL_15PT);
        g.drawString("PH", x + 20, y + 30);
    }

    public void drawIngame(Graphics g, int x, int y) {

    }

    public int getUpgrades() {
        return upgrades;
    }

    private void update() {
        System.arraycopy(baseStats, 0, totalStats, 0, baseStats.length);
        totalStats[Globals.STAT_POWER] = Math.round(baseStats[Globals.STAT_POWER] * (1 + bonusMult + upgrades * 0.01));
        totalStats[Globals.STAT_DEFENSE] = Math.round(baseStats[Globals.STAT_DEFENSE] * (1 + bonusMult + upgrades * 0.01));
        totalStats[Globals.STAT_SPIRIT] = Math.round(baseStats[Globals.STAT_SPIRIT] * (1 + bonusMult + upgrades * 0.01));

        if (baseStats[Globals.STAT_CRITCHANCE] > 0) {
            totalStats[Globals.STAT_CRITCHANCE] = baseStats[Globals.STAT_CRITCHANCE] + upgrades * UPGRADE_CRITCHANCE;
        }
        if (baseStats[Globals.STAT_CRITDMG] > 0) {
            totalStats[Globals.STAT_CRITDMG] = baseStats[Globals.STAT_CRITDMG] + upgrades * UPGRADE_CRITDMG;
        }
        if (baseStats[Globals.STAT_ARMOR] > 0) {
            totalStats[Globals.STAT_ARMOR] = Math.round(baseStats[Globals.STAT_ARMOR] + upgrades * UPGRADE_ARMOR);
        }
        if (baseStats[Globals.STAT_REGEN] > 0) {
            totalStats[Globals.STAT_REGEN] = baseStats[Globals.STAT_REGEN] + upgrades * UPGRADE_REGEN;
        }
        if (bonusMult + upgrades * 0.01 >= 1.1) {
            tier = TIER_DIVINE;
        } else if (bonusMult + upgrades * 0.01 >= 0.96) {
            tier = TIER_ARCHAIC;
        } else if (bonusMult + upgrades * 0.01 >= 0.81) {
            tier = TIER_LEGENDARY;
        } else if (bonusMult + upgrades * 0.01 >= 0.51) {
            tier = TIER_RUNIC;
        } else if (bonusMult + upgrades * 0.01 >= 0.15) {
            tier = TIER_RARE;
        } else {
            if (totalStats[Globals.STAT_CRITCHANCE] > 0
                    || totalStats[Globals.STAT_CRITDMG] > 0
                    || totalStats[Globals.STAT_ARMOR] > 0
                    || totalStats[Globals.STAT_REGEN] > 0) {
                tier = TIER_UNCOMMON;
            } else {
                tier = TIER_COMMON;
            }
        }
    }

    @Override
    public int getItemCode() {
        return itemCode;
    }

    public static boolean isValidItem(int i) {
        for (int k : ITEM_CODES) {
            if (k == i) {
                return true;
            }
        }
        return false;
    }

    public double getBonusMult() {
        return bonusMult;
    }

    public byte getTier() {
        return tier;
    }

    @Override
    public String getItemName() {
        return ITEM_NAMES.get(itemCode);
    }
    
    public void addUpgrade(int amount){
        upgrades += amount;
        update();
    }
}
