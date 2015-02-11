package blockfighter.server.entities.player.skills;

import blockfighter.server.Globals;

/**
 *
 * @author Ken Kwan
 */
public class SkillShieldFortify extends Skill {

    /**
     * Constructor for Shield Skill Fortify.
     */
    public SkillShieldFortify() {
        skillCode = SHIELD_FORTIFY;
        maxCooldown = 15000;
        reqWeapon = Globals.ITEM_SHIELD;
    }

}
