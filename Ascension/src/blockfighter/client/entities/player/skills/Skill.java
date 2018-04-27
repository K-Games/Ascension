package blockfighter.client.entities.player.skills;

import blockfighter.client.Core;
import blockfighter.client.entities.items.ItemEquip;
import blockfighter.shared.Globals;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public abstract class Skill {

    protected static final String HYPER_STANCE_DESC = "Grants Hyper Stance - Ignore knockback and stuns.";
    private FontMetrics fontMetric;
    private int boxWidth, boxHeight;

    protected byte level;
    protected long skillCastTime;

    protected String[] skillCurLevelDesc = new String[0];
    protected String[] skillNextLevelDesc = new String[0];
    protected String[] maxBonusDesc = new String[0];

    public void draw(final Graphics2D g, final int x, final int y) {
        draw(g, x, y, false);
    }

    public void draw(final Graphics2D g, final int x, final int y, final boolean disabled) {
        g.drawImage((disabled) ? getDisabledIcon() : getIcon(), x, y, null);
    }

    public void drawInfo(final Graphics2D g, final int x, final int y) {
        if (fontMetric == null) {
            fontMetric = g.getFontMetrics(Globals.ARIAL_15PT);
            updateInfoBoxSize();
        }

        g.setFont(Globals.ARIAL_15PT);
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
        g.drawImage(getIcon(), drawX + 10, drawY + 10, null);
        g.setColor(Color.WHITE);
        g.setFont(Globals.ARIAL_18PT);
        g.drawString(getSkillName(), drawX + 80, drawY + 30);
        g.setFont(Globals.ARIAL_15PT);
        if (getReqWeapon() != -1) {
            g.drawString("Level: " + this.level + " - Requires " + ItemEquip.getEquipTypeName(getReqWeapon()), drawX + 80, drawY + 50);
        } else {
            g.drawString("Level: " + this.level, drawX + 80, drawY + 50);
        }
        if (getMaxCooldown() > 0) {
            g.drawString("Cooldown: " + Globals.TIME_NUMBER_FORMAT.format(getMaxCooldown() / 1000D) + " Seconds", drawX + 80, drawY + 70);
        }

        int totalDescY = 0;
        for (String desc : getDesc()) {
            g.drawString(desc, drawX + 10, drawY + 90 + totalDescY);
            totalDescY += 20;
        }

        if (isPassive()) {
            g.setColor(new Color(255, 190, 0));
            g.drawString("Assign this passive to a hotkey to gain its effects.", drawX + 10, drawY + 90 + totalDescY);
            totalDescY += 20;
            g.setColor(Color.WHITE);
        }

        if (skillCurLevelDesc.length > 0) {
            totalDescY += 5;
            g.drawString("[Level " + this.level + "]", drawX + 10, drawY + 90 + totalDescY);
            totalDescY += 20;
            for (String descText : skillCurLevelDesc) {
                g.drawString(descText, drawX + 10, drawY + 90 + totalDescY);
                totalDescY += 20;
            }
        }

        if (this.level < 30 && skillNextLevelDesc.length > 0) {
            totalDescY += 5;
            g.drawString("[Level " + (this.level + 1) + "]", drawX + 10, drawY + 90 + totalDescY);
            totalDescY += 20;
            for (String descText : skillNextLevelDesc) {
                g.drawString(descText, drawX + 10, drawY + 90 + totalDescY);
                totalDescY += 20;
            }
        }

        if (!isPassive()) {
            totalDescY += 5;
            g.drawString("[Level 30 Bonus]", drawX + 10, drawY + 90 + totalDescY);
            totalDescY += 20;
            for (String descText : maxBonusDesc) {
                g.drawString(descText, drawX + 10, drawY + 90 + totalDescY);
                totalDescY += 20;
            }
            totalDescY += 5;
        }
    }

    public abstract void updateDesc();

    public abstract BufferedImage getIcon();

    public abstract BufferedImage getDisabledIcon();

    public abstract String[] getDesc();

    public abstract String getSkillName();

    public abstract byte getSkillCode();

    public abstract boolean isPassive();

    public abstract long getMaxCooldown();

    public abstract byte getReqWeapon();

    public abstract int getReqLevel();

    public abstract Double getCustomValue(String customHeader);

    public void updateInfoBoxSize() {
        if (fontMetric == null) {
            return;
        }
        int totalDescY = 0;
        totalDescY += getDesc().length * 20;

        if (isPassive()) {
            totalDescY += 20;
        }

        if (skillCurLevelDesc.length > 0) {
            totalDescY += 5;
            totalDescY += 20;
            totalDescY += skillCurLevelDesc.length * 20;
        }

        if (this.level < 30 && skillNextLevelDesc.length > 0) {
            totalDescY += 5;
            totalDescY += 20;
            totalDescY += skillNextLevelDesc.length * 20;
        }

        if (!isPassive()) {
            totalDescY += 5;
            totalDescY += 20;
            totalDescY += maxBonusDesc.length * 20;
        }

        boxHeight = 80 + totalDescY;

        boxWidth = fontMetric.stringWidth("Level: " + this.level + " - Requires " + ItemEquip.getEquipTypeName(getReqWeapon())) + 90;
        for (String s : getDesc()) {
            boxWidth = Math.max(boxWidth, fontMetric.stringWidth(s) + 20);
        }
        for (String s : maxBonusDesc) {
            boxWidth = Math.max(boxWidth, fontMetric.stringWidth(s) + 20);
        }
        for (String s : skillCurLevelDesc) {
            boxWidth = Math.max(boxWidth, fontMetric.stringWidth(s) + 20);
        }
        for (String s : skillNextLevelDesc) {
            boxWidth = Math.max(boxWidth, fontMetric.stringWidth(s) + 20);
        }
        if (isPassive()) {
            boxWidth = Math.max(boxWidth, fontMetric.stringWidth("Assign this passive to a hotkey to gain its effects.") + 20);
        }
    }

    public void resetCooldown() {
        this.skillCastTime = 0;
    }

    public void reduceCooldown(final int ms) {
        this.skillCastTime -= Globals.msToNs(ms);
    }

    public void setCooldown() {
        this.skillCastTime = Core.getLogicModule().getTime();
    }

    public long getCooldown() {
        long elapsed = Globals.nsToMs(Core.getLogicModule().getTime() - this.skillCastTime);
        long cd = getMaxCooldown() - elapsed;
        return (cd > 0) ? cd : 0;
    }

    public void setLevel(final byte lvl) {
        this.level = lvl;
        updateDesc();
        updateInfoBoxSize();
    }

    public byte getLevel() {
        return this.level;
    }

    public boolean addLevel(final byte amount) {
        if (this.level + amount <= 30) {
            this.level += amount;
            updateDesc();
            updateInfoBoxSize();
            return true;
        }
        return false;
    }

    public boolean isMaxed() {
        return this.level == 30;
    }

}
