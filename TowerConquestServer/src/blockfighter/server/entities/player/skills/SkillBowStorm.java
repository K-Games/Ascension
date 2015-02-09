package blockfighter.server.entities.player.skills;

import blockfighter.server.Globals;

/**
 *
 * @author Ken Kwan
 */
public class SkillBowStorm extends Skill {

    /**
     * Constructor for Bow Skill Arrow Storm.
     */
    public SkillBowStorm() {
        skillCode = BOW_STORM;
        maxCooldown = 13000;
        reqWeapon = Globals.ITEM_BOW;
    }

}
