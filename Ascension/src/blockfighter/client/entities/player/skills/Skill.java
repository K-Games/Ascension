package blockfighter.client.entities.player.skills;

import blockfighter.client.Core;
import blockfighter.client.entities.items.ItemEquip;
import blockfighter.shared.Globals;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

public abstract class Skill {

    protected static final String HYPER_STANCE_DESC = "Grants Hyper Stance - Ignore knockback and stuns.";
    protected static final String HYPER_STANCE_KEY = "%HYPERSTANCE";
    protected static final String BASE_VALUE_KEY = "%BV",
            VALUE_EXPR_HEAD = "%D(", EXPR_END = ")^",
            DURATION_EXPR_HEAD = "%T(",
            REPLACE_KEY = "%REPLACE";

    private FontMetrics fontMetric;
    private int boxWidth, boxHeight;

    protected byte level;
    protected long skillCastTime;

    protected String[] skillCurLevelDesc = new String[0];
    protected String[] skillNextLevelDesc = new String[0];
    protected String[] maxBonusDesc = new String[0];

    public static double calculateSkillValue(final double base, final double multiplier, final double level, final double factor) {
        return (base + multiplier * level) * factor;
    }

    public String processDescLine(final String line, final double level) {
        String result = line.replaceAll(Skill.HYPER_STANCE_KEY, Skill.HYPER_STANCE_DESC);

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
                    e.setVariable(variable, getStaticFieldValue("BASE_VALUE", Double.class));
                    break;
                case "multvalue":
                    e.setVariable(variable, getStaticFieldValue("MULT_VALUE", Double.class));
                    break;
                default:
                    e.setVariable(variable, getCustomValue("[" + variable + "]"));
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

    public final <T> T getStaticFieldValue(String fieldName, Class<T> fieldType) {
        try {
            return fieldType.cast(this.getClass().getDeclaredField(fieldName).get(null));
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex) {
            Globals.logError("Could not find static field: " + fieldName + " in " + this.getClass().getSimpleName(), ex);
        }
        return null;
    }

    public final Double getCustomValue(String customHeader) {
        HashMap customValues = getStaticFieldValue("CUSTOM_VALUES", HashMap.class);
        return (Double) customValues.get(customHeader);
    }

    public final String[] getDesc() {
        return getStaticFieldValue("DESCRIPTION", String[].class);
    }

    public final BufferedImage getIcon() {
        return Globals.SKILL_ICON[getSkillCode()];
    }

    public final BufferedImage getDisabledIcon() {
        return Globals.SKILL_DISABLED_ICON[getSkillCode()];
    }

    public long getMaxCooldown() {
        return getStaticFieldValue("MAX_COOLDOWN", Long.class);
    }

    public final int getReqLevel() {
        return getStaticFieldValue("REQ_LEVEL", Integer.class);
    }

    public final byte getReqWeapon() {
        return getStaticFieldValue("REQ_WEAPON", Byte.class);
    }

    public final byte getSkillCode() {
        return getStaticFieldValue("SKILL_CODE", Byte.class);
    }

    public final String getSkillName() {
        return getStaticFieldValue("SKILL_NAME", String.class);
    }

    public final boolean isPassive() {
        return getStaticFieldValue("IS_PASSIVE", Boolean.class);
    }

    public final boolean cantLevel() {
        return getStaticFieldValue("CANT_LEVEL", Boolean.class);
    }

    public void updateDesc() {
        if (this.cantLevel()) {
            return;
        }
        String[] LEVEL_DESC = getStaticFieldValue("LEVEL_DESC", String[].class);
        String[] MAX_BONUS_DESC = (this.isPassive() ? null : getStaticFieldValue("MAX_BONUS_DESC", String[].class));
        double BASE_VALUE = getStaticFieldValue("BASE_VALUE", Double.class);
        double MULT_VALUE = getStaticFieldValue("MULT_VALUE", Double.class);

        double curLevelValue = calculateSkillValue(BASE_VALUE, MULT_VALUE, this.level, 100);
        double nextLevelValue = calculateSkillValue(BASE_VALUE, MULT_VALUE, this.level + 1, 100);

        if (LEVEL_DESC != null) {
            this.skillCurLevelDesc = new String[LEVEL_DESC.length];
            for (byte i = 0; i < LEVEL_DESC.length; i++) {
                this.skillCurLevelDesc[i] = LEVEL_DESC[i].replaceAll(Skill.BASE_VALUE_KEY, Globals.NUMBER_FORMAT.format(curLevelValue));
                this.skillCurLevelDesc[i] = processDescLine(this.skillCurLevelDesc[i], this.level);
            }

            this.skillNextLevelDesc = new String[LEVEL_DESC.length];
            for (byte i = 0; i < LEVEL_DESC.length; i++) {
                this.skillNextLevelDesc[i] = LEVEL_DESC[i].replaceAll(Skill.BASE_VALUE_KEY, Globals.NUMBER_FORMAT.format(nextLevelValue));
                this.skillNextLevelDesc[i] = processDescLine(this.skillNextLevelDesc[i], this.level + 1);
            }
        } else {
            this.skillCurLevelDesc = new String[]{};
            this.skillNextLevelDesc = new String[]{};
        }

        if (MAX_BONUS_DESC != null) {
            this.maxBonusDesc = new String[MAX_BONUS_DESC.length];
            for (byte i = 0; i < MAX_BONUS_DESC.length; i++) {
                this.maxBonusDesc[i] = MAX_BONUS_DESC[i];
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
