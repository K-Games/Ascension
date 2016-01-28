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
        this.skillCode = BOW_ARC;
        this.maxCooldown = 500;
        this.reqWeapon = Globals.ITEM_BOW;
    }

}
