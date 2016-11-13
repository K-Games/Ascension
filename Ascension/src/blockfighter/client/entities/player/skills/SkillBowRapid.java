package blockfighter.client.entities.player.skills;

import blockfighter.shared.Globals;
import java.awt.image.BufferedImage;
import java.util.HashMap;

public class SkillBowRapid extends Skill {

    private static final String MAXLVLDMGMULT_HEADER = "[maxleveldamagemult]",
            MAXLVLBONUSCHC_HEADER = "[maxlevelbonuschance]";

    private static final String[] CUSTOM_DATA_HEADERS = {
        MAXLVLDMGMULT_HEADER,
        MAXLVLBONUSCHC_HEADER};

    private static final double MAX_LEVEL_DAMAGE_MULT, MAX_LEVEL_BONUS_CHANCE;

    private static final byte SKILL_CODE = Globals.BOW_RAPID;
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

        MAX_LEVEL_DAMAGE_MULT = Globals.loadDoubleValue(data, dataHeaders, MAXLVLDMGMULT_HEADER);
        MAX_LEVEL_BONUS_CHANCE = Globals.loadDoubleValue(data, dataHeaders, MAXLVLBONUSCHC_HEADER);
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
            "Deals " + NUMBER_FORMAT.format(BASE_VALUE + MULT_VALUE * this.level) + "% damage per hit."
        };
        this.skillNextLevelDesc = new String[]{
            "Deals " + NUMBER_FORMAT.format(BASE_VALUE + MULT_VALUE * (this.level + 1)) + "% damage per hit."
        };
        this.maxBonusDesc = new String[]{
            "Each shot has " + NUMBER_FORMAT.format(MAX_LEVEL_BONUS_CHANCE) + "% Chance to deal " + NUMBER_FORMAT.format(MAX_LEVEL_DAMAGE_MULT) + "x damage."
        };
    }

}
