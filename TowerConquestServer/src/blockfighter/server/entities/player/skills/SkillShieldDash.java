package blockfighter.server.entities.player.skills;

import blockfighter.server.Globals;

/**
 *
 * @author Ken Kwan
 */
public class SkillShieldDash extends Skill {

    public SkillShieldDash() {
        skillCode = SHIELD_DASH;
        maxCooldown = 2000;
        reqWeapon = Globals.ITEM_SHIELD;
    }

}
