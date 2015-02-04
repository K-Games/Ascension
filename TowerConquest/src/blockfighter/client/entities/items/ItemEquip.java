package blockfighter.client.entities.items;

import blockfighter.client.Globals;
import static blockfighter.client.Globals.*;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import javax.imageio.ImageIO;

/**
 *
 * @author Ken Kwan
 */
public class ItemEquip implements Item {

    public final static int TEMP_SWORD = 100000,
            TEMP_BLADE = 100001,
            TEMP_SHIELD = 110000,
            TEMP_BOW = 120000;
    public final static int TEMP_HEAD = 200000;
    public final static int TEMP_CHEST = 300000;
    public final static int TEMP_PANTS = 400000;
    public final static int TEMP_SHOULDER = 500000;
    public final static int TEMP_GLOVE = 600000;
    public final static int TEMP_SHOE = 700000;
    public final static int TEMP_BELT = 800000;
    public final static int TEMP_RING = 900000;
    public final static int TEMP_AMULET = 1000000;

    public final static double UPGRADE_CRITCHANCE = 0.0005,//0.05%
            UPGRADE_CRITDMG = 0.02, //2%
            UPGRADE_REGEN = 3,
            UPGRADE_ARMOR = 6;

    public final static int[] ITEM_CODES = {
        TEMP_SWORD, TEMP_HEAD, TEMP_CHEST,
        TEMP_PANTS, TEMP_SHOULDER, TEMP_GLOVE,
        TEMP_SHOE, TEMP_BELT, TEMP_RING,
        TEMP_AMULET, TEMP_BLADE, TEMP_SHIELD, TEMP_BOW};

    private final static HashMap<Integer, String> ITEM_NAMES = new HashMap<>(ITEM_CODES.length);
    private final static HashMap<Integer, BufferedImage> ITEM_ICONS = new HashMap<>(ITEM_CODES.length);
    private final static HashMap<Integer, BufferedImage[][]> ITEM_SPRITES = new HashMap<>(ITEM_CODES.length);
    private final static HashMap<String, Point> ITEM_ORIGINPOINT = new HashMap<>(ITEM_CODES.length * Globals.NUM_PLAYER_STATE);

    public final static byte TIER_COMMON = 0,
            TIER_UNCOMMON = 1,
            TIER_RARE = 2,      //.15(15%)-.5(50%) bonus
            TIER_RUNIC = 3,     //.51-.8
            TIER_LEGENDARY = 4, //.81-.95
            TIER_ARCHAIC = 5,   //.96-1.1
            TIER_DIVINE = 6;    //1.1+

    protected double[] baseStats = new double[Globals.NUM_STATS],
            totalStats = new double[Globals.NUM_STATS];
    protected int upgrades;
    protected double bonusMult;
    protected byte tier = TIER_COMMON;
    protected int itemCode;

    public static void unloadSprites() {
        ITEM_SPRITES.clear();
    }

    public static void loadItemSprite(int code) {
        BufferedImage[][] load = new BufferedImage[NUM_PLAYER_STATE][];

        try {
            ITEM_ICONS.put(code, ImageIO.read(Globals.class.getResource("sprites/character/equip/" + code + "/attack/mainhand1/icon.png")));
        } catch (Exception ex) {
        }

        load[PLAYER_STATE_ATTACK1] = new BufferedImage[5];
        for (int i = 0; i < load[PLAYER_STATE_ATTACK1].length; i++) {
            try {
                load[PLAYER_STATE_ATTACK1][i] = ImageIO.read(Globals.class.getResource("sprites/character/equip/" + code + "/attack/mainhand1/" + i + ".png"));
            } catch (Exception ex) {
            }
        }

        load[PLAYER_STATE_ATTACK2] = new BufferedImage[5];
        for (int i = 0; i < load[PLAYER_STATE_ATTACK2].length; i++) {
            try {
                load[PLAYER_STATE_ATTACK2][i] = ImageIO.read(Globals.class.getResource("sprites/character/equip/" + code + "/attack/mainhand2/" + i + ".png"));
            } catch (Exception ex) {
            }
        }

        load[PLAYER_STATE_ATTACKOFF1] = new BufferedImage[5];
        for (int i = 0; i < load[PLAYER_STATE_ATTACKOFF1].length; i++) {
            try {
                load[PLAYER_STATE_ATTACKOFF1][i] = ImageIO.read(Globals.class.getResource("sprites/character/equip/" + code + "/attack/offhand1/" + i + ".png"));
            } catch (Exception ex) {
            }
        }

        load[PLAYER_STATE_ATTACKOFF2] = new BufferedImage[5];
        for (int i = 0; i < load[PLAYER_STATE_ATTACKOFF2].length; i++) {
            try {
                load[PLAYER_STATE_ATTACKOFF2][i] = ImageIO.read(Globals.class.getResource("sprites/character/equip/" + code + "/attack/offhand2/" + i + ".png"));
            } catch (Exception ex) {
            }
        }

        load[PLAYER_STATE_ATTACKBOW] = new BufferedImage[5];
        for (int i = 0; i < load[PLAYER_STATE_ATTACKBOW].length; i++) {
            try {
                load[PLAYER_STATE_ATTACKBOW][i] = ImageIO.read(Globals.class.getResource("sprites/character/equip/" + code + "/attack/bow/" + i + ".png"));
            } catch (Exception ex) {
            }
        }

        load[PLAYER_STATE_STAND] = new BufferedImage[9];
        for (int i = 0; i < load[PLAYER_STATE_STAND].length; i++) {
            try {
                load[PLAYER_STATE_STAND][i] = ImageIO.read(Globals.class.getResource("sprites/character/equip/" + code + "/stand/" + i + ".png"));
            } catch (Exception ex) {
            }
        }

        load[PLAYER_STATE_WALK] = new BufferedImage[19];
        for (int i = 0; i < load[PLAYER_STATE_WALK].length; i++) {
            try {
                load[PLAYER_STATE_WALK][i] = ImageIO.read(Globals.class.getResource("sprites/character/equip/" + code + "/walk/" + i + ".png"));
            } catch (Exception ex) {
            }
        }

        load[PLAYER_STATE_JUMP] = new BufferedImage[1];
        try {
            load[PLAYER_STATE_JUMP][0] = ImageIO.read(Globals.class.getResource("sprites/character/equip/" + code + "/jump/0.png"));
        } catch (Exception ex) {
        }
        ITEM_SPRITES.put(code, load);
    }

    public static void loadItemNames() {
        ITEM_NAMES.put(TEMP_SWORD, "Sword");
        ITEM_NAMES.put(TEMP_HEAD, "Head");
        ITEM_NAMES.put(TEMP_CHEST, "Chest");
        ITEM_NAMES.put(TEMP_PANTS, "Pants");
        ITEM_NAMES.put(TEMP_SHOULDER, "Shoulder");
        ITEM_NAMES.put(TEMP_GLOVE, "Gloves");
        ITEM_NAMES.put(TEMP_SHOE, "Shoes");
        ITEM_NAMES.put(TEMP_BELT, "Belt");
        ITEM_NAMES.put(TEMP_RING, "Ring");
        ITEM_NAMES.put(TEMP_AMULET, "Amulet");
        ITEM_NAMES.put(TEMP_BLADE, "Blade");
        ITEM_NAMES.put(TEMP_SHIELD, "Shield");
        ITEM_NAMES.put(TEMP_BOW, "Bow");
    }

    public static void loadItemDrawOrigin() {
        ITEM_ORIGINPOINT.put(TEMP_SWORD + "_" + Globals.PLAYER_STATE_STAND, new Point(-35, -80));
        ITEM_ORIGINPOINT.put(TEMP_SWORD + "_" + Globals.PLAYER_STATE_WALK, new Point(-33, -207));
        ITEM_ORIGINPOINT.put(TEMP_SWORD + "_" + Globals.PLAYER_STATE_JUMP, new Point(-30, -180));
        ITEM_ORIGINPOINT.put(TEMP_SWORD + "_" + Globals.PLAYER_STATE_ATTACK1, new Point(-105, -243));
        ITEM_ORIGINPOINT.put(TEMP_SWORD + "_" + Globals.PLAYER_STATE_ATTACK2, new Point(17, -117));
        ITEM_ORIGINPOINT.put(TEMP_SWORD + "_" + Globals.PLAYER_STATE_ATTACKOFF1, new Point(25, -105));
        ITEM_ORIGINPOINT.put(TEMP_SWORD + "_" + Globals.PLAYER_STATE_ATTACKOFF2, new Point(10, -135));
        
        ITEM_ORIGINPOINT.put(TEMP_BOW + "_" + Globals.PLAYER_STATE_STAND, new Point(-111, -122));
        ITEM_ORIGINPOINT.put(TEMP_BOW + "_" + Globals.PLAYER_STATE_WALK, new Point(-115, -177));
        ITEM_ORIGINPOINT.put(TEMP_BOW + "_" + Globals.PLAYER_STATE_JUMP, new Point(-65, -165));
        ITEM_ORIGINPOINT.put(TEMP_BOW + "_" + Globals.PLAYER_STATE_ATTACKBOW, new Point(-25, -185));
    }

    public double[] getTotalStats() {
        return totalStats;
    }

    public double[] getBaseStats() {
        return baseStats;
    }

    public ItemEquip(int ic) {
        itemCode = ic;
    }

    public ItemEquip(double[] bs, int u, double mult, int ic) {
        itemCode = ic;
        baseStats = bs;
        upgrades = u;
        bonusMult = mult;
        update();
    }

    @Override
    public void draw(Graphics2D g, int x, int y) {
        drawMenu(g, x, y);
    }

    private void drawMenu(Graphics2D g, int x, int y) {
        if (ITEM_ICONS.containsKey(itemCode)) {
            BufferedImage sprite = ITEM_ICONS.get(itemCode);
            g.drawImage(sprite, x, y, null);
        } else {
            g.setFont(Globals.ARIAL_15PT);
            g.drawString("PH", x + 20, y + 30);
        }
    }

    public void drawIngame(Graphics2D g, int x, int y, byte state, byte frame, byte facing) {
        if (ITEM_SPRITES.containsKey(itemCode)) {
            BufferedImage sprite = ITEM_SPRITES.get(itemCode)[state][frame];
            if (sprite != null) {
                int sX = x + ((facing == Globals.RIGHT) ? 1 : -1) * ITEM_ORIGINPOINT.get(itemCode + "_" + state).x;
                int sY = y + ITEM_ORIGINPOINT.get(itemCode + "_" + state).y;
                int dX = sX + ((facing == Globals.RIGHT) ? 1 : -1) * sprite.getWidth();
                int dY = sY + sprite.getHeight();
                g.drawImage(sprite, sX, sY, dX, dY, 0, 0, sprite.getWidth(), sprite.getHeight(), null);
            }
        } else {
            ItemEquip.loadItemSprite(itemCode);
        }
    }

    public void drawIngame(Graphics2D g, int x, int y, byte state, byte frame, byte facing, boolean offhand) {
        if (getSlot(itemCode) != Globals.ITEM_OFFHAND) {
            return;
        }
        drawIngame(g, x, y, state, frame, facing);
    }

    public int getUpgrades() {
        return upgrades;
    }

    private void update() {
        System.arraycopy(baseStats, 0, totalStats, 0, baseStats.length);
        totalStats[Globals.STAT_POWER] = Math.round(baseStats[Globals.STAT_POWER] * (1 + bonusMult + upgrades * 0.02));
        totalStats[Globals.STAT_DEFENSE] = Math.round(baseStats[Globals.STAT_DEFENSE] * (1 + bonusMult + upgrades * 0.02));
        totalStats[Globals.STAT_SPIRIT] = Math.round(baseStats[Globals.STAT_SPIRIT] * (1 + bonusMult + upgrades * 0.02));

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

    public void addUpgrade(int amount) {
        upgrades += amount;
        update();
    }

    public static byte getSlot(int i) {
        if (i >= 100000 && i <= 109999) { //Swords
            return Globals.ITEM_WEAPON;
        } else if (i >= 110000 && i <= 119999) { //Shields/Quivers
            return Globals.ITEM_OFFHAND;
        } else if (i >= 120000 && i <= 129999) { //Bows
            return Globals.ITEM_BOW;
        } else if (i >= 200000 && i <= 209999) {
            return Globals.ITEM_HEAD;
        } else if (i >= 300000 && i <= 309999) {
            return Globals.ITEM_CHEST;
        } else if (i >= 400000 && i <= 409999) {
            return Globals.ITEM_PANTS;
        } else if (i >= 500000 && i <= 509999) {
            return Globals.ITEM_SHOULDER;
        } else if (i >= 600000 && i <= 609999) {
            return Globals.ITEM_GLOVE;
        } else if (i >= 700000 && i <= 709999) {
            return Globals.ITEM_SHOE;
        } else if (i >= 800000 && i <= 809999) {
            return Globals.ITEM_BELT;
        } else if (i >= 900000 && i <= 909999) {
            return Globals.ITEM_RING;
        } else if (i >= 1000000 && i <= 1009999) {
            return Globals.ITEM_AMULET;
        }
        return -1;
    }
}
