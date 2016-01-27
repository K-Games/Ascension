package blockfighter.server.entities.player.skills;

import blockfighter.server.Globals;

/**
 *
 * @author Ken Kwan
 */
public class SkillBowPower extends Skill {

	/**
	 * Constructor for Bow Skill Power Shot.
	 */
	public SkillBowPower() {
		this.skillCode = BOW_POWER;
		this.maxCooldown = 16000;
		this.reqWeapon = Globals.ITEM_BOW;
	}

}
