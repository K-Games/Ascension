package blockfighter.server.entities.player.skills;

import blockfighter.server.Globals;

/**
 *
 * @author Ken Kwan
 */
public class SkillSwordTaunt extends Skill {

    /**
     * Constructor for Sword Skill Taunt.
     */
    public SkillSwordTaunt() {
        this.skillCode = SWORD_TAUNT;
        this.maxCooldown = 25000;
        this.reqWeapon = Globals.ITEM_SWORD;
    }

}
