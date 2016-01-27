package blockfighter.server.entities.boss.Lightning;

import blockfighter.server.entities.player.skills.Skill;

/**
 *
 * @author Ken Kwan
 */
public class SkillBall extends Skill {

	public SkillBall() {
		this.skillCode = BossLightning.SKILL_BALL;
		this.maxCooldown = 10000;
	}

}
