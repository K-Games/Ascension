package blockfighter.server.entities.player.skills;

import blockfighter.server.Globals;

/**
 *
 * @author Ken Kwan
 */
public class SkillSwordDrive extends Skill {

    /**
     * Constructor for Sword Skill Drive.
     */
    public SkillSwordDrive() {
        skillCode = SWORD_DRIVE;
        maxCooldown = 1000;
        reqWeapon = Globals.ITEM_SWORD;
    }

}
