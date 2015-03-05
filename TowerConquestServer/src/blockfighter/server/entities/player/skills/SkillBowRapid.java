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
        skillCode = BOW_RAPID;
        maxCooldown = 700;
        reqWeapon = Globals.ITEM_BOW;
    }

}
