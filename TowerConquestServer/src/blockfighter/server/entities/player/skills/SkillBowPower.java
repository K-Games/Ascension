package blockfighter.server.entities.player.skills;

import blockfighter.server.Globals;

/**
 *
 * @author Ken Kwan
 */
public class SkillBowPower extends Skill {

    /**
     * Constructor for Bow Skill Power Shot.
     */
    public SkillBowPower() {
        skillCode = BOW_POWER;
        maxCooldown = 16000;
        reqWeapon = Globals.ITEM_BOW;
    }

}
