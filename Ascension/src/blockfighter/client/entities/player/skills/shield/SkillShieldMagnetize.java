package blockfighter.client.entities.player.skills.shield;

import blockfighter.client.entities.player.skills.Skill;
import blockfighter.shared.Globals;
import java.util.HashMap;

public class SkillShieldMagnetize extends Skill {

    public static final String[] CUSTOM_DATA_HEADERS;
    public static final HashMap<String, Double> CUSTOM_VALUES;

    public static final byte SKILL_CODE = Globals.SHIELD_MAGNETIZE;
    public static final String SKILL_NAME;
    public static final String[] DESCRIPTION;
    public static final boolean IS_PASSIVE;
    public static final boolean CANT_LEVEL;
    public static final byte REQ_WEAPON;
    public static final long MAX_COOLDOWN;

    public static final double BASE_VALUE, MULT_VALUE;
    public static final int REQ_LEVEL;

    static {
        String[] data = Globals.loadSkillRawData(SKILL_CODE);
        HashMap<String, Integer> dataHeaders = Globals.getDataHeaders(data);

        CUSTOM_DATA_HEADERS = Globals.getSkillCustomHeaders(data, dataHeaders);
        CUSTOM_VALUES = new HashMap<>(CUSTOM_DATA_HEADERS.length);

        SKILL_NAME = Globals.loadSkillName(data, dataHeaders);
        DESCRIPTION = Globals.loadSkillDesc(data, dataHeaders);
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

    @Override
    public void updateDesc() {
        this.skillCurLevelDesc = new String[]{
            "Deals " + Globals.NUMBER_FORMAT.format((BASE_VALUE + MULT_VALUE * this.level) * 100) + "% damage + Defense multiplied by " + Globals.NUMBER_FORMAT.format(CUSTOM_VALUES.get(CUSTOM_DATA_HEADERS[0]) + CUSTOM_VALUES.get(CUSTOM_DATA_HEADERS[1]) * this.level) + "."
        };
        this.skillNextLevelDesc = new String[]{
            "Deals " + Globals.NUMBER_FORMAT.format((BASE_VALUE + MULT_VALUE * (this.level + 1)) * 100) + "% damage + Defense multiplied by " + Globals.NUMBER_FORMAT.format(CUSTOM_VALUES.get(CUSTOM_DATA_HEADERS[0]) + CUSTOM_VALUES.get(CUSTOM_DATA_HEADERS[1]) * (this.level + 1)) + "."
        };
        this.maxBonusDesc = new String[]{
            "Deals " + Globals.NUMBER_FORMAT.format(CUSTOM_VALUES.get(CUSTOM_DATA_HEADERS[2])) + "x damage."
        };
    }
}
