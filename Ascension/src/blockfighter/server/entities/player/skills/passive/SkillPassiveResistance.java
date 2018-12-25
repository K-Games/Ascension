package blockfighter.server.entities.player.skills.passive;

import blockfighter.server.LogicModule;
import blockfighter.server.entities.player.skills.SkillPassive;
import blockfighter.shared.Globals;

public class SkillPassiveResistance extends SkillPassive {

    public static final byte SKILL_CODE = Globals.PASSIVE_RESIST;

    public SkillPassiveResistance(final LogicModule l) {
        super(l);
    }

    @Override
    public long getMaxCooldown() {
        return (long) (getSkillData().getMaxCooldown() - (getSkillData().getBaseValue() + getSkillData().getMultValue() * this.level));
    }

}
