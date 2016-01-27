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
        skillCode = BOW_FROST;
        maxCooldown = 22000;
        reqWeapon = Globals.ITEM_BOW;
    }

}
