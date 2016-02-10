package blockfighter.client.entities.items;

import blockfighter.client.Globals;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang3.StringUtils;
import static blockfighter.client.Globals.NUM_PLAYER_ANIM_STATE;
import static blockfighter.client.Globals.PLAYER_ANIM_STATE_STAND;
import static blockfighter.client.Globals.PLAYER_ANIM_STATE_WALK;
import static blockfighter.client.Globals.PLAYER_ANIM_STATE_JUMP;
import static blockfighter.client.Globals.PLAYER_ANIM_STATE_ATTACK;
import static blockfighter.client.Globals.PLAYER_ANIM_STATE_ATTACKBOW;
import static blockfighter.client.Globals.PLAYER_ANIM_STATE_DEAD;
import static blockfighter.client.Globals.PLAYER_ANIM_STATE_BUFF;

/**
 *
 * @author Ken Kwan
 */
public class ItemEquip implements Item {

    private static DecimalFormat df = new DecimalFormat("###,###,##0.##");

    public final static double UPGRADE_CRITCHANCE = 0.002, // 0.2%
            UPGRADE_CRITDMG = 0.02, // 2%
            UPGRADE_REGEN = 8,
            UPGRADE_ARMOR = 24,
            UPGRADE_MULT = 0.04;

    public final static HashMap<Integer, Integer> ITEM_CODES = new HashMap<>();

    private final static HashMap<Byte, String> ITEM_TYPENAME = new HashMap<>(13);
    private final static HashMap<Integer, String> ITEM_NAMES;
    private final static HashMap<Integer, BufferedImage> ITEM_ICONS;
    private final static HashMap<String, BufferedImage[][]> ITEM_SPRITES = new HashMap<>();
    private final static HashMap<Integer, String> ITEM_DESC;
    private final static HashMap<String, Point> ITEM_ORIGINPOINT;

    public final static byte TIER_COMMON = 0,
            TIER_UNCOMMON = 1,
            TIER_RARE = 2, // .15(15%)-.5(50%) bonus
            TIER_RUNIC = 3, // .51-.8
            TIER_LEGENDARY = 4, // .81-.95
            TIER_ARCHAIC = 5, // .96-1.1
            TIER_DIVINE = 6; // 1.1+

    protected double[] baseStats = new double[Globals.NUM_STATS],
            totalStats = new double[Globals.NUM_STATS];
    protected int upgrades;
    protected double bonusMult;
    protected byte tier = TIER_COMMON;
    protected int itemCode;

    static {
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

        loadItemCodes();

        ITEM_NAMES = new HashMap<>(ITEM_CODES.size());
        ITEM_ICONS = new HashMap<>(ITEM_CODES.size());
        ITEM_DESC = new HashMap<>(ITEM_CODES.size());
        loadItemDetails();

        ITEM_ORIGINPOINT = new HashMap<>(ITEM_CODES.size() * Globals.NUM_PLAYER_ANIM_STATE);
        loadItemDrawOrigin();
    }

    public static void init() {
    }

    private static void loadItemCodes() {
        try {
            InputStream itemFile = Globals.class.getResourceAsStream("itemdata/equip/itemcodes.txt");
            LineIterator it = IOUtils.lineIterator(itemFile, "UTF-8");
            try {
                while (it.hasNext()) {
                    String line = it.nextLine();
                    try {
                        int itemcode = Integer.parseInt(line);
                        ITEM_CODES.put(itemcode, itemcode);
                    } catch (NumberFormatException e) {
                    }
                }
            } finally {
                LineIterator.closeQuietly(it);
            }
        } catch (IOException e) {
            System.err.println("Could not load item codes from data");
            System.exit(1);
        }
    }

    private static void loadItemDetails() {
        System.out.println("Loading Item Data...");
        System.out.print("[");
        for (final Map.Entry<Integer, Integer> itemEntry : ITEM_CODES.entrySet()) {
            final int itemCode = itemEntry.getValue();
            try {
                InputStream itemFile = Globals.class.getResourceAsStream("itemdata/equip/" + itemCode + ".txt");
                LineIterator it = IOUtils.lineIterator(itemFile, "UTF-8");
                try {
                    System.out.print(itemCode + ",");
                    if (it.hasNext()) {
                        final String name = it.nextLine();
                        ITEM_NAMES.put(itemCode, name);

                    }
                    if (it.hasNext()) {
                        final String desc = it.nextLine();
                        ITEM_DESC.put(itemCode, desc);

                    }
                } finally {
                    LineIterator.closeQuietly(it);
                }
            } catch (IOException | NullPointerException e) {
                System.err.println("Could not load item #" + itemCode + " details.");
            }
        }
        System.out.println("]");
    }

    private static void loadItemDrawOrigin() {
        //Standard for naming item offset
        //Key = CODE_STATECODE, or CODE_offhand_STATECODE
    }

    public static void unloadSprites() {
        ITEM_SPRITES.clear();
    }

    public static void loadItemIcon(final int code) {
        BufferedImage icon = null;
        try {
            icon = ImageIO.read(Globals.class.getResourceAsStream("sprites/equip/" + code + "/icon.png"));
        } catch (final Exception ex) {
        }
        ITEM_ICONS.put(code, icon);
    }

    public static void loadItemSprite(final int code) {
        final BufferedImage[][] load = new BufferedImage[NUM_PLAYER_ANIM_STATE][];

        load[PLAYER_ANIM_STATE_ATTACK] = new BufferedImage[Globals.ATTACK_FRAMES];
        for (int i = 0; i < load[PLAYER_ANIM_STATE_ATTACK].length; i++) {
            try {
                load[PLAYER_ANIM_STATE_ATTACK][i] = ImageIO
                        .read(Globals.class.getResourceAsStream("sprites/equip/" + code + "/attack/mainhand1/" + i + ".png"));
            } catch (final Exception ex) {
            }
        }

        load[PLAYER_ANIM_STATE_ATTACKBOW] = new BufferedImage[Globals.ATTACKBOW_FRAMES];
        for (int i = 0; i < load[PLAYER_ANIM_STATE_ATTACKBOW].length; i++) {
            try {
                load[PLAYER_ANIM_STATE_ATTACKBOW][i] = ImageIO
                        .read(Globals.class.getResourceAsStream("sprites/equip/" + code + "/attack/bow/" + i + ".png"));
            } catch (final Exception ex) {
            }
        }

        load[PLAYER_ANIM_STATE_STAND] = new BufferedImage[Globals.STAND_FRAMES];
        for (int i = 0; i < load[PLAYER_ANIM_STATE_STAND].length; i++) {
            try {
                load[PLAYER_ANIM_STATE_STAND][i] = ImageIO
                        .read(Globals.class.getResourceAsStream("sprites/equip/" + code + "/stand/" + i + ".png"));
            } catch (final Exception ex) {
            }
        }

        load[PLAYER_ANIM_STATE_WALK] = new BufferedImage[Globals.WALK_FRAMES];
        for (int i = 0; i < load[PLAYER_ANIM_STATE_WALK].length; i++) {
            try {
                load[PLAYER_ANIM_STATE_WALK][i] = ImageIO
                        .read(Globals.class.getResourceAsStream("sprites/equip/" + code + "/walk/" + i + ".png"));
            } catch (final Exception ex) {
            }
        }

        load[PLAYER_ANIM_STATE_JUMP] = new BufferedImage[Globals.JUMP_FRAMES];
        try {
            load[PLAYER_ANIM_STATE_JUMP][0] = ImageIO.read(Globals.class.getResourceAsStream("sprites/equip/" + code + "/jump/0.png"));
        } catch (final Exception ex) {
        }

        load[PLAYER_ANIM_STATE_BUFF] = new BufferedImage[Globals.BUFF_FRAMES];
        for (int i = 0; i < load[PLAYER_ANIM_STATE_BUFF].length; i++) {
            try {
                load[PLAYER_ANIM_STATE_BUFF][i] = ImageIO
                        .read(Globals.class.getResourceAsStream("sprites/equip/" + code + "/buff/" + i + ".png"));
            } catch (final Exception ex) {
            }
        }

        load[PLAYER_ANIM_STATE_DEAD] = new BufferedImage[Globals.DEAD_FRAMES];
        for (int i = 0; i < load[PLAYER_ANIM_STATE_DEAD].length; i++) {
            try {
                load[PLAYER_ANIM_STATE_DEAD][i] = ImageIO
                        .read(Globals.class.getResourceAsStream("sprites/equip/" + code + "/dead/" + i + ".png"));
            } catch (final Exception ex) {
            }
        }
        ITEM_SPRITES.put(Integer.toString(code), load);
    }

    public static void loadOffhandSprite(final int code) {
        final BufferedImage[][] load = new BufferedImage[NUM_PLAYER_ANIM_STATE][];

        load[PLAYER_ANIM_STATE_ATTACK] = new BufferedImage[Globals.ATTACK_FRAMES];
        for (int i = 0; i < load[PLAYER_ANIM_STATE_ATTACK].length; i++) {
            try {
                load[PLAYER_ANIM_STATE_ATTACK][i] = ImageIO.read(
                        Globals.class.getResourceAsStream("sprites/equip/" + code + "/offhand/attack/mainhand1/" + i + ".png"));
            } catch (final Exception ex) {
            }
        }

        load[PLAYER_ANIM_STATE_ATTACKBOW] = new BufferedImage[Globals.ATTACKBOW_FRAMES];
        for (int i = 0; i < load[PLAYER_ANIM_STATE_ATTACKBOW].length; i++) {
            try {
                load[PLAYER_ANIM_STATE_ATTACKBOW][i] = ImageIO
                        .read(Globals.class.getResourceAsStream("sprites/equip/" + code + "/offhand/attack/bow/" + i + ".png"));
            } catch (final Exception ex) {
            }
        }

        load[PLAYER_ANIM_STATE_STAND] = new BufferedImage[Globals.STAND_FRAMES];
        for (int i = 0; i < load[PLAYER_ANIM_STATE_STAND].length; i++) {
            try {
                load[PLAYER_ANIM_STATE_STAND][i] = ImageIO
                        .read(Globals.class.getResourceAsStream("sprites/equip/" + code + "/offhand/stand/" + i + ".png"));
            } catch (final Exception ex) {
            }
        }

        load[PLAYER_ANIM_STATE_WALK] = new BufferedImage[Globals.WALK_FRAMES];
        for (int i = 0; i < load[PLAYER_ANIM_STATE_WALK].length; i++) {
            try {
                load[PLAYER_ANIM_STATE_WALK][i] = ImageIO
                        .read(Globals.class.getResourceAsStream("sprites/equip/" + code + "/offhand/walk/" + i + ".png"));
            } catch (final Exception ex) {
            }
        }

        load[PLAYER_ANIM_STATE_JUMP] = new BufferedImage[Globals.JUMP_FRAMES];
        try {
            load[PLAYER_ANIM_STATE_JUMP][0] = ImageIO
                    .read(Globals.class.getResourceAsStream("sprites/equip/" + code + "/offhand/jump/0.png"));
        } catch (final Exception ex) {
        }

        load[PLAYER_ANIM_STATE_BUFF] = new BufferedImage[Globals.BUFF_FRAMES];
        for (int i = 0; i < load[PLAYER_ANIM_STATE_BUFF].length; i++) {
            try {
                load[PLAYER_ANIM_STATE_BUFF][i] = ImageIO
                        .read(Globals.class.getResourceAsStream("sprites/equip/" + code + "/offhand/buff/" + i + ".png"));
            } catch (final Exception ex) {
            }
        }

        load[PLAYER_ANIM_STATE_DEAD] = new BufferedImage[Globals.DEAD_FRAMES];
        for (int i = 0; i < load[PLAYER_ANIM_STATE_DEAD].length; i++) {
            try {
                load[PLAYER_ANIM_STATE_DEAD][i] = ImageIO
                        .read(Globals.class.getResourceAsStream("sprites/equip/" + code + "/offhand/dead/" + i + ".png"));
            } catch (final Exception ex) {
            }
        }
        ITEM_SPRITES.put(code + "_offhand", load);
    }

    public double[] getTotalStats() {
        return this.totalStats;
    }

    public double[] getBaseStats() {
        return this.baseStats;
    }

    public static String getItemTypeName(final byte itemType) {
        return ITEM_TYPENAME.get(itemType);
    }

    public ItemEquip(final int ic) {
        this.itemCode = ic;
    }

    public ItemEquip(final int ic, final double level) {
        this(ic, level, false);
    }

    public ItemEquip(final int ic, final double level, final boolean legendary) {
        this.itemCode = ic;
        this.baseStats = newEquipStat(ic, level);
        if (legendary) {
            this.bonusMult = (Globals.rng(10) + 90) / 100D;
        } else {
            this.bonusMult = Globals.rng(90) / 100D;
        }
        this.upgrades = 0;
        update();
    }

    public static double[] newEquipStat(final int ic, final double level) {
        final double[] newStats = new double[Globals.NUM_STATS];
        newStats[Globals.STAT_LEVEL] = level;

        switch (getItemType(ic)) {
            case Globals.ITEM_WEAPON:
                newStats[Globals.STAT_POWER] = level + 0.25D * level + Globals.rng(6);
                break;
            case Globals.ITEM_BOW:
                newStats[Globals.STAT_POWER] = level + 0.25D * level + Globals.rng(6);
                break;
            case Globals.ITEM_SHIELD:
                newStats[Globals.STAT_DEFENSE] = level + 0.25D * level + Globals.rng(6);
                break;
            case Globals.ITEM_QUIVER:
                newStats[Globals.STAT_POWER] = level + 0.25D * level + Globals.rng(6);
                newStats[Globals.STAT_CRITCHANCE] = level * 0.001 + Globals.rng(11) * 0.001;
                break;
            case Globals.ITEM_CHEST:
                newStats[Globals.STAT_DEFENSE] = level + 0.25D * level + Globals.rng(6);
                break;
            case Globals.ITEM_PANTS:
                newStats[Globals.STAT_DEFENSE] = level + 0.25D * level + Globals.rng(6);
                break;
            case Globals.ITEM_HEAD:
                newStats[Globals.STAT_DEFENSE] = level + 0.25D * level + Globals.rng(6);
                newStats[Globals.STAT_SPIRIT] = level + 0.25D * level + Globals.rng(6);
                break;
            case Globals.ITEM_SHOE:
                newStats[Globals.STAT_SPIRIT] = level + 0.25D * level + Globals.rng(6);
                break;
            case Globals.ITEM_BELT:
                newStats[Globals.STAT_SPIRIT] = level + 0.25D * level + Globals.rng(6);
                break;
            case Globals.ITEM_SHOULDER:
                newStats[Globals.STAT_DEFENSE] = level + 0.25D * level + Globals.rng(6);
                newStats[Globals.STAT_POWER] = level + 0.25D * level + Globals.rng(6);
                break;
            case Globals.ITEM_GLOVE:
                newStats[Globals.STAT_SPIRIT] = level + 0.25D * level + Globals.rng(6);
                newStats[Globals.STAT_POWER] = level + 0.25D * level + Globals.rng(6);
                break;
            case Globals.ITEM_AMULET:
                newStats[Globals.STAT_CRITDMG] = level * 0.02 + Globals.rng(20) * 0.01;
                newStats[Globals.STAT_REGEN] = level * 5 + Globals.rng(10) * 5;
                break;
            case Globals.ITEM_RING:
                newStats[Globals.STAT_CRITCHANCE] = level * 0.001 + Globals.rng(11) * 0.001;
                newStats[Globals.STAT_ARMOR] = level * 18 + Globals.rng(10) * 18;
                break;
        }
        newStats[Globals.STAT_POWER] = Math.round(newStats[Globals.STAT_POWER]);
        newStats[Globals.STAT_DEFENSE] = Math.round(newStats[Globals.STAT_DEFENSE]);
        newStats[Globals.STAT_SPIRIT] = Math.round(newStats[Globals.STAT_SPIRIT]);
        return newStats;
    }

    public ItemEquip(final double[] bs, final int u, final double mult, final int ic) {
        this.itemCode = ic;
        this.baseStats = bs;
        this.upgrades = u;
        this.bonusMult = mult;
        update();
    }

    @Override
    public void draw(final Graphics2D g, final int x, final int y) {
        drawIcon(g, x, y);
    }

    public void drawInfo(final Graphics2D g, final Rectangle2D.Double box) {
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
        if (ITEM_DESC.containsKey(this.itemCode)) {
            final int lines = StringUtils.countMatches(ITEM_DESC.get(this.itemCode), "\n") + 1;
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
        maxWidth = Math.max(maxWidth, g.getFontMetrics().stringWidth("Type: " + ITEM_TYPENAME.get(getItemType(this.itemCode))));
        maxWidth = Math.max(maxWidth, g.getFontMetrics().stringWidth("Level: " + (int) getTotalStats()[Globals.STAT_LEVEL]));

        for (byte i = 0; i < getTotalStats().length; i++) {
            if (getTotalStats()[i] > 0) {
                switch (i) {
                    case Globals.STAT_CRITCHANCE:
                    case Globals.STAT_CRITDMG:
                        maxWidth = Math.max(maxWidth,
                                g.getFontMetrics().stringWidth(Globals.getStatName(i) + ": " + df.format(getTotalStats()[i] * 100) + "%"));
                        break;

                    case Globals.STAT_REGEN:
                        maxWidth = Math.max(maxWidth,
                                g.getFontMetrics().stringWidth(Globals.getStatName(i) + ": " + df.format(getTotalStats()[i])));
                        break;
                    default:
                        maxWidth = Math.max(maxWidth,
                                g.getFontMetrics().stringWidth(Globals.getStatName(i) + ": " + (int) getTotalStats()[i]));
                }

            }
        }

        g.setFont(Globals.ARIAL_15PTITALIC);
        if (ITEM_DESC.containsKey(this.itemCode)) {
            for (final String line : ITEM_DESC.get(this.itemCode).split("\n")) {
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

        g.drawString("Type: " + ITEM_TYPENAME.get(getItemType(this.itemCode)), x + 40, y + rowY);
        rowY += 20;
        g.drawString(Globals.getStatName(Globals.STAT_LEVEL) + ": " + (int) getTotalStats()[Globals.STAT_LEVEL], x + 40, y + rowY);

        rowY += 20;
        for (byte i = 0; i < getTotalStats().length; i++) {
            if (getTotalStats()[i] > 0 && i != Globals.STAT_LEVEL) {
                switch (i) {
                    case Globals.STAT_CRITCHANCE:
                    case Globals.STAT_CRITDMG:
                        g.drawString(Globals.getStatName(i) + ": " + df.format(getTotalStats()[i] * 100) + "%", x + 40, y + rowY);
                        break;
                    case Globals.STAT_REGEN:
                        g.drawString(Globals.getStatName(i) + ": " + df.format(getTotalStats()[i]), x + 40, y + rowY);
                        break;
                    default:
                        g.drawString(Globals.getStatName(i) + ": " + (int) getTotalStats()[i], x + 40, y + rowY);
                }
                rowY += 20;
            }
        }

        g.setFont(Globals.ARIAL_15PTITALIC);
        if (ITEM_DESC.containsKey(this.itemCode)) {
            for (final String line : ITEM_DESC.get(this.itemCode).split("\n")) {
                g.drawString(line, x + 40, y + rowY);
                rowY += 20;
            }
        }
    }

    private void drawIcon(final Graphics2D g, final int x, final int y) {
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

    public void drawIngame(final Graphics2D g, final int x, final int y, final byte state, final byte frame, final byte facing) {
        if (!isValidItem(this.itemCode)) {
            return;
        }
        if (ITEM_SPRITES.containsKey(Integer.toString(this.itemCode))) {
            BufferedImage sprite = ITEM_SPRITES.get(Integer.toString(this.itemCode))[state][frame];
            if (sprite != null) {
                int sX = x + ((facing == Globals.RIGHT) ? 1 : -1) * ITEM_ORIGINPOINT.get(this.itemCode + "_" + state).x;
                int sY = y + ITEM_ORIGINPOINT.get(this.itemCode + "_" + state).y;
                int dX = sX + ((facing == Globals.RIGHT) ? 1 : -1) * sprite.getWidth();
                int dY = sY + sprite.getHeight();
                g.drawImage(sprite, sX, sY, dX, dY, 0, 0, sprite.getWidth(), sprite.getHeight(), null);
            }
        } else {
            ItemEquip.loadItemSprite(this.itemCode);
        }

    }

    public void drawIngame(final Graphics2D g, final int x, final int y, final byte state, final byte frame, final byte facing,
            final boolean offhand) {
        if (!isValidItem(this.itemCode)) {
            return;
        }
        if (getItemType(this.itemCode) == Globals.ITEM_SHIELD) {
            drawIngame(g, x, y, state, frame, facing);
        } else if (getItemType(this.itemCode) == Globals.ITEM_SWORD) {
            if (ITEM_SPRITES.containsKey(this.itemCode + "_offhand")) {
                BufferedImage sprite = ITEM_SPRITES.get(this.itemCode + "_offhand")[state][frame];
                if (sprite != null) {
                    int sX = x + ((facing == Globals.RIGHT) ? 1 : -1) * ITEM_ORIGINPOINT.get(this.itemCode + "_offhand_" + state).x;
                    int sY = y + ITEM_ORIGINPOINT.get(this.itemCode + "_offhand_" + state).y;
                    int dX = sX + ((facing == Globals.RIGHT) ? 1 : -1) * sprite.getWidth();
                    int dY = sY + sprite.getHeight();
                    g.drawImage(sprite, sX, sY, dX, dY, 0, 0, sprite.getWidth(), sprite.getHeight(), null);
                }
            } else {
                ItemEquip.loadOffhandSprite(this.itemCode);
            }
        }

    }

    public int getUpgrades() {
        return this.upgrades;
    }

    private void update() {
        System.arraycopy(this.baseStats, 0, this.totalStats, 0, this.baseStats.length);
        this.totalStats[Globals.STAT_POWER] = Math
                .round(this.baseStats[Globals.STAT_POWER] * (1 + this.bonusMult + this.upgrades * UPGRADE_MULT));
        this.totalStats[Globals.STAT_DEFENSE] = Math
                .round(this.baseStats[Globals.STAT_DEFENSE] * (1 + this.bonusMult + this.upgrades * UPGRADE_MULT));
        this.totalStats[Globals.STAT_SPIRIT] = Math
                .round(this.baseStats[Globals.STAT_SPIRIT] * (1 + this.bonusMult + this.upgrades * UPGRADE_MULT));

        if (this.baseStats[Globals.STAT_CRITCHANCE] > 0) {
            this.totalStats[Globals.STAT_CRITCHANCE] = this.baseStats[Globals.STAT_CRITCHANCE] * (1 + this.bonusMult / 3) + this.upgrades * UPGRADE_CRITCHANCE;
        }
        if (this.baseStats[Globals.STAT_CRITDMG] > 0) {
            this.totalStats[Globals.STAT_CRITDMG] = this.baseStats[Globals.STAT_CRITDMG] * (1 + this.bonusMult / 4)
                    + this.upgrades * UPGRADE_CRITDMG;
        }
        if (this.baseStats[Globals.STAT_ARMOR] > 0) {
            this.totalStats[Globals.STAT_ARMOR] = Math
                    .round(this.baseStats[Globals.STAT_ARMOR] * (1 + this.bonusMult / 2) + this.upgrades * UPGRADE_ARMOR);
        }
        if (this.baseStats[Globals.STAT_REGEN] > 0) {
            this.totalStats[Globals.STAT_REGEN] = Math
                    .round(10D * (this.baseStats[Globals.STAT_REGEN] * (1 + this.bonusMult / 2) + this.upgrades * UPGRADE_REGEN)) / 10D;
        }
        if (this.bonusMult + this.upgrades * UPGRADE_MULT >= 1.1) {
            this.tier = TIER_DIVINE;
        } else if (this.bonusMult + this.upgrades * UPGRADE_MULT >= .95) {
            this.tier = TIER_ARCHAIC;
        } else if (this.bonusMult + this.upgrades * UPGRADE_MULT >= 0.9) {
            this.tier = TIER_LEGENDARY;
        } else if (this.bonusMult + this.upgrades * UPGRADE_MULT >= 0.85) {
            this.tier = TIER_RUNIC;
        } else if (this.bonusMult + this.upgrades * UPGRADE_MULT >= 0.7) {
            this.tier = TIER_RARE;
        } else if (this.bonusMult + this.upgrades * UPGRADE_MULT >= 0.5) {
            this.tier = TIER_UNCOMMON;
        } else {
            this.tier = TIER_COMMON;
        }
    }

    @Override
    public int getItemCode() {
        return this.itemCode;
    }

    public static boolean isValidItem(final int i) {
        return ITEM_CODES.containsKey(i);
    }

    public double getBonusMult() {
        return this.bonusMult;
    }

    public byte getTier() {
        return this.tier;
    }

    @Override
    public String getItemName() {
        if (!ITEM_NAMES.containsKey(this.itemCode)) {
            return "NO NAME";
        }
        return ITEM_NAMES.get(this.itemCode);
    }

    public void addUpgrade(final int amount) {
        this.upgrades += amount;
        update();
    }

    public static byte getItemType(final int i) {
        if (i >= 100000 && i <= 109999) { // Swords
            return Globals.ITEM_SWORD;
        } else if (i >= 110000 && i <= 119999) { // Shields
            return Globals.ITEM_SHIELD;
        } else if (i >= 120000 && i <= 129999) { // Bows
            return Globals.ITEM_BOW;
        } else if (i >= 130000 && i <= 199999) { // Quivers
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
