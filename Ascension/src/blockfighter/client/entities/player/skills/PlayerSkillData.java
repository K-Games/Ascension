package blockfighter.client.entities.player.skills;

import blockfighter.client.Core;
import blockfighter.client.entities.items.ItemEquip;
import blockfighter.shared.Globals;
import blockfighter.shared.data.skill.SkillData;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

public class PlayerSkillData {

    private static final String HYPER_STANCE_DESC = "Grants Hyper Stance - Ignore knockback and stuns.";
    private static final String HYPER_STANCE_KEY = "%HYPERSTANCE";
    private static final String BASE_VALUE_KEY = "%BV",
            VALUE_EXPR_HEAD = "%D(", EXPR_END = ")^",
            DURATION_EXPR_HEAD = "%T(",
            REPLACE_KEY = "%REPLACE";

    private transient FontMetrics fontMetric;
    private transient int boxWidth, boxHeight;

    private SkillData skillData;
    private byte level;

    private transient byte skillCode;
    private transient long skillCastTime;

    private transient String[] skillCurLevelDesc = new String[0];
    private transient String[] skillNextLevelDesc = new String[0];
    private transient String[] maxBonusDesc = new String[0];

    public PlayerSkillData() {
    }

    public static double calculateSkillValue(final double base, final double multiplier, final double level, final double factor) {
        return (base + multiplier * level) * factor;
    }

    public String processDescLine(final String line, final double level) {
        String result = line.replaceAll(PlayerSkillData.HYPER_STANCE_KEY, PlayerSkillData.HYPER_STANCE_DESC);

        while (result.contains(VALUE_EXPR_HEAD)
                && result.indexOf(EXPR_END) > result.indexOf(VALUE_EXPR_HEAD)) {
            int exprStartIndex = result.indexOf(VALUE_EXPR_HEAD) + VALUE_EXPR_HEAD.length();
            int exprEndIndex = result.indexOf(EXPR_END);

            String exprData = result.substring(exprStartIndex, exprEndIndex);
            result = result.replace(result.subSequence(exprStartIndex - VALUE_EXPR_HEAD.length(), exprEndIndex + EXPR_END.length()), REPLACE_KEY);
            result = result.replace(REPLACE_KEY, Globals.NUMBER_FORMAT.format(evaluateSkillDesc(exprData, level)));
        }

        while (result.contains(DURATION_EXPR_HEAD)
                && result.indexOf(EXPR_END) > result.indexOf(DURATION_EXPR_HEAD)) {
            int exprStartIndex = result.indexOf(DURATION_EXPR_HEAD) + DURATION_EXPR_HEAD.length();
            int exprEndIndex = result.indexOf(EXPR_END);

            String exprData = result.substring(exprStartIndex, exprEndIndex);
            result = result.replace(result.subSequence(exprStartIndex - DURATION_EXPR_HEAD.length(), exprEndIndex + EXPR_END.length()), REPLACE_KEY);
            result = result.replace(REPLACE_KEY, Globals.TIME_NUMBER_FORMAT.format(evaluateSkillDesc(exprData, level)));
        }
        return result;
    }

    public double evaluateSkillDesc(final String exprData, final double level) {
        String[] data = exprData.split(",");
        String expr = data[0];
        String[] variables = new String[data.length - 1];
        for (byte i = 1; i < data.length; i++) {
            variables[i - 1] = data[i].trim();
        }

        Expression e = new ExpressionBuilder(expr)
                .variables(variables)
                .build();

        for (String variable : variables) {
            switch (variable.toLowerCase()) {
                case "lvl":
                    e.setVariable(variable, level);
                    break;
                case "basevalue":
                    e.setVariable(variable, skillData.getBaseValue());
                    break;
                case "multvalue":
                    e.setVariable(variable, skillData.getMultValue());
                    break;
                default:
                    e.setVariable(variable, skillData.getCustomValues().get(variable));
            }
        }
        return e.evaluate();
    }

    public void draw(final Graphics2D g, final int x, final int y) {
        draw(g, x, y, false);
    }

    public void draw(final Graphics2D g, final int x, final int y, final boolean disabled) {
        if (getIcon() != null) {
            g.drawImage((disabled) ? getDisabledIcon() : getIcon(), x, y, null);
        } else {
            g.drawString("PH", x + 25, y + 25);
        }
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
        g.drawString(skillData.getSkillName(), drawX + 80, drawY + 30);
        g.setFont(Globals.ARIAL_15PT);
        if (skillData.getReqWeapon() != null) {
            g.drawString("Level: " + this.level + " - Requires " + ItemEquip.getEquipTypeName(skillData.getReqWeapon()), drawX + 80, drawY + 50);
        } else {
            g.drawString("Level: " + this.level, drawX + 80, drawY + 50);
        }
        if (skillData.getMaxCooldown() > 0) {
            g.drawString("Cooldown: " + Globals.TIME_NUMBER_FORMAT.format(skillData.getMaxCooldown() / 1000D) + " Seconds", drawX + 80, drawY + 70);
        }

        int totalDescY = 0;
        for (String desc : skillData.getDesc()) {
            g.drawString(desc, drawX + 10, drawY + 90 + totalDescY);
            totalDescY += 20;
        }

        if (skillData.isPassive()) {
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

        if (!skillData.isPassive()) {
            totalDescY += 5;
            g.drawString("[Level 30 Bonus]", drawX + 10, drawY + 90 + totalDescY);
            totalDescY += 20;
            for (String descText : maxBonusDesc) {
                g.drawString(descText, drawX + 10, drawY + 90 + totalDescY);
                totalDescY += 20;
            }
        }
    }

    public final BufferedImage getIcon() {
        return Globals.SKILL_ICON[getSkillCode()];
    }

    public final BufferedImage getDisabledIcon() {
        return Globals.SKILL_DISABLED_ICON[getSkillCode()];
    }

    public final byte getSkillCode() {
        return skillCode;
    }

    public void updateDesc() {
        if (skillData.cantLevel()) {
            return;
        }

        double curLevelValue = calculateSkillValue(skillData.getBaseValue(), skillData.getMultValue(), this.level, 100);
        double nextLevelValue = calculateSkillValue(skillData.getBaseValue(), skillData.getMultValue(), this.level + 1, 100);

        if (skillData.getLevelDesc() != null) {
            this.skillCurLevelDesc = new String[skillData.getLevelDesc().size()];
            for (byte i = 0; i < skillData.getLevelDesc().size(); i++) {
                this.skillCurLevelDesc[i] = skillData.getLevelDesc().get(i).replaceAll(PlayerSkillData.BASE_VALUE_KEY, Globals.NUMBER_FORMAT.format(curLevelValue));
                this.skillCurLevelDesc[i] = processDescLine(this.skillCurLevelDesc[i], this.level);
            }

            this.skillNextLevelDesc = new String[skillData.getLevelDesc().size()];
            for (byte i = 0; i < skillData.getLevelDesc().size(); i++) {
                this.skillNextLevelDesc[i] = skillData.getLevelDesc().get(i).replaceAll(PlayerSkillData.BASE_VALUE_KEY, Globals.NUMBER_FORMAT.format(nextLevelValue));
                this.skillNextLevelDesc[i] = processDescLine(this.skillNextLevelDesc[i], this.level + 1);
            }
        } else {
            this.skillCurLevelDesc = new String[]{};
            this.skillNextLevelDesc = new String[]{};
        }

        if (skillData.getMaxBonusDesc() != null) {
            this.maxBonusDesc = new String[skillData.getMaxBonusDesc().size()];
            for (byte i = 0; i < skillData.getMaxBonusDesc().size(); i++) {
                this.maxBonusDesc[i] = skillData.getMaxBonusDesc().get(i);
                this.maxBonusDesc[i] = processDescLine(this.maxBonusDesc[i], this.level);
            }
        } else {
            this.maxBonusDesc = new String[]{};
        }
    }

    public void updateInfoBoxSize() {
        if (fontMetric == null) {
            return;
        }
        int totalDescY = 0;
        totalDescY += skillData.getDesc().size() * 20;

        if (skillData.isPassive()) {
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

        if (!skillData.isPassive()) {
            totalDescY += 5;
            totalDescY += 20;
            totalDescY += maxBonusDesc.length * 20;
        }

        boxHeight = 80 + totalDescY;
        if (skillData.getReqWeapon() != null) {
            boxWidth = fontMetric.stringWidth("Level: " + this.level + " - Requires " + ItemEquip.getEquipTypeName(skillData.getReqWeapon())) + 90;
        } else {
            boxWidth = fontMetric.stringWidth("Level: " + this.level) + 90;
        }

        for (String s : skillData.getDesc()) {
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
        if (skillData.isPassive()) {
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
        long cd = skillData.getMaxCooldown() - elapsed;
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

    public void setSkillCode(byte skillCode) {
        this.skillCode = skillCode;
        this.skillData = Globals.SkillClassMap.get(skillCode).getSkillData();
    }

    public SkillData getSkillData() {
        return skillData;
    }
}
