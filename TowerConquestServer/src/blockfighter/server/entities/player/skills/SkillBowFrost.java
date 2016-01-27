package blockfighter.server.entities.player.skills;

import blockfighter.server.Globals;

/**
 *
 * @author Ken Kwan
 */
public class SkillBowFrost extends Skill {

	/**
	 * Constructor for Bow Skill Frost Bind.
	 */
	public SkillBowFrost() {
		this.skillCode = BOW_FROST;
		this.maxCooldown = 22000;
		this.reqWeapon = Globals.ITEM_BOW;
	}

}
