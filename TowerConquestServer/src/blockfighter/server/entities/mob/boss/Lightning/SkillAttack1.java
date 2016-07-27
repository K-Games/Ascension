package blockfighter.server.entities.mob.boss.Lightning;

import blockfighter.server.LogicModule;
import blockfighter.server.entities.mob.MobSkill;

public class SkillAttack1 extends MobSkill {

    public SkillAttack1(final LogicModule l) {
        super(l);
        this.skillCode = BossLightning.SKILL_ATT1;
        this.maxCooldown = 3000;
    }

}
