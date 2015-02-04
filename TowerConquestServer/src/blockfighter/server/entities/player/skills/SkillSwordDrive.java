package blockfighter.server.entities.player.skills;

import blockfighter.server.Globals;

/**
 *
 * @author Ken Kwan
 */
public class SkillSwordDrive extends Skill {

    public SkillSwordDrive() {
        skillCode = SWORD_DRIVE;
        maxCooldown = 1000;
        reqWeapon = Globals.ITEM_WEAPON;
    }


}
