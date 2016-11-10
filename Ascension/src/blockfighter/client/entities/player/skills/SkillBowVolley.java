package blockfighter.client.entities.player.skills;

import blockfighter.shared.Globals;
import java.awt.image.BufferedImage;
import java.util.HashMap;

public class SkillBowVolley extends Skill {

    private static final String MAXLVLBUFFDMG_HEADER = "[maxlevelbuffdamage]",
            MAXLVLBUFFDURATION_HEADER = "[maxlevelbuffduration]";

    private static final String[] CUSTOM_DATA_HEADERS = {
        MAXLVLBUFFDMG_HEADER,
        MAXLVLBUFFDURATION_HEADER};

    private static final double MAX_LEVEL_BUFF_DAMAGE, MAX_LEVEL_BUFF_DURATION;

    private static final byte SKILL_CODE = Globals.BOW_VOLLEY;
    private static final BufferedImage ICON = Globals.SKILL_ICON[SKILL_CODE];

    private static final String SKILL_NAME;
    private static final String[] DESCRIPTION;
    private static final boolean IS_PASSIVE;
    private static final byte REQ_WEAPON;
    private static final double MAX_COOLDOWN;

    private static final double BASE_VALUE, MULT_VALUE;

    static {
        String[] data = Globals.loadSkillData(SKILL_CODE);
        HashMap<String, Integer> dataHeaders = Globals.getDataHeaders(data, CUSTOM_DATA_HEADERS);

        SKILL_NAME = Globals.loadSkillName(data, dataHeaders);
        DESCRIPTION = Globals.loadSkillDesc(data, dataHeaders);
        REQ_WEAPON = Globals.loadReqWeapon(data, dataHeaders);
        MAX_COOLDOWN = Globals.loadDoubleValue(data, dataHeaders, Globals.SKILL_MAXCOOLDOWN_HEADER);
        BASE_VALUE = Globals.loadDoubleValue(data, dataHeaders, Globals.SKILL_BASEVALUE_HEADER) * 100;
        MULT_VALUE = Globals.loadDoubleValue(data, dataHeaders, Globals.SKILL_MULTVALUE_HEADER) * 100;
        IS_PASSIVE = Globals.loadBooleanValue(data, dataHeaders, Globals.SKILL_PASSIVE_HEADER);

        MAX_LEVEL_BUFF_DAMAGE = Globals.loadDoubleValue(data, dataHeaders, MAXLVLBUFFDMG_HEADER) * 100;
        MAX_LEVEL_BUFF_DURATION = Globals.loadDoubleValue(data, dataHeaders, MAXLVLBUFFDURATION_HEADER) / 1000;
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
    public double getMaxCooldown() {
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
            "Deals " + NUMBER_FORMAT.format(BASE_VALUE + MULT_VALUE * this.level) + "% damage per hit"
        };
        this.skillNextLevelDesc = new String[]{
            "Deals " + NUMBER_FORMAT.format(BASE_VALUE + MULT_VALUE * (this.level + 1)) + "% damage per hit"
        };
        this.maxBonusDesc = new String[]{
            "Each Critical Hit increases damage by " + NUMBER_FORMAT.format(MAX_LEVEL_BUFF_DAMAGE) + "% for " + TIME_NUMBER_FORMAT.format(MAX_LEVEL_BUFF_DURATION) + " seconds."
        };
    }
}
