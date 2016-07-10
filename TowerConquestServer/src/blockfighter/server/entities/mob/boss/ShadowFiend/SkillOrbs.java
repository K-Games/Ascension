package blockfighter.server.entities.mob.boss.ShadowFiend;

import blockfighter.server.LogicModule;
import blockfighter.server.entities.mob.MobSkill;

/**
 *
 * @author Ken Kwan
 */
public class SkillOrbs extends MobSkill {

    public SkillOrbs(final LogicModule l) {
        super(l);
        this.skillCode = BossShadowFiend.SKILL_ORBS;
    }

}
