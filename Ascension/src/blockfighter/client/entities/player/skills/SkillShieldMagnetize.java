package blockfighter.client.entities.player.skills;

import blockfighter.shared.Globals;
import java.awt.image.BufferedImage;
import java.util.HashMap;

public class SkillShieldMagnetize extends Skill {

    public static final String CUSTOMHEADER_BASEDEF = "[basedefense]",
            CUSTOMHEADER_MULTDEF = "[multdefense]",
            CUSTOMHEADER_MAXLEVELMULT = "[maxlevelmult]";

    private static final String[] CUSTOM_DATA_HEADERS = {
        CUSTOMHEADER_BASEDEF,
        CUSTOMHEADER_MULTDEF,
        CUSTOMHEADER_MAXLEVELMULT
    };

    private static final HashMap<String, Double> CUSTOM_VALUES = new HashMap<>(3);

    private static final byte SKILL_CODE = Globals.SHIELD_MAGNETIZE;
    private static final BufferedImage ICON = Globals.SKILL_ICON[SKILL_CODE];
    private static final BufferedImage DISABLED_ICON;

    private static final String SKILL_NAME;
    private static final String[] DESCRIPTION;
    private static final boolean IS_PASSIVE;
    private static final byte REQ_WEAPON;
    private static final long MAX_COOLDOWN;

    private static final double BASE_VALUE, MULT_VALUE;
    private static final int REQ_LEVEL;

    static {
        DISABLED_ICON = Globals.getDisabledIcon(ICON);
        String[] data = Globals.loadSkillData(SKILL_CODE);
        HashMap<String, Integer> dataHeaders = Globals.getDataHeaders(data, CUSTOM_DATA_HEADERS);

        SKILL_NAME = Globals.loadSkillName(data, dataHeaders);
        DESCRIPTION = Globals.loadSkillDesc(data, dataHeaders);
        REQ_WEAPON = Globals.loadReqWeapon(data, dataHeaders);
        MAX_COOLDOWN = (long) Globals.loadDoubleValue(data, dataHeaders, Globals.SKILL_MAXCOOLDOWN_HEADER);
        BASE_VALUE = Globals.loadDoubleValue(data, dataHeaders, Globals.SKILL_BASEVALUE_HEADER) * 100;
        MULT_VALUE = Globals.loadDoubleValue(data, dataHeaders, Globals.SKILL_MULTVALUE_HEADER) * 100;
        IS_PASSIVE = Globals.loadBooleanValue(data, dataHeaders, Globals.SKILL_PASSIVE_HEADER);
        REQ_LEVEL = Globals.loadSkillReqLevel(data, dataHeaders);
        CUSTOM_VALUES.put(CUSTOMHEADER_BASEDEF, Globals.loadDoubleValue(data, dataHeaders, CUSTOMHEADER_BASEDEF));
        CUSTOM_VALUES.put(CUSTOMHEADER_MULTDEF, Globals.loadDoubleValue(data, dataHeaders, CUSTOMHEADER_MULTDEF));
        CUSTOM_VALUES.put(CUSTOMHEADER_MAXLEVELMULT, Globals.loadDoubleValue(data, dataHeaders, CUSTOMHEADER_MAXLEVELMULT));
    }

    @Override
    public HashMap<String, Double> getCustomValues() {
        return CUSTOM_VALUES;
    }

    @Override
    public String[] getDesc() {
        return DESCRIPTION;
    }

    @Override
    public BufferedImage getIcon() {
        return ICON;
    }

    @Override
    public long getMaxCooldown() {
        return MAX_COOLDOWN;
    }

    @Override
    public byte getReqWeapon() {
        return REQ_WEAPON;
    }

    @Override
    public byte getSkillCode() {
        return SKILL_CODE;
    }

    @Override
    public String getSkillName() {
        return SKILL_NAME;
    }

    @Override
    public boolean isPassive() {
        return IS_PASSIVE;
    }

    @Override
    public int getReqLevel() {
        return REQ_LEVEL;
    }

    @Override
    public void updateDesc() {
        this.skillCurLevelDesc = new String[]{
            "Deals " + Globals.NUMBER_FORMAT.format(BASE_VALUE + MULT_VALUE * this.level) + "% damage + Defense multiplied by " + Globals.NUMBER_FORMAT.format(CUSTOM_VALUES.get(CUSTOMHEADER_BASEDEF) + CUSTOM_VALUES.get(CUSTOMHEADER_MULTDEF) * this.level) + "."
        };
        this.skillNextLevelDesc = new String[]{
            "Deals " + Globals.NUMBER_FORMAT.format(BASE_VALUE + MULT_VALUE * (this.level + 1)) + "% damage + Defense multiplied by " + Globals.NUMBER_FORMAT.format(CUSTOM_VALUES.get(CUSTOMHEADER_BASEDEF) + CUSTOM_VALUES.get(CUSTOMHEADER_MULTDEF) * (this.level + 1)) + "."
        };
        this.maxBonusDesc = new String[]{
            "Deals " + Globals.NUMBER_FORMAT.format(CUSTOM_VALUES.get(CUSTOMHEADER_MAXLEVELMULT)) + "x damage."
        };
    }

    @Override
    public BufferedImage getDisabledIcon() {
        return DISABLED_ICON;
    }
}
