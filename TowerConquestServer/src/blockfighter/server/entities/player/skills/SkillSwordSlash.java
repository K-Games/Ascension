package blockfighter.server.entities.player.skills;

import blockfighter.server.Globals;

/**
 *
 * @author Ken Kwan
 */
public class SkillSwordSlash extends Skill {

    /**
     * Constructor for Sword Skill Defensive Impact.
     */
    public SkillSwordSlash() {
        skillCode = SWORD_SLASH;
        maxCooldown = 400;
        reqWeapon = Globals.ITEM_SWORD;
    }

}
