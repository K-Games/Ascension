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
        skillCode = SWORD_TAUNT;
        maxCooldown = 25000;
        reqWeapon = Globals.ITEM_SWORD;
    }

}
