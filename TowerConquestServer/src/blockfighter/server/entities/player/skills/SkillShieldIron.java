package blockfighter.server.entities.player.skills;

import blockfighter.server.Globals;

/**
 *
 * @author Ken Kwan
 */
public class SkillShieldIron extends Skill {

    /**
     * Constructor for Shield Skill Iron Fortress.
     */
    public SkillShieldIron() {
        skillCode = SHIELD_IRON;
        maxCooldown = 13000;
        reqWeapon = Globals.ITEM_SHIELD;
    }

}
