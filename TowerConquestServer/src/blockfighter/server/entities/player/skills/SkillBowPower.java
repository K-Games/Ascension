package blockfighter.server.entities.player.skills;

import blockfighter.server.Globals;

/**
 *
 * @author Ken Kwan
 */
public class SkillBowPower extends Skill {

    public SkillBowPower() {
        skillCode = BOW_POWER;
        maxCooldown = 6000;
        reqWeapon = Globals.ITEM_BOW;
    }

}
