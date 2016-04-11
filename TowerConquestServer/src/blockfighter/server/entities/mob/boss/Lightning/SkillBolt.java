package blockfighter.server.entities.mob.boss.Lightning;

import blockfighter.server.LogicModule;
import blockfighter.server.entities.player.skills.Skill;

/**
 *
 * @author Ken Kwan
 */
public class SkillBolt extends Skill {

    public SkillBolt(final LogicModule l) {
        super(l);
        this.skillCode = BossLightning.SKILL_BOLT;
        this.maxCooldown = 20000;
    }

}
