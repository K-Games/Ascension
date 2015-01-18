package blockfighter.client.entities.items;

import blockfighter.client.Globals;
import java.awt.Graphics;
import java.util.HashMap;

/**
 *
 * @author Ken
 */
public class ItemEquip implements PlayerItem {

    public final static int TEMP_WEAP = 100001;
    private final static int[] ITEM_CODES = {TEMP_WEAP};

    private final static HashMap<Integer, String> ITEM_NAMES = new HashMap<>(ITEM_CODES.length);

    public final static byte TIER_COMMON = 0,
            TIER_UNCOMMON = 1,
            TIER_RARE = 2, //.15(15%)-.5(50%) bonus
            TIER_RUNIC = 3,//.51-.8
            TIER_LEGENDARY = 4,//.81-.95
            TIER_ARCHAIC = 5;//.96-1

    protected double[] baseStats = new double[Globals.NUM_STATS],
            totalStats = new double[Globals.NUM_STATS];
    protected int upgrades;
    protected double bonusMult;
    protected byte tier = TIER_COMMON;
    protected int itemCode;
    
    public static void loadItemNames(){
        ITEM_NAMES.put(TEMP_WEAP, "Sword");
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
        totalStats[Globals.STAT_POWER] = baseStats[Globals.STAT_POWER] * (1 + bonusMult + upgrades * 0.01);
        totalStats[Globals.STAT_DEFENSE] = baseStats[Globals.STAT_DEFENSE] * (1 + bonusMult + upgrades * 0.01);
        totalStats[Globals.STAT_SPIRIT] = baseStats[Globals.STAT_SPIRIT] * (1 + bonusMult + upgrades * 0.01);

        if (baseStats[Globals.STAT_CRITCHANCE] > 0) {
            totalStats[Globals.STAT_CRITCHANCE] = baseStats[Globals.STAT_CRITCHANCE] + upgrades * 0.005;
        }
        if (baseStats[Globals.STAT_CRITDMG] > 0) {
            totalStats[Globals.STAT_CRITDMG] = baseStats[Globals.STAT_CRITDMG] + upgrades * 0.02;
        }
        if (baseStats[Globals.STAT_ARMOR] > 0) {
            totalStats[Globals.STAT_ARMOR] = baseStats[Globals.STAT_ARMOR] + upgrades * 20;
        }
        if (baseStats[Globals.STAT_REGEN] > 0) {
            totalStats[Globals.STAT_REGEN] = baseStats[Globals.STAT_REGEN] + upgrades * 6;
        }
        if (bonusMult + upgrades * 0.01 >= 0.96) {
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
    
    public String getItemName(){
        return ITEM_NAMES.get(itemCode);
    }
}
