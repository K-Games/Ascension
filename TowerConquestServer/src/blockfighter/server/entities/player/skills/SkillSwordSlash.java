package blockfighter.server.entities.player.skills;

import blockfighter.server.Globals;

/**
 *
 * @author Ken Kwan
 */
public class SkillSwordSlash extends Skill {

	/**
	 * Constructor for Sword Skill Defensive Impact.
	 */
	public SkillSwordSlash() {
		this.skillCode = SWORD_SLASH;
		this.maxCooldown = 400;
		this.reqWeapon = Globals.ITEM_SWORD;
	}

}
