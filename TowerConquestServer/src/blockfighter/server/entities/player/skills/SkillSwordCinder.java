package blockfighter.server.entities.player.skills;

import blockfighter.server.Globals;

/**
 *
 * @author Ken Kwan
 */
public class SkillSwordCinder extends Skill {

	/**
	 * Constructor for Sword Skill Cinder.
	 */
	public SkillSwordCinder() {
		this.skillCode = SWORD_CINDER;
		this.maxCooldown = 20000;
		this.reqWeapon = Globals.ITEM_SWORD;
	}

}
