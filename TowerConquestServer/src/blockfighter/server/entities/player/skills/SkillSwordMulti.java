package blockfighter.server.entities.player.skills;

import blockfighter.server.Globals;

/**
 *
 * @author Ken Kwan
 */
public class SkillSwordMulti extends Skill {

    /**
     * Constructor for Sword Skill Whirlwind.
     */
    public SkillSwordMulti() {
        skillCode = SWORD_MULTI;
        maxCooldown = 6000;
        reqWeapon = Globals.ITEM_WEAPON;
    }

}
