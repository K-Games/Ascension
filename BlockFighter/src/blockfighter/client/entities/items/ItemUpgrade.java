package blockfighter.client.entities.items;

import blockfighter.client.Globals;
import java.awt.Graphics;
import java.util.Random;

/**
 *
 * @author Ken
 */
public class ItemUpgrade implements PlayerItem {

    protected final static int ITEM_TOME = 1;
    private final static int[] ITEM_UPGRADES_CODES = {ITEM_TOME};

    protected int level;
    protected static Random upgradeRng = new Random();
    protected int itemCode;

    public ItemUpgrade(int code, int l) {
        itemCode = code;
        level = l;
    }

    public int getLevel() {
        return level;
    }

    public static boolean rollUpgrade(ItemUpgrade i, ItemEquip e) {
        int roll = upgradeRng.nextInt(10000) + 1;
        int power = e.getUpgrades() - (i.level - (int) e.getStats()[Globals.STAT_LEVEL]);
        if (power < 0) {
            power = 0;
        }
        double chance = Math.pow(0.8, power);
        return roll < (int) (chance * 10000);
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
    public void draw(Graphics g, int x, int y) {
        
    }
}
