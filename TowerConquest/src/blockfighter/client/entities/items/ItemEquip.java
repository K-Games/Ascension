package blockfighter.client.entities.items;

import blockfighter.client.Globals;
import static blockfighter.client.Globals.NUM_PLAYER_STATE;
import static blockfighter.client.Globals.PLAYER_STATE_ATTACK1;
import static blockfighter.client.Globals.PLAYER_STATE_ATTACK2;
import static blockfighter.client.Globals.PLAYER_STATE_ATTACKBOW;
import static blockfighter.client.Globals.PLAYER_STATE_ATTACKOFF1;
import static blockfighter.client.Globals.PLAYER_STATE_ATTACKOFF2;
import static blockfighter.client.Globals.PLAYER_STATE_BUFF;
import static blockfighter.client.Globals.PLAYER_STATE_DEAD;
import static blockfighter.client.Globals.PLAYER_STATE_JUMP;
import static blockfighter.client.Globals.PLAYER_STATE_STAND;
import static blockfighter.client.Globals.PLAYER_STATE_WALK;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Random;
import javax.imageio.ImageIO;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author Ken Kwan
 */
public class ItemEquip implements Item {

    public final static int TEMP_SWORD = 100000,
            TEMP_BLADE = 100001,
            TEMP_SHIELD = 110000,
            TEMP_BOW = 120000,
            TEMP_QUIVER = 130000;
    public final static int TEMP_HEAD = 200000;
    public final static int TEMP_CHEST = 300000;
    public final static int TEMP_PANTS = 400000;
    public final static int TEMP_SHOULDER = 500000;
    public final static int TEMP_GLOVE = 600000;
    public final static int TEMP_SHOE = 700000;
    public final static int TEMP_BELT = 800000;
    public final static int TEMP_RING = 900000;
    public final static int TEMP_AMULET = 1000000;

    private final static Random rng = new Random();
    private static DecimalFormat df = new DecimalFormat("###,###,##0.##");

    public final static double UPGRADE_CRITCHANCE = 0.002,//0.2%
            UPGRADE_CRITDMG = 0.02, //2%
            UPGRADE_REGEN = 8,
            UPGRADE_ARMOR = 24,
            UPGRADE_MULT = 0.04;

    public final static int[] ITEM_CODES = {
        TEMP_SWORD, TEMP_HEAD, TEMP_CHEST,
        TEMP_PANTS, TEMP_SHOULDER, TEMP_GLOVE,
        TEMP_SHOE, TEMP_BELT, TEMP_RING,
        TEMP_AMULET, TEMP_BLADE, TEMP_SHIELD,
        TEMP_BOW, TEMP_QUIVER};

    private final static HashMap<Byte, String> ITEM_TYPENAME = new HashMap<>(13);
    private final static HashMap<Integer, String> ITEM_NAMES = new HashMap<>(ITEM_CODES.length);
    private final static HashMap<Integer, BufferedImage> ITEM_ICONS = new HashMap<>(ITEM_CODES.length);
    private final static HashMap<String, BufferedImage[][]> ITEM_SPRITES = new HashMap<>();
    private final static HashMap<Integer, String> ITEM_DESC = new HashMap<>(ITEM_CODES.length);
    private final static HashMap<String, Point> ITEM_ORIGINPOINT = new HashMap<>(ITEM_CODES.length * Globals.NUM_PLAYER_STATE);

    public final static byte TIER_COMMON = 0,
            TIER_UNCOMMON = 1,
            TIER_RARE = 2, //.15(15%)-.5(50%) bonus
            TIER_RUNIC = 3, //.51-.8
            TIER_LEGENDARY = 4, //.81-.95
            TIER_ARCHAIC = 5, //.96-1.1
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

    public static void loadItemIcon(int code) {
        BufferedImage icon = null;
        try {
            icon = ImageIO.read(Globals.class.getResourceAsStream("sprites/character/equip/" + code + "/attack/mainhand1/icon.png"));
        } catch (Exception ex) {
        }
        ITEM_ICONS.put(code, icon);
    }

    public static void loadItemSprite(int code) {
        BufferedImage[][] load = new BufferedImage[NUM_PLAYER_STATE][];

        load[PLAYER_STATE_ATTACK1] = new BufferedImage[5];
        for (int i = 0; i < load[PLAYER_STATE_ATTACK1].length; i++) {
            try {
                load[PLAYER_STATE_ATTACK1][i] = ImageIO.read(Globals.class.getResourceAsStream("sprites/character/equip/" + code + "/attack/mainhand1/" + i + ".png"));
            } catch (Exception ex) {
            }
        }

        load[PLAYER_STATE_ATTACK2] = new BufferedImage[5];
        for (int i = 0; i < load[PLAYER_STATE_ATTACK2].length; i++) {
            try {
                load[PLAYER_STATE_ATTACK2][i] = ImageIO.read(Globals.class.getResourceAsStream("sprites/character/equip/" + code + "/attack/mainhand2/" + i + ".png"));
            } catch (Exception ex) {
            }
        }

        load[PLAYER_STATE_ATTACKOFF1] = new BufferedImage[5];
        for (int i = 0; i < load[PLAYER_STATE_ATTACKOFF1].length; i++) {
            try {
                load[PLAYER_STATE_ATTACKOFF1][i] = ImageIO.read(Globals.class.getResourceAsStream("sprites/character/equip/" + code + "/attack/offhand1/" + i + ".png"));
            } catch (Exception ex) {
            }
        }

        load[PLAYER_STATE_ATTACKOFF2] = new BufferedImage[5];
        for (int i = 0; i < load[PLAYER_STATE_ATTACKOFF2].length; i++) {
            try {
                load[PLAYER_STATE_ATTACKOFF2][i] = ImageIO.read(Globals.class.getResourceAsStream("sprites/character/equip/" + code + "/attack/offhand2/" + i + ".png"));
            } catch (Exception ex) {
            }
        }

        load[PLAYER_STATE_ATTACKBOW] = new BufferedImage[5];
        for (int i = 0; i < load[PLAYER_STATE_ATTACKBOW].length; i++) {
            try {
                load[PLAYER_STATE_ATTACKBOW][i] = ImageIO.read(Globals.class.getResourceAsStream("sprites/character/equip/" + code + "/attack/bow/" + i + ".png"));
            } catch (Exception ex) {
            }
        }

        load[PLAYER_STATE_STAND] = new BufferedImage[10];
        for (int i = 0; i < load[PLAYER_STATE_STAND].length; i++) {
            try {
                load[PLAYER_STATE_STAND][i] = ImageIO.read(Globals.class.getResourceAsStream("sprites/character/equip/" + code + "/stand/" + i + ".png"));
            } catch (Exception ex) {
            }
        }

        load[PLAYER_STATE_WALK] = new BufferedImage[19];
        for (int i = 0; i < load[PLAYER_STATE_WALK].length; i++) {
            try {
                load[PLAYER_STATE_WALK][i] = ImageIO.read(Globals.class.getResourceAsStream("sprites/character/equip/" + code + "/walk/" + i + ".png"));
            } catch (Exception ex) {
            }
        }

        load[PLAYER_STATE_JUMP] = new BufferedImage[1];
        try {
            load[PLAYER_STATE_JUMP][0] = ImageIO.read(Globals.class.getResourceAsStream("sprites/character/equip/" + code + "/jump/0.png"));
        } catch (Exception ex) {
        }
        load[PLAYER_STATE_BUFF] = new BufferedImage[10];
        for (int i = 0; i < load[PLAYER_STATE_BUFF].length; i++) {
            try {
                load[PLAYER_STATE_BUFF][i] = ImageIO.read(Globals.class.getResourceAsStream("sprites/character/equip/" + code + "/buff/" + i + ".png"));
            } catch (Exception ex) {
            }
        }
        load[PLAYER_STATE_DEAD] = new BufferedImage[15];
        for (int i = 0; i < load[PLAYER_STATE_DEAD].length; i++) {
            try {
                load[PLAYER_STATE_DEAD][i] = ImageIO.read(Globals.class.getResourceAsStream("sprites/character/equip/" + code + "/dead/" + i + ".png"));
            } catch (Exception ex) {
            }
        }
        ITEM_SPRITES.put(Integer.toString(code), load);
    }

    public static void loadOffhandSprite(int code) {
        BufferedImage[][] load = new BufferedImage[NUM_PLAYER_STATE][];

        load[PLAYER_STATE_ATTACK1] = new BufferedImage[5];
        for (int i = 0; i < load[PLAYER_STATE_ATTACK1].length; i++) {
            try {
                load[PLAYER_STATE_ATTACK1][i] = ImageIO.read(Globals.class.getResourceAsStream("sprites/character/equip/" + code + "/offhand/attack/mainhand1/" + i + ".png"));
            } catch (Exception ex) {
            }
        }

        load[PLAYER_STATE_ATTACK2] = new BufferedImage[5];
        for (int i = 0; i < load[PLAYER_STATE_ATTACK2].length; i++) {
            try {
                load[PLAYER_STATE_ATTACK2][i] = ImageIO.read(Globals.class.getResourceAsStream("sprites/character/equip/" + code + "/offhand/attack/mainhand2/" + i + ".png"));
            } catch (Exception ex) {
            }
        }

        load[PLAYER_STATE_ATTACKOFF1] = new BufferedImage[5];
        for (int i = 0; i < load[PLAYER_STATE_ATTACKOFF1].length; i++) {
            try {
                load[PLAYER_STATE_ATTACKOFF1][i] = ImageIO.read(Globals.class.getResourceAsStream("sprites/character/equip/" + code + "/offhand/attack/offhand1/" + i + ".png"));
            } catch (Exception ex) {
            }
        }

        load[PLAYER_STATE_ATTACKOFF2] = new BufferedImage[5];
        for (int i = 0; i < load[PLAYER_STATE_ATTACKOFF2].length; i++) {
            try {
                load[PLAYER_STATE_ATTACKOFF2][i] = ImageIO.read(Globals.class.getResourceAsStream("sprites/character/equip/" + code + "/offhand/attack/offhand2/" + i + ".png"));
            } catch (Exception ex) {
            }
        }

        load[PLAYER_STATE_ATTACKBOW] = new BufferedImage[5];
        for (int i = 0; i < load[PLAYER_STATE_ATTACKBOW].length; i++) {
            try {
                load[PLAYER_STATE_ATTACKBOW][i] = ImageIO.read(Globals.class.getResourceAsStream("sprites/character/equip/" + code + "/offhand/attack/bow/" + i + ".png"));
            } catch (Exception ex) {
            }
        }

        load[PLAYER_STATE_STAND] = new BufferedImage[10];
        for (int i = 0; i < load[PLAYER_STATE_STAND].length; i++) {
            try {
                load[PLAYER_STATE_STAND][i] = ImageIO.read(Globals.class.getResourceAsStream("sprites/character/equip/" + code + "/offhand/stand/" + i + ".png"));
            } catch (Exception ex) {
            }
        }

        load[PLAYER_STATE_WALK] = new BufferedImage[19];
        for (int i = 0; i < load[PLAYER_STATE_WALK].length; i++) {
            try {
                load[PLAYER_STATE_WALK][i] = ImageIO.read(Globals.class.getResourceAsStream("sprites/character/equip/" + code + "/offhand/walk/" + i + ".png"));
            } catch (Exception ex) {
            }
        }

        load[PLAYER_STATE_JUMP] = new BufferedImage[1];
        try {
            load[PLAYER_STATE_JUMP][0] = ImageIO.read(Globals.class.getResourceAsStream("sprites/character/equip/" + code + "/offhand/jump/0.png"));
        } catch (Exception ex) {
        }
        
        load[PLAYER_STATE_BUFF] = new BufferedImage[10];
        for (int i = 0; i < load[PLAYER_STATE_BUFF].length; i++) {
            try {
                load[PLAYER_STATE_BUFF][i] = ImageIO.read(Globals.class.getResourceAsStream("sprites/character/equip/" + code + "/offhand/buff/" + i + ".png"));
            } catch (Exception ex) {
            }
        }
        
        load[PLAYER_STATE_DEAD] = new BufferedImage[15];
        for (int i = 0; i < load[PLAYER_STATE_DEAD].length; i++) {
            try {
                load[PLAYER_STATE_DEAD][i] = ImageIO.read(Globals.class.getResourceAsStream("sprites/character/equip/" + code + "/offhand/dead/" + i + ".png"));
            } catch (Exception ex) {
            }
        }
        ITEM_SPRITES.put(code + "_offhand", load);
    }

    public static void loadItemDetails() {
        ITEM_TYPENAME.put(Globals.ITEM_AMULET, "Amulet");
        ITEM_TYPENAME.put(Globals.ITEM_BELT, "Belt");
        ITEM_TYPENAME.put(Globals.ITEM_BOW, "Bow");
        ITEM_TYPENAME.put(Globals.ITEM_CHEST, "Chest");
        ITEM_TYPENAME.put(Globals.ITEM_GLOVE, "Glove");
        ITEM_TYPENAME.put(Globals.ITEM_HEAD, "Head");
        ITEM_TYPENAME.put(Globals.ITEM_SHIELD, "Shield");
        ITEM_TYPENAME.put(Globals.ITEM_PANTS, "Pants");
        ITEM_TYPENAME.put(Globals.ITEM_QUIVER, "Quiver");
        ITEM_TYPENAME.put(Globals.ITEM_RING, "Ring");
        ITEM_TYPENAME.put(Globals.ITEM_SHOE, "Shoe");
        ITEM_TYPENAME.put(Globals.ITEM_SHOULDER, "Shoulder");
        ITEM_TYPENAME.put(Globals.ITEM_SWORD, "Sword");

        ITEM_NAMES.put(TEMP_SWORD, "Juggernaut's Blade");
        ITEM_DESC.put(TEMP_SWORD, "SPPIIIIIINNNNN!");

        ITEM_NAMES.put(TEMP_HEAD, "Wards");
        ITEM_DESC.put(TEMP_HEAD, "Ward's plz CM.");

        ITEM_NAMES.put(TEMP_CHEST, "THAT CHEST");
        ITEM_DESC.put(TEMP_CHEST, "Lifting brah.");

        ITEM_NAMES.put(TEMP_PANTS, "Pants, BABY!");
        ITEM_NAMES.put(TEMP_SHOULDER, "Carry Shoulders");
        ITEM_NAMES.put(TEMP_GLOVE, "MIDAS!?");
        ITEM_NAMES.put(TEMP_SHOE, "Boots of Travel");
        ITEM_NAMES.put(TEMP_BELT, "BELT OF GIANT STRENGTH");
        ITEM_NAMES.put(TEMP_RING, "Ring of Basilius");
        ITEM_NAMES.put(TEMP_AMULET, "Null Talisman");
        ITEM_NAMES.put(TEMP_BLADE, "Blades of Attack");
        ITEM_NAMES.put(TEMP_SHIELD, "Poor Man's Shield");
        ITEM_NAMES.put(TEMP_BOW, "Drow Ranger");
        ITEM_NAMES.put(TEMP_QUIVER, "WindRUNNER");
    }

    public static void loadItemDrawOrigin() {
        ITEM_ORIGINPOINT.put(TEMP_SWORD + "_" + Globals.PLAYER_STATE_STAND, new Point(-45, -80));
        ITEM_ORIGINPOINT.put(TEMP_SWORD + "_" + Globals.PLAYER_STATE_WALK, new Point(-38, -200));
        ITEM_ORIGINPOINT.put(TEMP_SWORD + "_" + Globals.PLAYER_STATE_JUMP, new Point(-35, -180));
        ITEM_ORIGINPOINT.put(TEMP_SWORD + "_" + Globals.PLAYER_STATE_ATTACK1, new Point(-103, -239));
        ITEM_ORIGINPOINT.put(TEMP_SWORD + "_" + Globals.PLAYER_STATE_ATTACK2, new Point(10, -115));
        ITEM_ORIGINPOINT.put(TEMP_SWORD + "_" + Globals.PLAYER_STATE_ATTACKOFF1, new Point(12, -100));
        ITEM_ORIGINPOINT.put(TEMP_SWORD + "_" + Globals.PLAYER_STATE_ATTACKOFF2, new Point(0, -130));

        ITEM_ORIGINPOINT.put(TEMP_SWORD + "_offhand_" + Globals.PLAYER_STATE_STAND, new Point(20, -88));
        ITEM_ORIGINPOINT.put(TEMP_SWORD + "_offhand_" + Globals.PLAYER_STATE_WALK, new Point(-14, -173));
        ITEM_ORIGINPOINT.put(TEMP_SWORD + "_offhand_" + Globals.PLAYER_STATE_JUMP, new Point(30, -221));
        ITEM_ORIGINPOINT.put(TEMP_SWORD + "_offhand_" + Globals.PLAYER_STATE_ATTACK1, new Point(-43, -225));
        ITEM_ORIGINPOINT.put(TEMP_SWORD + "_offhand_" + Globals.PLAYER_STATE_ATTACK2, new Point(45, -158));

        ITEM_ORIGINPOINT.put(TEMP_BOW + "_" + Globals.PLAYER_STATE_STAND, new Point(-111, -122));
        ITEM_ORIGINPOINT.put(TEMP_BOW + "_" + Globals.PLAYER_STATE_WALK, new Point(-115, -177));
        ITEM_ORIGINPOINT.put(TEMP_BOW + "_" + Globals.PLAYER_STATE_JUMP, new Point(-65, -170));
        ITEM_ORIGINPOINT.put(TEMP_BOW + "_" + Globals.PLAYER_STATE_ATTACKBOW, new Point(-25, -185));
        
        ITEM_ORIGINPOINT.put(TEMP_BLADE + "_" + Globals.PLAYER_STATE_STAND, new Point(-45, -80));
        ITEM_ORIGINPOINT.put(TEMP_BLADE + "_" + Globals.PLAYER_STATE_WALK, new Point(-38, -200));
        ITEM_ORIGINPOINT.put(TEMP_BLADE + "_" + Globals.PLAYER_STATE_JUMP, new Point(-35, -180));
        ITEM_ORIGINPOINT.put(TEMP_BLADE + "_" + Globals.PLAYER_STATE_ATTACK1, new Point(-103, -239));
        ITEM_ORIGINPOINT.put(TEMP_BLADE + "_" + Globals.PLAYER_STATE_ATTACK2, new Point(10, -115));
        ITEM_ORIGINPOINT.put(TEMP_BLADE + "_" + Globals.PLAYER_STATE_ATTACKOFF1, new Point(12, -100));
        ITEM_ORIGINPOINT.put(TEMP_BLADE + "_" + Globals.PLAYER_STATE_ATTACKOFF2, new Point(0, -130));

        ITEM_ORIGINPOINT.put(TEMP_BLADE + "_offhand_" + Globals.PLAYER_STATE_STAND, new Point(20, -88));
        ITEM_ORIGINPOINT.put(TEMP_BLADE + "_offhand_" + Globals.PLAYER_STATE_WALK, new Point(-14, -173));
        ITEM_ORIGINPOINT.put(TEMP_BLADE + "_offhand_" + Globals.PLAYER_STATE_JUMP, new Point(30, -221));
        ITEM_ORIGINPOINT.put(TEMP_BLADE + "_offhand_" + Globals.PLAYER_STATE_ATTACK1, new Point(-43, -225));
        ITEM_ORIGINPOINT.put(TEMP_BLADE + "_offhand_" + Globals.PLAYER_STATE_ATTACK2, new Point(45, -158));

    }

    public double[] getTotalStats() {
        return totalStats;
    }

    public double[] getBaseStats() {
        return baseStats;
    }

    public static String getItemTypeName(byte itemType) {
        return ITEM_TYPENAME.get(itemType);
    }

    public ItemEquip(int ic) {
        itemCode = ic;
    }

    public ItemEquip(int ic, double level) {
        itemCode = ic;
        baseStats = new double[Globals.NUM_STATS];
        baseStats[Globals.STAT_LEVEL] = level;
        switch (getItemType(itemCode)) {
            case Globals.ITEM_WEAPON:
                baseStats[Globals.STAT_POWER] = level + (rng.nextInt(26) / 100D) * level;
                break;
            case Globals.ITEM_BOW:
                baseStats[Globals.STAT_POWER] = level + (rng.nextInt(26) / 100D) * level;
                break;
            case Globals.ITEM_SHIELD:
                baseStats[Globals.STAT_DEFENSE] = level + (rng.nextInt(26) / 100D) * level;
                break;
            case Globals.ITEM_QUIVER:
                baseStats[Globals.STAT_POWER] = level + (rng.nextInt(26) / 100D) * level;
                baseStats[Globals.STAT_CRITCHANCE] = Math.round(rng.nextInt(26) / 100D * level + level * 0.75) * 0.001;
                break;
            case Globals.ITEM_CHEST:
                baseStats[Globals.STAT_DEFENSE] = level + (rng.nextInt(26) / 100D) * level;
                break;
            case Globals.ITEM_PANTS:
                baseStats[Globals.STAT_DEFENSE] = level + (rng.nextInt(26) / 100D) * level;
                break;
            case Globals.ITEM_HEAD:
                baseStats[Globals.STAT_DEFENSE] = level + (rng.nextInt(26) / 100D) * level;
                baseStats[Globals.STAT_SPIRIT] = level + (rng.nextInt(26) / 100D) * level;
                break;
            case Globals.ITEM_SHOE:
                baseStats[Globals.STAT_SPIRIT] = level + (rng.nextInt(26) / 100D) * level;
                break;
            case Globals.ITEM_BELT:
                baseStats[Globals.STAT_SPIRIT] = level + (rng.nextInt(26) / 100D) * level;
                break;
            case Globals.ITEM_SHOULDER:
                baseStats[Globals.STAT_DEFENSE] = level + (rng.nextInt(26) / 100D) * level;
                baseStats[Globals.STAT_POWER] = level + (rng.nextInt(26) / 100D) * level;
                break;
            case Globals.ITEM_GLOVE:
                baseStats[Globals.STAT_SPIRIT] = level + (rng.nextInt(26) / 100D) * level;
                baseStats[Globals.STAT_POWER] = level + (rng.nextInt(26) / 100D) * level;
                break;
            case Globals.ITEM_AMULET:
                baseStats[Globals.STAT_CRITDMG] = Math.round(rng.nextInt(26) / 100D * level + level * 0.75) * 0.02;
                baseStats[Globals.STAT_REGEN] = rng.nextInt((int) level + 1) * 5;
                break;
            case Globals.ITEM_RING:
                baseStats[Globals.STAT_CRITCHANCE] = Math.round(rng.nextInt(26) / 100D * level + level * 0.75) * 0.001;
                baseStats[Globals.STAT_ARMOR] = Math.round(rng.nextInt(26) / 100D * level + level * 0.75) * 18;
                break;
        }
        baseStats[Globals.STAT_POWER] = Math.round(baseStats[Globals.STAT_POWER]);
        baseStats[Globals.STAT_DEFENSE] = Math.round(baseStats[Globals.STAT_DEFENSE]);
        baseStats[Globals.STAT_SPIRIT] = Math.round(baseStats[Globals.STAT_SPIRIT]);
        bonusMult = rng.nextInt(101) / 100D;
        upgrades = 0;
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

    public void drawInfo(Graphics2D g, Rectangle2D.Double box) {
        int y = (int) box.y;
        int x = (int) box.x;
        int boxHeight = 70, boxWidth;

        if (getTotalStats()[Globals.STAT_POWER] > 0) {
            boxHeight += 20;
        }
        if (getTotalStats()[Globals.STAT_DEFENSE] > 0) {
            boxHeight += 20;
        }
        if (getTotalStats()[Globals.STAT_SPIRIT] > 0) {
            boxHeight += 20;
        }
        if (getTotalStats()[Globals.STAT_REGEN] > 0) {
            boxHeight += 20;
        }
        if (getTotalStats()[Globals.STAT_ARMOR] > 0) {
            boxHeight += 20;
        }
        if (getTotalStats()[Globals.STAT_CRITDMG] > 0) {
            boxHeight += 20;
        }
        if (getTotalStats()[Globals.STAT_CRITCHANCE] > 0) {
            boxHeight += 20;
        }
        if (ITEM_DESC.containsKey(itemCode)) {
            int lines = StringUtils.countMatches(ITEM_DESC.get(itemCode), "\n") + 1;
            boxHeight += lines * 20;
        }
        g.setFont(Globals.ARIAL_15PT);
        String tierString = "";
        switch (getTier()) {
            case ItemEquip.TIER_COMMON:
                tierString = "Common ";
                break;
            case ItemEquip.TIER_UNCOMMON:
                tierString = "Uncommon ";
                break;
            case ItemEquip.TIER_RARE:
                tierString = "Rare ";
                break;
            case ItemEquip.TIER_RUNIC:
                tierString = "Runic ";
                break;
            case ItemEquip.TIER_LEGENDARY:
                tierString = "Legendary ";
                break;
            case ItemEquip.TIER_ARCHAIC:
                tierString = "Archaic ";
                break;
            case ItemEquip.TIER_DIVINE:
                tierString = "Divine ";
                break;
        }
        int maxWidth = 0;
        if (getUpgrades() > 0) {
            maxWidth = Math.max(maxWidth, g.getFontMetrics().stringWidth(tierString + getItemName() + " +" + getUpgrades()));
        } else {
            maxWidth = Math.max(maxWidth, g.getFontMetrics().stringWidth(tierString + getItemName()));
        }
        maxWidth = Math.max(maxWidth, g.getFontMetrics().stringWidth("Type: " + ITEM_TYPENAME.get(getItemType(itemCode))));
        maxWidth = Math.max(maxWidth, g.getFontMetrics().stringWidth("Level: " + (int) getTotalStats()[Globals.STAT_LEVEL]));

        if (getTotalStats()[Globals.STAT_POWER] > 0) {
            maxWidth = Math.max(maxWidth, g.getFontMetrics().stringWidth("Power: " + (int) getTotalStats()[Globals.STAT_POWER]));
        }
        if (getTotalStats()[Globals.STAT_DEFENSE] > 0) {
            maxWidth = Math.max(maxWidth, g.getFontMetrics().stringWidth("Defense: " + (int) getTotalStats()[Globals.STAT_DEFENSE]));
        }
        if (getTotalStats()[Globals.STAT_SPIRIT] > 0) {
            maxWidth = Math.max(maxWidth, g.getFontMetrics().stringWidth("Spirit: " + (int) getTotalStats()[Globals.STAT_SPIRIT]));
        }
        if (getTotalStats()[Globals.STAT_ARMOR] > 0) {
            maxWidth = Math.max(maxWidth, g.getFontMetrics().stringWidth("Armor: " + (int) getTotalStats()[Globals.STAT_ARMOR]));
        }
        if (getTotalStats()[Globals.STAT_REGEN] > 0) {
            maxWidth = Math.max(maxWidth, g.getFontMetrics().stringWidth("Regen: " + df.format(getTotalStats()[Globals.STAT_REGEN]) + " HP/Sec"));
        }
        if (getTotalStats()[Globals.STAT_CRITDMG] > 0) {
            maxWidth = Math.max(maxWidth, g.getFontMetrics().stringWidth("Critical Damage: " + df.format(getTotalStats()[Globals.STAT_CRITDMG] * 100) + "%"));
        }
        if (getTotalStats()[Globals.STAT_CRITCHANCE] > 0) {
            maxWidth = Math.max(maxWidth, g.getFontMetrics().stringWidth("Critical Chance: " + df.format(getTotalStats()[Globals.STAT_CRITCHANCE] * 100) + "%"));
        }

        g.setFont(Globals.ARIAL_15PT_ITALIC);
        if (ITEM_DESC.containsKey(itemCode)) {
            for (String line : ITEM_DESC.get(itemCode).split("\n")) {
                maxWidth = Math.max(maxWidth, g.getFontMetrics().stringWidth(line));
            }
        }

        boxWidth = maxWidth + 20;
        if (y + boxHeight > 720) {
            y = 700 - boxHeight;
        }

        if (x + 30 + boxWidth > 1280) {
            x = 1240 - boxWidth;
        }
        g.setColor(new Color(30, 30, 30, 185));
        g.fillRect(x + 30, y, boxWidth, boxHeight);
        g.setColor(Color.BLACK);
        g.drawRect(x + 30, y, boxWidth, boxHeight);
        g.drawRect(x + 31, y + 1, boxWidth - 2, boxHeight - 2);

        g.setFont(Globals.ARIAL_15PT);
        switch (getTier()) {
            case ItemEquip.TIER_COMMON:
                g.setColor(Color.WHITE);
                break;
            case ItemEquip.TIER_UNCOMMON:
                g.setColor(new Color(180, 0, 255));
                break;
            case ItemEquip.TIER_RARE:
                g.setColor(new Color(255, 225, 0));
                break;
            case ItemEquip.TIER_RUNIC:
                g.setColor(new Color(255, 130, 0));
                break;
            case ItemEquip.TIER_LEGENDARY:
                g.setColor(new Color(205, 15, 0));
                break;
            case ItemEquip.TIER_ARCHAIC:
                g.setColor(new Color(0, 220, 0));
                break;
            case ItemEquip.TIER_DIVINE:
                g.setColor(new Color(0, 255, 160));
                break;
        }
        if (getUpgrades() > 0) {
            g.drawString(tierString + getItemName() + " +" + getUpgrades(), x + 40, y + 20);
        } else {
            g.drawString(tierString + getItemName(), x + 40, y + 20);
        }
        g.setColor(Color.WHITE);
        int rowY = 40;

        g.drawString("Type: " + ITEM_TYPENAME.get(getItemType(itemCode)), x + 40, y + rowY);
        rowY += 20;

        g.drawString("Level: " + (int) getTotalStats()[Globals.STAT_LEVEL], x + 40, y + rowY);
        rowY += 20;
        if (getTotalStats()[Globals.STAT_POWER] > 0) {
            g.drawString("Power: " + (int) getTotalStats()[Globals.STAT_POWER], x + 40, y + rowY);
            rowY += 20;
        }
        if (getTotalStats()[Globals.STAT_DEFENSE] > 0) {
            g.drawString("Defense: " + (int) getTotalStats()[Globals.STAT_DEFENSE], x + 40, y + rowY);
            rowY += 20;
        }
        if (getTotalStats()[Globals.STAT_SPIRIT] > 0) {
            g.drawString("Spirit: " + (int) getTotalStats()[Globals.STAT_SPIRIT], x + 40, y + rowY);
            rowY += 20;
        }

        if (getTotalStats()[Globals.STAT_ARMOR] > 0) {
            g.drawString("Armor: " + (int) getTotalStats()[Globals.STAT_ARMOR], x + 40, y + rowY);
            rowY += 20;
        }
        if (getTotalStats()[Globals.STAT_REGEN] > 0) {
            g.drawString("Regen: " + df.format(getTotalStats()[Globals.STAT_REGEN]) + " HP/Sec", x + 40, y + rowY);
            rowY += 20;
        }
        if (getTotalStats()[Globals.STAT_CRITDMG] > 0) {
            g.drawString("Critical Damage: " + df.format(getTotalStats()[Globals.STAT_CRITDMG] * 100) + "%", x + 40, y + rowY);
            rowY += 20;
        }
        if (getTotalStats()[Globals.STAT_CRITCHANCE] > 0) {
            g.drawString("Critical Chance: " + df.format(getTotalStats()[Globals.STAT_CRITCHANCE] * 100) + "%", x + 40, y + rowY);
            rowY += 20;
        }

        g.setFont(Globals.ARIAL_15PT_ITALIC);
        if (ITEM_DESC.containsKey(itemCode)) {
            for (String line : ITEM_DESC.get(itemCode).split("\n")) {
                g.drawString(line, x + 40, y + rowY);
                rowY += 20;
            }
        }
    }

    private void drawMenu(Graphics2D g, int x, int y) {
        if (ITEM_ICONS.containsKey(itemCode)) {
            BufferedImage sprite = ITEM_ICONS.get(itemCode);
            if (sprite != null) {
                g.drawImage(sprite, x, y, null);
            } else {
                g.setFont(Globals.ARIAL_15PT);
                g.drawString("PH", x + 20, y + 30);
            }
        } else {
            loadItemIcon(itemCode);
        }

    }

    public void drawIngame(Graphics2D g, int x, int y, byte state, byte frame, byte facing) {
        if (ITEM_SPRITES.containsKey(Integer.toString(itemCode))) {
            BufferedImage sprite = ITEM_SPRITES.get(Integer.toString(itemCode))[state][frame];
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
        if (getItemType(itemCode) == Globals.ITEM_SHIELD) {
            drawIngame(g, x, y, state, frame, facing);
        } else if (getItemType(itemCode) == Globals.ITEM_SWORD) {
            if (ITEM_SPRITES.containsKey(itemCode + "_offhand")) {
                BufferedImage sprite = ITEM_SPRITES.get(itemCode + "_offhand")[state][frame];
                if (sprite != null) {
                    int sX = x + ((facing == Globals.RIGHT) ? 1 : -1) * ITEM_ORIGINPOINT.get(itemCode + "_offhand_" + state).x;
                    int sY = y + ITEM_ORIGINPOINT.get(itemCode + "_offhand_" + state).y;
                    int dX = sX + ((facing == Globals.RIGHT) ? 1 : -1) * sprite.getWidth();
                    int dY = sY + sprite.getHeight();
                    g.drawImage(sprite, sX, sY, dX, dY, 0, 0, sprite.getWidth(), sprite.getHeight(), null);
                }
            } else {
                ItemEquip.loadOffhandSprite(itemCode);
            }
        }

    }

    public int getUpgrades() {
        return upgrades;
    }

    private void update() {
        System.arraycopy(baseStats, 0, totalStats, 0, baseStats.length);
        totalStats[Globals.STAT_POWER] = Math.round(baseStats[Globals.STAT_POWER] * (1 + bonusMult + upgrades * UPGRADE_MULT));
        totalStats[Globals.STAT_DEFENSE] = Math.round(baseStats[Globals.STAT_DEFENSE] * (1 + bonusMult + upgrades * UPGRADE_MULT));
        totalStats[Globals.STAT_SPIRIT] = Math.round(baseStats[Globals.STAT_SPIRIT] * (1 + bonusMult + upgrades * UPGRADE_MULT));

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
        if (bonusMult + upgrades * UPGRADE_MULT >= 1.1) {
            tier = TIER_DIVINE;
        } else if (bonusMult + upgrades * UPGRADE_MULT >= 0.96) {
            tier = TIER_ARCHAIC;
        } else if (bonusMult + upgrades * UPGRADE_MULT >= 0.81) {
            tier = TIER_LEGENDARY;
        } else if (bonusMult + upgrades * UPGRADE_MULT >= 0.51) {
            tier = TIER_RUNIC;
        } else if (bonusMult + upgrades * UPGRADE_MULT >= 0.15) {
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

    public static byte getItemType(int i) {
        if (i >= 100000 && i <= 109999) { //Swords
            return Globals.ITEM_SWORD;
        } else if (i >= 110000 && i <= 119999) { //Shields
            return Globals.ITEM_SHIELD;
        } else if (i >= 120000 && i <= 129999) { //Bows
            return Globals.ITEM_BOW;
        } else if (i >= 130000 && i <= 199999) { //Quivers
            return Globals.ITEM_QUIVER;
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
