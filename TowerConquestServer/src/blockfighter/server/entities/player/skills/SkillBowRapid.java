package blockfighter.server.entities.player.skills;

import blockfighter.server.Globals;

/**
 *
 * @author Ken Kwan
 */
public class SkillBowRapid extends Skill {

    /**
     * Constructor for Bow Skill Rapid Fire.
     */
    public SkillBowRapid() {
        this.skillCode = BOW_RAPID;
        this.maxCooldown = 700;
        this.reqWeapon = Globals.ITEM_BOW;
    }

}
