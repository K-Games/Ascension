package blockfighter.client.entities.items;

import blockfighter.shared.Globals;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;

public class ItemUpgrade implements Item {

    public final static int ITEM_TOME = 100;
    private final static HashMap<Integer, String> ITEM_NAMES = new HashMap<>();
    private final static HashMap<Integer, BufferedImage> ITEM_ICONS = new HashMap<>();

    protected int level;
    protected int itemCode;

    static {
        loadUpgradeItems();
    }

    private static void loadUpgradeItems() {
        ITEM_NAMES.put(ITEM_TOME, "Soul Stone");
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

    public static double upgradeChance(final ItemEquip e, final ItemUpgrade... upgrades) {
        if (e == null) {
            return 0;
        }
        ItemUpgrade highestUpgrade = null;
        for (ItemUpgrade upgrade : upgrades) {
            if (upgrade != null) {
                if (highestUpgrade == null
                        || upgrade.getLevel() > highestUpgrade.getLevel()) {
                    highestUpgrade = upgrade;
                }
            }
        }

        if (highestUpgrade == null) {
            return 0;
        }

        int power = (int) (e.getTotalStats()[Globals.STAT_LEVEL] + e.getUpgrades() - highestUpgrade.level);
        for (ItemUpgrade upgrade : upgrades) {
            if (upgrade != null && upgrade != highestUpgrade) {
                power--;
            }
        }

        if (power < 0) {
            power = 0;
        }

        return Math.pow(0.8, power);
    }

    public static boolean rollUpgrade(final ItemEquip e, final ItemUpgrade... upgrades) {
        final int roll = Globals.rng(10000) + 1;
        return roll < (int) (upgradeChance(e, upgrades) * 10000);
    }

    @Override
    public int getItemCode() {
        return this.itemCode;
    }

    public static boolean isValidItem(final int i) {
        return Globals.ITEM_UPGRADE_CODES.contains(i);
    }

    @Override
    public void draw(final Graphics2D g, final int x, final int y) {
        if (ITEM_ICONS.containsKey(this.itemCode)) {
            final BufferedImage sprite = ITEM_ICONS.get(this.itemCode);
            if (sprite != null) {
                g.drawImage(sprite, x, y, null);
                g.setFont(Globals.ARIAL_15PT);
                g.setColor(Color.WHITE);
                g.drawString(Integer.toString(getLevel()), x + 2, y + 55);

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
        return "Level " + getLevel() + " " + ITEM_NAMES.get(this.itemCode);
    }

    @Override
    public void drawInfo(Graphics2D g, Rectangle2D.Double box) {
        g.setColor(new Color(30, 30, 30, 185));
        int y = (int) box.y;
        int x = (int) box.x;

        g.setFont(Globals.ARIAL_15PT);
        final int boxHeight = 70, boxWidth = g.getFontMetrics().stringWidth("Can be infused into any equipment to enhance it.") + 20;

        if (y + boxHeight > 720) {
            y = 700 - boxHeight;
        }

        if (x + 30 + boxWidth > 1280) {
            x = 1240 - boxWidth;
        }
        g.fillRect(x + 30, y, boxWidth, boxHeight);
        g.setColor(Color.BLACK);
        g.drawRect(x + 30, y, boxWidth, boxHeight);
        g.drawRect(x + 31, y + 1, boxWidth - 2, boxHeight - 2);

        g.setFont(Globals.ARIAL_15PT);
        g.setColor(Color.WHITE);
        g.drawString(getItemName(), x + 40, y + 20);
        g.drawString("A physical manifestation of a slain soul.", x + 40, y + 40);
        g.drawString("Can be infused into any equipment to enhance it.", x + 40, y + 60);
    }
}
