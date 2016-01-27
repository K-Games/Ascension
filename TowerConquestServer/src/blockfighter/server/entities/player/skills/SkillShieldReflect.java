package blockfighter.server.entities.player.skills;

import blockfighter.server.Globals;

/**
 *
 * @author Ken Kwan
 */
public class SkillShieldReflect extends Skill {

	/**
	 * Constructor for Shield Skill Reflect.
	 */
	public SkillShieldReflect() {
		this.skillCode = SHIELD_REFLECT;
		this.maxCooldown = 15000;
		this.reqWeapon = Globals.ITEM_SHIELD;
	}

}
