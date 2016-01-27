package blockfighter.server.entities.player.skills;

import blockfighter.server.Globals;

/**
 *
 * @author Ken Kwan
 */
public class SkillSwordCinder extends Skill {

    /**
     * Constructor for Sword Skill Cinder.
     */
    public SkillSwordCinder() {
        skillCode = SWORD_CINDER;
        maxCooldown = 20000;
        reqWeapon = Globals.ITEM_SWORD;
    }

}
