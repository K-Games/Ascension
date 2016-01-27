package blockfighter.server.entities.boss.Lightning;

import blockfighter.server.entities.player.skills.Skill;

/**
 *
 * @author Ken Kwan
 */
public class SkillBolt extends Skill {

	public SkillBolt() {
		this.skillCode = BossLightning.SKILL_BOLT;
		this.maxCooldown = 20000;
	}

}
