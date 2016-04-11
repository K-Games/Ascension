package blockfighter.client.entities.items;

import blockfighter.client.Globals;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;

/**
 *
 * @author Ken Kwan
 */
public class ItemUpgrade implements Item {

    public final static int ITEM_TOME = 1;
    private final static int[] ITEM_UPGRADES_CODES = {ITEM_TOME};
    private final static HashMap<Integer, String> ITEM_NAMES = new HashMap<>(ITEM_UPGRADES_CODES.length);
    private final static HashMap<Integer, BufferedImage> ITEM_ICONS = new HashMap<>(ITEM_UPGRADES_CODES.length);

    protected int level;
    protected int itemCode;

    static {
        loadUpgradeItems();
    }

    private static void loadUpgradeItems() {
        ITEM_NAMES.put(ITEM_TOME, "Tome of Enhancement");
    }

    public static void loadItemIcon(final int code) {
        BufferedImage icon = Globals.loadTextureResource("sprites/upgrade/" + code + ".png");
        ITEM_ICONS.put(code, icon);
    }

    public ItemUpgrade(final int code, final int l) {
        this.itemCode = code;
        this.level = l;
    }

    public int getLevel() {
        return this.level;
    }

    public static double upgradeChance(final ItemUpgrade i, final ItemEquip e) {
        if (i == null || e == null) {
            return 0;
        }
        int power = (int) (e.getTotalStats()[Globals.STAT_LEVEL] + e.getUpgrades() - i.level);
        if (power < 0) {
            power = 0;
        }
        return Math.pow(0.8, power);
    }

    public static boolean rollUpgrade(final ItemUpgrade i, final ItemEquip e) {
        final int roll = Globals.rng(10000) + 1;
        return roll < (int) (upgradeChance(i, e) * 10000);
    }

    @Override
    public int getItemCode() {
        return this.itemCode;
    }

    public static boolean isValidItem(final int i) {
        for (final int k : ITEM_UPGRADES_CODES) {
            if (k == i) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void draw(final Graphics2D g, final int x, final int y) {
        if (ITEM_ICONS.containsKey(this.itemCode)) {
            final BufferedImage sprite = ITEM_ICONS.get(this.itemCode);
            if (sprite != null) {
                g.drawImage(sprite, x, y, null);
            } else {
                g.setFont(Globals.ARIAL_15PT);
                g.setColor(Color.WHITE);
                g.drawString("PH", x + 20, y + 30);
            }
        } else {
            loadItemIcon(this.itemCode);
        }
    }

    @Override
    public String getItemName() {
        return ITEM_NAMES.get(this.itemCode);
    }
}
