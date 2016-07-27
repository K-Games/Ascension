package blockfighter.server.entities.mob.boss.Lightning;

import blockfighter.server.LogicModule;
import blockfighter.server.entities.mob.MobSkill;

public class SkillBolt extends MobSkill {

    public SkillBolt(final LogicModule l) {
        super(l);
        this.skillCode = BossLightning.SKILL_BOLT;
        this.maxCooldown = 20000;
    }

}
