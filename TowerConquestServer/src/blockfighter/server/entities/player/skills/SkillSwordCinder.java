package blockfighter.server.entities.player.skills;

import blockfighter.server.Globals;

/**
 *
 * @author Ken Kwan
 */
public class SkillSwordCinder extends Skill {

    public SkillSwordCinder() {
        skillCode = SWORD_CINDER;
        maxCooldown = 6000;
        reqWeapon = Globals.ITEM_WEAPON;
    }


}
