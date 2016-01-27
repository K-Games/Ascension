package blockfighter.server.entities.player.skills;

import blockfighter.server.Globals;

/**
 *
 * @author Ken Kwan
 */
public class SkillShieldCharge extends Skill {

    /**
     * Constructor for Shield Skill Charge.
     */
    public SkillShieldCharge() {
        skillCode = SHIELD_CHARGE;
        maxCooldown = 17000;
        reqWeapon = Globals.ITEM_SHIELD;
    }

}
