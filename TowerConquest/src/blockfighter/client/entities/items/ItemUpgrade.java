package blockfighter.client.entities.items;

import blockfighter.client.Globals;
import java.awt.Graphics2D;
import java.util.HashMap;
import java.util.Random;

/**
 *
 * @author Ken Kwan
 */
public class ItemUpgrade implements Item {

    protected final static int ITEM_TOME = 1;
    private final static int[] ITEM_UPGRADES_CODES = {ITEM_TOME};
    private final static HashMap<Integer, String> ITEM_NAMES = new HashMap<>(ITEM_UPGRADES_CODES.length);

    protected int level;
    protected static Random upgradeRng = new Random();
    protected int itemCode;

    public static void loadUpgradeItems() {
        ITEM_NAMES.put(ITEM_TOME, "Tome of Enhancement");
    }

    public ItemUpgrade(int code, int l) {
        itemCode = code;
        level = l;
    }

    public int getLevel() {
        return level;
    }

    public static double upgradeChance(ItemUpgrade i, ItemEquip e) {
        if (i == null || e == null) {
            return 0;
        }
        int power = (int) (e.getStats()[Globals.STAT_LEVEL] + e.getUpgrades() - i.level);
        if (power < 0) {
            power = 0;
        }
        return Math.pow(0.8, power);
    }

    public static boolean rollUpgrade(ItemUpgrade i, ItemEquip e) {
        int roll = upgradeRng.nextInt(10000) + 1;
        return roll < (int) (upgradeChance(i, e) * 10000);
    }

    @Override
    public int getItemCode() {
        return itemCode;
    }

    public static boolean isValidItem(int i) {
        for (int k : ITEM_UPGRADES_CODES) {
            if (k == i) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void draw(Graphics2D g, int x, int y) {
        g.setFont(Globals.ARIAL_15PT);
        g.drawString("PH", x + 20, y + 30);
    }

    @Override
    public String getItemName() {
        return ITEM_NAMES.get(itemCode);
    }
}
