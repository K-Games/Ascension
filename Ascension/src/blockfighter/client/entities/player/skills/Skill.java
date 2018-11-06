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

public class Skill {

    private transient String[] CUSTOM_DATA_HEADERS;
    private transient HashMap<String, Double> CUSTOM_VALUES;

    private transient String SKILL_NAME;

    private transient String[] DESCRIPTION;
    private transient String[] LEVEL_DESC;
    private transient String[] MAX_BONUS_DESC;

    private transient boolean IS_PASSIVE;
    private transient boolean CANT_LEVEL;
    private transient byte REQ_WEAPON;
    private transient long MAX_COOLDOWN;

    private transient double BASE_VALUE, MULT_VALUE;
    private transient int REQ_LEVEL;

    private static final String HYPER_STANCE_DESC = "Grants Hyper Stance - Ignore knockback and stuns.";
    private static final String HYPER_STANCE_KEY = "%HYPERSTANCE";
    private static final String BASE_VALUE_KEY = "%BV",
            VALUE_EXPR_HEAD = "%D(", EXPR_END = ")^",
            DURATION_EXPR_HEAD = "%T(",
            REPLACE_KEY = "%REPLACE";

    private transient FontMetrics fontMetric;
    private transient int boxWidth, boxHeight;

    private byte level;

    private transient byte skillCode;
    private transient long skillCastTime;

    private transient String[] skillCurLevelDesc = new String[0];
    private transient String[] skillNextLevelDesc = new String[0];
    private transient String[] maxBonusDesc = new String[0];

    public Skill() {
    }

    public Skill(Skill skill) {
        skillCode = skill.skillCode;
        CUSTOM_DATA_HEADERS = skill.CUSTOM_DATA_HEADERS;
        CUSTOM_VALUES = skill.CUSTOM_VALUES;

        SKILL_NAME = skill.SKILL_NAME;

        DESCRIPTION = skill.DESCRIPTION;
        LEVEL_DESC = skill.LEVEL_DESC;
        MAX_BONUS_DESC = skill.MAX_BONUS_DESC;

        REQ_WEAPON = skill.REQ_WEAPON;
        MAX_COOLDOWN = skill.MAX_COOLDOWN;
        BASE_VALUE = skill.BASE_VALUE;
        MULT_VALUE = skill.MULT_VALUE;
        IS_PASSIVE = skill.IS_PASSIVE;
        CANT_LEVEL = skill.CANT_LEVEL;
        REQ_LEVEL = skill.REQ_LEVEL;

        CUSTOM_VALUES = skill.CUSTOM_VALUES;
    }

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
                    e.setVariable(variable, BASE_VALUE);
                    break;
                case "multvalue":
                    e.setVariable(variable, MULT_VALUE);
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

    public final Double getCustomValue(String customHeader) {
        HashMap customValues = CUSTOM_VALUES;
        return (Double) customValues.get(customHeader);
    }

    public final String[] getDesc() {
        return DESCRIPTION;
    }

    public final BufferedImage getIcon() {
        return Globals.SKILL_ICON[getSkillCode()];
    }

    public final BufferedImage getDisabledIcon() {
        return Globals.SKILL_DISABLED_ICON[getSkillCode()];
    }

    public long getMaxCooldown() {
        return MAX_COOLDOWN;
    }

    public final int getReqLevel() {
        return REQ_LEVEL;
    }

    public final byte getReqWeapon() {
        return REQ_WEAPON;
    }

    public final byte getSkillCode() {
        return skillCode;
    }

    public final String getSkillName() {
        return SKILL_NAME;
    }

    public final boolean isPassive() {
        return IS_PASSIVE;
    }

    public final boolean cantLevel() {
        return CANT_LEVEL;
    }

    public void updateDesc() {
        if (this.cantLevel()) {
            return;
        }

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

    public void setSkillCode(byte skillCode) {
        this.skillCode = skillCode;
        loadSkillData();
    }

    private void loadSkillData() {
        String[] data = Globals.loadSkillRawData(skillCode);
        HashMap<String, Integer> dataHeaders = Globals.getDataHeaders(data);

        CUSTOM_DATA_HEADERS = Globals.getSkillCustomHeaders(data, dataHeaders);
        CUSTOM_VALUES = new HashMap<>(CUSTOM_DATA_HEADERS.length);

        SKILL_NAME = Globals.loadSkillName(data, dataHeaders);

        DESCRIPTION = Globals.loadSkillDesc(data, dataHeaders);
        LEVEL_DESC = Globals.loadSkillLevelDesc(data, dataHeaders);
        MAX_BONUS_DESC = Globals.loadSkillMaxBonusDesc(data, dataHeaders);

        REQ_WEAPON = Globals.loadSkillReqWeapon(data, dataHeaders);
        MAX_COOLDOWN = (long) Globals.loadDoubleValue(data, dataHeaders, Globals.SKILL_MAXCOOLDOWN_HEADER);
        BASE_VALUE = Globals.loadDoubleValue(data, dataHeaders, Globals.SKILL_BASEVALUE_HEADER);
        MULT_VALUE = Globals.loadDoubleValue(data, dataHeaders, Globals.SKILL_MULTVALUE_HEADER);
        IS_PASSIVE = Globals.loadBooleanValue(data, dataHeaders, Globals.SKILL_PASSIVE_HEADER);
        CANT_LEVEL = Globals.loadBooleanValue(data, dataHeaders, Globals.SKILL_CANT_LEVEL_HEADER);
        REQ_LEVEL = Globals.loadSkillReqLevel(data, dataHeaders);

        for (String customHeader : CUSTOM_DATA_HEADERS) {
            CUSTOM_VALUES.put(customHeader, Globals.loadDoubleValue(data, dataHeaders, customHeader));
        }
    }

    public String[] getCustomDataHeaders() {
        return CUSTOM_DATA_HEADERS;
    }
}
