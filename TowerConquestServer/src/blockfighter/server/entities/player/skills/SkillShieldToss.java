package blockfighter.server.entities.player.skills;

import blockfighter.server.Globals;

/**
 *
 * @author Ken Kwan
 */
public class SkillShieldToss extends Skill {

	/**
	 * Constructor for Shield Skill Shield Throw.
	 */
	public SkillShieldToss() {
		this.skillCode = SHIELD_TOSS;
		this.maxCooldown = 13000;
		this.reqWeapon = Globals.ITEM_SHIELD;
	}

}
