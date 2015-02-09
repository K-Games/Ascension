package blockfighter.server.entities.player.skills;

import blockfighter.server.Globals;

/**
 *
 * @author Ken Kwan
 */
public class SkillBowArc extends Skill {

    /**
     * Constructor for Bow Skill Arcshot.
     */
    public SkillBowArc() {
        skillCode = BOW_ARC;
        maxCooldown = 500;
        reqWeapon = Globals.ITEM_BOW;
    }

}
