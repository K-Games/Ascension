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
        this.skillCode = SWORD_MULTI;
        this.maxCooldown = 18000;
        this.reqWeapon = Globals.ITEM_SWORD;
    }

}
