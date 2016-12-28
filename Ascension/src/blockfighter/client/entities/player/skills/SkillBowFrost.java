package blockfighter.client.entities.player.skills;

import blockfighter.shared.Globals;
import java.awt.image.BufferedImage;
import java.util.HashMap;

public class SkillBowFrost extends Skill {

    public static final String CUSTOMHEADER_BASESTUN = "[basestun]",
            CUSTOMHEADER_MAXLEVELSTUN = "[maxlevelstun]",
            CUSTOMHEADER_MAXLEVELBONUSPROJ = "[maxlevelbonusproj]",
            CUSTOMHEADER_MAXLEVELBONUSDAMAGE = "[maxlevelbonusdamage]";

    private static final String[] CUSTOM_DATA_HEADERS = {
        CUSTOMHEADER_BASESTUN,
        CUSTOMHEADER_MAXLEVELSTUN,
        CUSTOMHEADER_MAXLEVELBONUSPROJ,
        CUSTOMHEADER_MAXLEVELBONUSDAMAGE
    };

    private static final HashMap<String, Double> CUSTOM_VALUES = new HashMap<>(4);

    private static final byte SKILL_CODE = Globals.BOW_FROST;
    private static final BufferedImage ICON = Globals.SKILL_ICON[SKILL_CODE];
    private static final String SKILL_NAME;
    private static final String[] DESCRIPTION;
    private static final boolean IS_PASSIVE;
    private static final byte REQ_WEAPON;
    private static final long MAX_COOLDOWN;
    private static final double BASE_VALUE, MULT_VALUE;

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

        CUSTOM_VALUES.put(CUSTOMHEADER_BASESTUN, Globals.loadDoubleValue(data, dataHeaders, CUSTOMHEADER_BASESTUN) / 1000D);
        CUSTOM_VALUES.put(CUSTOMHEADER_MAXLEVELSTUN, Globals.loadDoubleValue(data, dataHeaders, CUSTOMHEADER_MAXLEVELSTUN) / 1000D);
        CUSTOM_VALUES.put(CUSTOMHEADER_MAXLEVELBONUSDAMAGE, Globals.loadDoubleValue(data, dataHeaders, CUSTOMHEADER_MAXLEVELBONUSDAMAGE) * 100);
        CUSTOM_VALUES.put(CUSTOMHEADER_MAXLEVELBONUSPROJ, Globals.loadDoubleValue(data, dataHeaders, CUSTOMHEADER_MAXLEVELBONUSPROJ));
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
    public void updateDesc() {
        this.skillCurLevelDesc = new String[]{
            "Deals " + Globals.NUMBER_FORMAT.format(BASE_VALUE + MULT_VALUE * this.level) + "% damage. Stuns for " + Globals.TIME_NUMBER_FORMAT.format(CUSTOM_VALUES.get(CUSTOMHEADER_BASESTUN)) + " seconds."
        };
        this.skillNextLevelDesc = new String[]{
            "Deals " + Globals.NUMBER_FORMAT.format(BASE_VALUE + MULT_VALUE * (this.level + 1)) + "% damage. Stuns for " + Globals.TIME_NUMBER_FORMAT.format(CUSTOM_VALUES.get(CUSTOMHEADER_BASESTUN)) + " seconds."
        };
        this.maxBonusDesc = new String[]{
            "Stun now lasts for " + Globals.TIME_NUMBER_FORMAT.format(CUSTOM_VALUES.get(CUSTOMHEADER_MAXLEVELSTUN)) + " seconds.",
            "Hits an additional " + Globals.NUMBER_FORMAT.format(CUSTOM_VALUES.get(CUSTOMHEADER_MAXLEVELBONUSPROJ)) + " times dealing " + Globals.NUMBER_FORMAT.format(CUSTOM_VALUES.get(CUSTOMHEADER_MAXLEVELBONUSDAMAGE)) + "% damage."
        };
    }
}
