package blockfighter.server.entities.player.skills;

import blockfighter.server.Globals;

/**
 *
 * @author Ken Kwan
 */
public class SkillSwordGash extends Skill {

    /**
     * Constructor for Sword Skill Gash.
     */
    public SkillSwordGash() {
        this.skillCode = SWORD_GASH;
        this.maxCooldown = 500;
        this.reqWeapon = Globals.ITEM_SWORD;
    }

}
