package blockfighter.client.entities.player.skills;

import blockfighter.client.AscensionClient;
import blockfighter.client.LogicModule;
import blockfighter.client.entities.items.ItemEquip;
import blockfighter.shared.Globals;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;

public abstract class Skill {

    protected static LogicModule logic;
    protected boolean isPassive = false;
    protected byte reqWeapon = -1;
    protected byte skillCode;
    protected byte level;
    protected long skillCastTime;
    protected int maxCooldown = 0;
    protected BufferedImage icon = Globals.MENU_BUTTON[Globals.BUTTON_SLOT];
    protected DecimalFormat df = new DecimalFormat("###,###,##0.##");
    protected DecimalFormat cddf = new DecimalFormat("0.#");
    protected String skillName = "NO_NAME";
    protected String[] description = new String[0];
    protected String[] skillCurLevelDesc = new String[0];
    protected String[] skillNextLevelDesc = new String[0];
    protected String[] maxBonusDesc = new String[0];

    public final static byte NUM_SKILLS = 30,
            SWORD_VORPAL = 0x00,
            SWORD_MULTI = 0x01,
            SWORD_PHANTOM = 0x01,
            SWORD_CINDER = 0x02,
            SWORD_GASH = 0x03,
            SWORD_SLASH = 0x04,
            SWORD_TAUNT = 0x05,
            BOW_ARC = 0x06,
            BOW_POWER = 0x07,
            BOW_RAPID = 0x08,
            BOW_FROST = 0x09,
            BOW_STORM = 0x0A,
            BOW_VOLLEY = 0x0B,
            SHIELD_FORTIFY = 0x0C,
            SHIELD_ROAR = 0x0D,
            SHIELD_CHARGE = 0x0E,
            SHIELD_REFLECT = 0x0F,
            SHIELD_TOSS = 0x10,
            SHIELD_MAGNETIZE = 0x10,
            SHIELD_DASH = 0x11,
            PASSIVE_DUALSWORD = 0x12,
            PASSIVE_KEENEYE = 0x13,
            PASSIVE_VITALHIT = 0x14,
            PASSIVE_SHIELDMASTERY = 0x15,
            PASSIVE_BARRIER = 0x16,
            PASSIVE_RESIST = 0x17,
            PASSIVE_BOWMASTERY = 0x18,
            PASSIVE_WILLPOWER = 0x19,
            PASSIVE_HARMONY = 0x1A,
            PASSIVE_REVIVE = 0x1B,
            PASSIVE_TOUGH = 0x1B,
            PASSIVE_SHADOWATTACK = 0x1C,
            PASSIVE_STATIC = 0x1D;

    public static void init() {
        logic = AscensionClient.getLogicModule();
    }

    public void draw(final Graphics2D g, final int x, final int y) {
        g.drawImage(this.icon, x, y, null);
    }

    public void drawInfo(final Graphics2D g, final int x, final int y) {
        updateDesc();
        final int boxHeight = ((this.level < 30) ? 130 : 105) + description.length * 20 + skillCurLevelDesc.length * 20 + ((this.level < 30) ? skillNextLevelDesc.length * 20 : 0) + ((isPassive) ? 20 : maxBonusDesc.length * 20 + 25);
        g.setFont(Globals.ARIAL_15PT);
        int boxWidth = g.getFontMetrics().stringWidth("Level: " + this.level + " - Requires " + ItemEquip.getItemTypeName(this.reqWeapon)) + 90;
        for (String s : description) {
            boxWidth = Math.max(boxWidth, g.getFontMetrics().stringWidth(s) + 20);
        }
        for (String s : maxBonusDesc) {
            boxWidth = Math.max(boxWidth, g.getFontMetrics().stringWidth(s) + 20);
        }
        for (String s : skillCurLevelDesc) {
            boxWidth = Math.max(boxWidth, g.getFontMetrics().stringWidth(s) + 20);
        }
        for (String s : skillNextLevelDesc) {
            boxWidth = Math.max(boxWidth, g.getFontMetrics().stringWidth(s) + 20);
        }
        if (isPassive) {
            boxWidth = Math.max(boxWidth, g.getFontMetrics().stringWidth("Assign this passive to a hotkey to gain its effects.") + 20);
        }

        int drawX = x, drawY = y;
        if (drawY + boxHeight > 700) {
            drawY = 700 - boxHeight;
        }

        if (drawX + 30 + boxWidth > 1240) {
            drawX = 1240 - boxWidth;
        }
        g.setColor(new Color(30, 30, 30, 185));
        g.fillRect(drawX, drawY, boxWidth, boxHeight);
        g.setColor(Color.BLACK);
        g.drawRect(drawX, drawY, boxWidth, boxHeight);
        g.drawRect(drawX + 1, drawY + 1, boxWidth - 2, boxHeight - 2);
        g.drawImage(this.icon, drawX + 10, drawY + 10, null);
        g.setColor(Color.WHITE);
        g.setFont(Globals.ARIAL_18PT);
        g.drawString(getSkillName(), drawX + 80, drawY + 30);
        g.setFont(Globals.ARIAL_15PT);
        if (this.reqWeapon != -1) {
            g.drawString("Level: " + this.level + " - Requires " + ItemEquip.getItemTypeName(this.reqWeapon), drawX + 80, drawY + 50);
        } else {
            g.drawString("Level: " + this.level, drawX + 80, drawY + 50);
        }
        if (this.maxCooldown > 0) {
            g.drawString("Cooldown: " + cddf.format(getMaxCooldown() / 1000D) + " Seconds", drawX + 80, drawY + 70);
        }

        int totalDescY = 0;
        for (int i = 0; i < description.length; i++) {
            g.drawString(description[i], drawX + 10, drawY + 90 + i * 20);
        }
        totalDescY += description.length * 20;

        if (isPassive) {
            g.setColor(new Color(255, 190, 0));
            g.drawString("Assign this passive to a hotkey to gain its effects.", drawX + 10, drawY + 90 + totalDescY);
            totalDescY += 20;
            g.setColor(Color.WHITE);
        }

        g.drawString("[Level " + this.level + "]", drawX + 10, drawY + 95 + totalDescY);
        for (int i = 0; i < skillCurLevelDesc.length; i++) {
            g.drawString(skillCurLevelDesc[i], drawX + 10, drawY + 115 + totalDescY + i * 20);
        }
        totalDescY += skillCurLevelDesc.length * 20;

        if (this.level < 30) {
            g.drawString("[Level " + (this.level + 1) + "]", drawX + 10, drawY + 120 + totalDescY);
            for (int i = 0; i < skillNextLevelDesc.length; i++) {
                g.drawString(skillNextLevelDesc[i], drawX + 10, drawY + 140 + totalDescY + i * 20);
            }
            totalDescY += skillNextLevelDesc.length * 20;
        }

        if (!isPassive) {
            if (this.level < 30) {
                g.drawString("[Level 30 Bonus]", drawX + 10, drawY + 145 + totalDescY);
                for (int i = 0; i < maxBonusDesc.length; i++) {
                    g.drawString(maxBonusDesc[i], drawX + 10, drawY + 165 + totalDescY + i * 20);
                }
            } else {
                g.drawString("[Level 30 Bonus]", drawX + 10, drawY + 120 + totalDescY);
                for (int i = 0; i < maxBonusDesc.length; i++) {
                    g.drawString(maxBonusDesc[i], drawX + 10, drawY + 140 + totalDescY + i * 20);
                }
            }
        }
    }

    public abstract void updateDesc();

    public String getSkillName() {
        return this.skillName;
    }

    public byte getSkillCode() {
        return this.skillCode;
    }

    public void resetCooldown() {
        this.skillCastTime = 0;
    }

    public void reduceCooldown(final int ms) {
        this.skillCastTime -= Globals.msToNs(ms);
    }

    public void setCooldown() {
        this.skillCastTime = logic.getTime();
    }

    public long getCooldown() {
        long elapsed = Globals.nsToMs(logic.getTime() - this.skillCastTime);
        long cd = this.maxCooldown - elapsed;
        return (cd > 0) ? cd : 0;
    }

    public void setLevel(final byte lvl) {
        this.level = lvl;
    }

    public byte getLevel() {
        return this.level;
    }

    public boolean addLevel(final byte amount) {
        if (this.level + amount <= 30) {
            this.level += amount;
            return true;
        }
        return false;
    }

    public boolean isMaxed() {
        return this.level == 30;
    }

    public double getMaxCooldown() {
        return this.maxCooldown;
    }
}
