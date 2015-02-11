package blockfighter.server.entities.player.skills;

import blockfighter.server.Globals;

/**
 *
 * @author Ken Kwan
 */
public class SkillShieldToss extends Skill {

    /**
     * Constructor for Shield Skill Shield Throw.
     */
    public SkillShieldToss() {
        skillCode = SHIELD_TOSS;
        maxCooldown = 5000;
        reqWeapon = Globals.ITEM_SHIELD;
    }

}
