package blockfighter.server.entities.mob.boss.Lightning;

import blockfighter.server.LogicModule;
import blockfighter.server.entities.player.skills.Skill;

/**
 *
 * @author Ken Kwan
 */
public class SkillBall extends Skill {

    public SkillBall(final LogicModule l) {
        super(l);
        this.skillCode = BossLightning.SKILL_BALL;
        this.maxCooldown = 10000;
    }

}
