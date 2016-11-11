package blockfighter.client.entities.player.skills;

import blockfighter.shared.Globals;
import java.awt.image.BufferedImage;
import java.util.HashMap;

public class SkillSwordCinder extends Skill {

    private static final String BUFFDURATION_HEADER = "[buffduration]",
            DMGAMP_HEADER = "[damageamp]",
            BURNDMG_HEADER = "[burndamage]",
            BONUSCRITCHC_HEADER = "[bonuscritchc]";

    private static final String[] CUSTOM_DATA_HEADERS = {
        BUFFDURATION_HEADER,
        DMGAMP_HEADER,
        BURNDMG_HEADER,
        BONUSCRITCHC_HEADER
    };

    private static final double BUFF_DURATION,
            BURN_DAMAGE,
            DAMAGE_AMP,
            BONUS_CRIT_CHANCE;

    private static final byte SKILL_CODE = Globals.SWORD_CINDER;
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
        BUFF_DURATION = Globals.loadDoubleValue(data, dataHeaders, BUFFDURATION_HEADER) / 1000D;
        BURN_DAMAGE = Globals.loadDoubleValue(data, dataHeaders, BURNDMG_HEADER) * 100;
        DAMAGE_AMP = Globals.loadDoubleValue(data, dataHeaders, DMGAMP_HEADER) * 100;
        BONUS_CRIT_CHANCE = Globals.loadDoubleValue(data, dataHeaders, BONUSCRITCHC_HEADER) * 100;
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
            "Deals " + NUMBER_FORMAT.format(BASE_VALUE + MULT_VALUE * this.level) + "% damage.",
            "Burning enemies take " + NUMBER_FORMAT.format(DAMAGE_AMP * this.level) + "% increased damage."
        };
        this.skillNextLevelDesc = new String[]{
            "Deals " + NUMBER_FORMAT.format(BASE_VALUE + MULT_VALUE * (this.level + 1)) + "% damage.",
            "Burning enemies take " + NUMBER_FORMAT.format(DAMAGE_AMP * (this.level + 1)) + "% increased damage."
        };
        this.maxBonusDesc = new String[]{
            "Burn also deals " + NUMBER_FORMAT.format(BURN_DAMAGE) + "% damage per second for " + TIME_NUMBER_FORMAT.format(BUFF_DURATION) + " seconds.",
            "Firebrand has " + NUMBER_FORMAT.format(BONUS_CRIT_CHANCE) + "% Critical Hit Chance."
        };
    }
}
