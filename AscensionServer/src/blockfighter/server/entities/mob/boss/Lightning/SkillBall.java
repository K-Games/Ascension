package blockfighter.server.entities.mob.boss.Lightning;

import blockfighter.server.LogicModule;
import blockfighter.server.entities.mob.MobSkill;

public class SkillBall extends MobSkill {

    public SkillBall(final LogicModule l) {
        super(l);
        this.skillCode = BossLightning.SKILL_BALL;
        this.maxCooldown = 10000;
    }

}
