package blockfighter.server.entities.mob.boss.Lightning;

import blockfighter.server.LogicModule;
import blockfighter.server.entities.mob.MobSkill;

public class SkillAttack2 extends MobSkill {

    public SkillAttack2(final LogicModule l) {
        super(l);
        this.skillCode = BossLightning.SKILL_ATT2;
        this.maxCooldown = 2000;
    }

}
