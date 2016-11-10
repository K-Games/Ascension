package blockfighter.client.entities.player.skills;

import blockfighter.shared.Globals;
import java.awt.image.BufferedImage;
import java.util.HashMap;

public class SkillBowFrost extends Skill {

    private static final String BASESTUN_HEADER = "[basestun]",
            MAXLEVELSTUN_HEADER = "[maxlevelstun]",
            MAXLEVELBONUSPROJ_HEADER = "[maxlevelbonusproj]",
            MAXLEVELBONUSDAMAGE_HEADER = "[maxlevelbonusdamage]";

    private static final String[] CUSTOM_DATA_HEADERS = {
        BASESTUN_HEADER,
        MAXLEVELSTUN_HEADER,
        MAXLEVELBONUSPROJ_HEADER,
        MAXLEVELBONUSDAMAGE_HEADER
    };

    private static final double BASE_STUN, MAX_LEVEL_STUN, MAX_LEVEL_BONUS_DAMAGE;
    private static final int MAX_LEVEL_BONUS_PROJ;

    private static final byte SKILL_CODE = Globals.BOW_FROST;
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

        BASE_STUN = Globals.loadDoubleValue(data, dataHeaders, BASESTUN_HEADER) / 1000D;
        MAX_LEVEL_STUN = Globals.loadDoubleValue(data, dataHeaders, MAXLEVELSTUN_HEADER) / 1000D;
        MAX_LEVEL_BONUS_DAMAGE = Globals.loadDoubleValue(data, dataHeaders, MAXLEVELBONUSDAMAGE_HEADER) * 100;
        MAX_LEVEL_BONUS_PROJ = (int) Globals.loadDoubleValue(data, dataHeaders, MAXLEVELBONUSPROJ_HEADER);
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
            "Deals " + NUMBER_FORMAT.format(BASE_VALUE + MULT_VALUE * this.level) + "% damage. Stuns for " + TIME_NUMBER_FORMAT.format(BASE_STUN) + " seconds."
        };
        this.skillNextLevelDesc = new String[]{
            "Deals " + NUMBER_FORMAT.format(BASE_VALUE + MULT_VALUE * (this.level + 1)) + "% damage. Stuns for " + TIME_NUMBER_FORMAT.format(BASE_STUN) + " seconds."
        };
        this.maxBonusDesc = new String[]{
            "Stun now lasts for " + TIME_NUMBER_FORMAT.format(MAX_LEVEL_STUN) + " seconds.",
            "Hits an additional " + MAX_LEVEL_BONUS_PROJ + " times dealing " + NUMBER_FORMAT.format(MAX_LEVEL_BONUS_DAMAGE) + "% damage."
        };
    }
}
