package blockfighter.client.entities.player.skills.passive;

import blockfighter.client.entities.player.skills.Skill;
import blockfighter.shared.Globals;
import java.awt.image.BufferedImage;
import java.util.HashMap;

public class SkillPassiveResistance extends Skill {

    private static final byte SKILL_CODE = Globals.PASSIVE_RESIST;
    private static final String SKILL_NAME;
    private static final String[] DESCRIPTION;
    private static final boolean IS_PASSIVE;
    private static final byte REQ_WEAPON;
    private static final long MAX_COOLDOWN;

    private static final double BASE_VALUE, MULT_VALUE;
    private static final int REQ_LEVEL;

    static {
        String[] data = Globals.loadSkillRawData(SKILL_CODE);
        HashMap<String, Integer> dataHeaders = Globals.getDataHeaders(data);

        SKILL_NAME = Globals.loadSkillName(data, dataHeaders);
        DESCRIPTION = Globals.loadSkillDesc(data, dataHeaders);
        REQ_WEAPON = Globals.loadSkillReqWeapon(data, dataHeaders);
        MAX_COOLDOWN = (long) Globals.loadDoubleValue(data, dataHeaders, Globals.SKILL_MAXCOOLDOWN_HEADER);
        BASE_VALUE = Globals.loadDoubleValue(data, dataHeaders, Globals.SKILL_BASEVALUE_HEADER);
        MULT_VALUE = Globals.loadDoubleValue(data, dataHeaders, Globals.SKILL_MULTVALUE_HEADER);
        IS_PASSIVE = Globals.loadBooleanValue(data, dataHeaders, Globals.SKILL_PASSIVE_HEADER);
        REQ_LEVEL = Globals.loadSkillReqLevel(data, dataHeaders);
    }

    @Override
    public HashMap<String, Double> getCustomValues() {
        return null;
    }

    @Override
    public String[] getDesc() {
        return DESCRIPTION;
    }

    @Override
    public BufferedImage getIcon() {
        return Globals.SKILL_ICON[SKILL_CODE];
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
    public long getMaxCooldown() {
        return (long) (MAX_COOLDOWN - (BASE_VALUE + MULT_VALUE * this.level));
    }

    @Override
    public void setCooldown() {
        super.setCooldown();
    }

    @Override
    public int getReqLevel() {
        return REQ_LEVEL;
    }

    @Override
    public void updateDesc() {
        this.skillCurLevelDesc = new String[]{
            "Reduce cooldown by " + Globals.TIME_NUMBER_FORMAT.format(BASE_VALUE / 1000 + MULT_VALUE / 1000 * this.level) + ((BASE_VALUE / 1000 + MULT_VALUE / 1000 * this.level > 1) ? " seconds." : " second.")
        };
        this.skillNextLevelDesc = new String[]{
            "Reduce cooldown by " + Globals.NUMBER_FORMAT.format(BASE_VALUE / 1000 + MULT_VALUE / 1000 * (this.level + 1)) + ((BASE_VALUE / 1000 + MULT_VALUE / 1000 * (this.level + 1) > 1) ? " seconds." : " second.")
        };
    }

    @Override
    public BufferedImage getDisabledIcon() {
        return Globals.SKILL_DISABLED_ICON[SKILL_CODE];
    }
}
