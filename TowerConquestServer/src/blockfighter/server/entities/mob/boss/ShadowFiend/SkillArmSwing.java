package blockfighter.server.entities.mob.boss.ShadowFiend;

import blockfighter.server.LogicModule;
import blockfighter.server.entities.mob.MobSkill;

/**
 *
 * @author Ken Kwan
 */
public class SkillArmSwing extends MobSkill {

    public SkillArmSwing(final LogicModule l) {
        super(l);
        this.skillCode = BossShadowFiend.SKILL_ARMSWING;
    }

}
