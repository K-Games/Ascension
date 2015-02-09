package blockfighter.server.entities.player.skills;

import blockfighter.server.Globals;

/**
 *
 * @author Ken Kwan
 */
public class SkillBowVolley extends Skill {

    /**
     * Constructor for Bow Skill Volley.
     */
    public SkillBowVolley() {
        skillCode = BOW_VOLLEY;
        maxCooldown = 7000;
        reqWeapon = Globals.ITEM_BOW;
    }

}
