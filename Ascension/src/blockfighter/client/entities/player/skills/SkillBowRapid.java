package blockfighter.client.entities.player.skills;

import blockfighter.shared.Globals;
import java.awt.image.BufferedImage;
import java.util.HashMap;

public class SkillBowRapid extends Skill {

    public static final String[] CUSTOM_DATA_HEADERS;
    private static final HashMap<String, Double> CUSTOM_VALUES;

    private static final byte SKILL_CODE = Globals.BOW_RAPID;
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
        HashMap<String, Integer> dataHeaders = Globals.getDataHeaders(data);

        CUSTOM_DATA_HEADERS = Globals.loadSkillCustomHeaders(data, dataHeaders);
        CUSTOM_VALUES = new HashMap<>(CUSTOM_DATA_HEADERS.length);

        SKILL_NAME = Globals.loadSkillName(data, dataHeaders);
        DESCRIPTION = Globals.loadSkillDesc(data, dataHeaders);
        REQ_WEAPON = Globals.loadReqWeapon(data, dataHeaders);
        MAX_COOLDOWN = (long) Globals.loadDoubleValue(data, dataHeaders, Globals.SKILL_MAXCOOLDOWN_HEADER);
        BASE_VALUE = Globals.loadDoubleValue(data, dataHeaders, Globals.SKILL_BASEVALUE_HEADER);
        MULT_VALUE = Globals.loadDoubleValue(data, dataHeaders, Globals.SKILL_MULTVALUE_HEADER);
        IS_PASSIVE = Globals.loadBooleanValue(data, dataHeaders, Globals.SKILL_PASSIVE_HEADER);
        REQ_LEVEL = Globals.loadSkillReqLevel(data, dataHeaders);

        for (String customHeader : CUSTOM_DATA_HEADERS) {
            CUSTOM_VALUES.put(customHeader, Globals.loadDoubleValue(data, dataHeaders, customHeader));
        }
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
            "Deals " + Globals.NUMBER_FORMAT.format((BASE_VALUE + MULT_VALUE * this.level) * 100) + "% damage per hit."
        };
        this.skillNextLevelDesc = new String[]{
            "Deals " + Globals.NUMBER_FORMAT.format((BASE_VALUE + MULT_VALUE * (this.level + 1)) * 100) + "% damage per hit."
        };
        this.maxBonusDesc = new String[]{
            "Each shot has " + Globals.NUMBER_FORMAT.format(CUSTOM_VALUES.get(CUSTOM_DATA_HEADERS[1])) + "% Chance to deal " + Globals.NUMBER_FORMAT.format(CUSTOM_VALUES.get(CUSTOM_DATA_HEADERS[0])) + "x damage."
        };
    }

    @Override
    public BufferedImage getDisabledIcon() {
        return DISABLED_ICON;
    }

}
