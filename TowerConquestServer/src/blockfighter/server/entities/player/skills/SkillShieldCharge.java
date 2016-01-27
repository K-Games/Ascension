package blockfighter.server.entities.player.skills;

import blockfighter.server.Globals;

/**
 *
 * @author Ken Kwan
 */
public class SkillShieldCharge extends Skill {

	/**
	 * Constructor for Shield Skill Charge.
	 */
	public SkillShieldCharge() {
		this.skillCode = SHIELD_CHARGE;
		this.maxCooldown = 17000;
		this.reqWeapon = Globals.ITEM_SHIELD;
	}

}
