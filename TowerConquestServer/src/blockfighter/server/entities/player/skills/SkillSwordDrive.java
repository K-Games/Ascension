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
        this.skillCode = SWORD_DRIVE;
        this.maxCooldown = 500;
        this.reqWeapon = Globals.ITEM_SWORD;
    }

}
