package blockfighter.server.entities.player.skills;

import blockfighter.server.Globals;

/**
 *
 * @author Ken Kwan
 */
public class SkillShieldIron extends Skill {

	/**
	 * Constructor for Shield Skill Iron Fortress.
	 */
	public SkillShieldIron() {
		this.skillCode = SHIELD_IRON;
		this.maxCooldown = 20000;
		this.reqWeapon = Globals.ITEM_SHIELD;
	}

}
