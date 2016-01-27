package blockfighter.server.entities.boss.Lightning;

import blockfighter.server.entities.player.skills.Skill;

/**
 *
 * @author Ken Kwan
 */
public class SkillAttack1 extends Skill {

	public SkillAttack1() {
		this.skillCode = BossLightning.SKILL_ATT1;
		this.maxCooldown = 3000;
	}

}
