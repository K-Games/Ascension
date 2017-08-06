package blockfighter.client.entities.player.skills;

import blockfighter.shared.Globals;
import java.awt.image.BufferedImage;
import java.util.HashMap;

public class SkillPassiveShieldMastery extends Skill {

    public static final String CUSTOMHEADER_BASEDMGREDUCT = "[basedmgreduct]",
            CUSTOMHEADER_MULTDMGREDUCT = "[multdmgreduct]";

    private static final String[] CUSTOM_DATA_HEADERS = {
        CUSTOMHEADER_BASEDMGREDUCT,
        CUSTOMHEADER_MULTDMGREDUCT
    };

    private static final HashMap<String, Double> CUSTOM_VALUES = new HashMap<>(2);

    private static final byte SKILL_CODE = Globals.PASSIVE_SHIELDMASTERY;
    private static final BufferedImage ICON = Globals.SKILL_ICON[SKILL_CODE];

    private static final String SKILL_NAME;
    private static final String[] DESCRIPTION;
    private static final boolean IS_PASSIVE;
    private static final byte REQ_WEAPON;
    private static final long MAX_COOLDOWN;

    private static final double BASE_VALUE, MULT_VALUE;
    private static final int REQ_LEVEL;

    static {
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

        CUSTOM_VALUES.put(CUSTOMHEADER_BASEDMGREDUCT, Globals.loadDoubleValue(data, dataHeaders, CUSTOMHEADER_BASEDMGREDUCT) * 100);
        CUSTOM_VALUES.put(CUSTOMHEADER_MULTDMGREDUCT, Globals.loadDoubleValue(data, dataHeaders, CUSTOMHEADER_MULTDMGREDUCT) * 100);
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
//        this.description = new String[]{
//            "When equipped with a Sword and Shield you deal",
//            "additional damage and take reduced damage."
//        };
        this.skillCurLevelDesc = new String[]{
            "Deal additional " + Globals.NUMBER_FORMAT.format(BASE_VALUE + MULT_VALUE * this.level) + "% damage.",
            "Take " + Globals.NUMBER_FORMAT.format(CUSTOM_VALUES.get(CUSTOMHEADER_BASEDMGREDUCT) + CUSTOM_VALUES.get(CUSTOMHEADER_MULTDMGREDUCT) * this.level) + "% reduced damage."
        };
        this.skillNextLevelDesc = new String[]{
            "Deal additional " + Globals.NUMBER_FORMAT.format(BASE_VALUE + MULT_VALUE * (this.level + 1)) + "% damage.",
            "Take " + Globals.NUMBER_FORMAT.format(CUSTOM_VALUES.get(CUSTOMHEADER_BASEDMGREDUCT) + CUSTOM_VALUES.get(CUSTOMHEADER_MULTDMGREDUCT) * (this.level + 1)) + "% reduced damage."
        };
    }
}
