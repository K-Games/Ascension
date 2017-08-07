package blockfighter.server.entities.player.skills;

import blockfighter.server.LogicModule;
import blockfighter.shared.Globals;
import java.util.HashMap;

public class SkillPassiveResistance extends SkillPassive {

    private static final byte SKILL_CODE = Globals.PASSIVE_RESIST;

    private static final boolean IS_PASSIVE;
    private static final byte REQ_WEAPON;
    private static final long MAX_COOLDOWN;

    private static final double BASE_VALUE, MULT_VALUE;
    private static final int REQ_LEVEL;

    static {
        String[] data = Globals.loadSkillData(SKILL_CODE);
        HashMap<String, Integer> dataHeaders = Globals.getDataHeaders(data);

        REQ_WEAPON = Globals.loadReqWeapon(data, dataHeaders);
        REQ_LEVEL = Globals.loadSkillReqLevel(data, dataHeaders);
        MAX_COOLDOWN = (long) Globals.loadDoubleValue(data, dataHeaders, Globals.SKILL_MAXCOOLDOWN_HEADER);
        BASE_VALUE = Globals.loadDoubleValue(data, dataHeaders, Globals.SKILL_BASEVALUE_HEADER);
        MULT_VALUE = Globals.loadDoubleValue(data, dataHeaders, Globals.SKILL_MULTVALUE_HEADER);
        IS_PASSIVE = Globals.loadBooleanValue(data, dataHeaders, Globals.SKILL_PASSIVE_HEADER);
    }

    public SkillPassiveResistance(final LogicModule l) {
        super(l);
    }

    @Override
    public double getBaseValue() {
        return BASE_VALUE;
    }

    @Override
    public double getMultValue() {
        return MULT_VALUE;
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
}
