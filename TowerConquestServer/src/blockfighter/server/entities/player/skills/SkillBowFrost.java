package blockfighter.server.entities.player.skills;

import blockfighter.server.Globals;

/**
 *
 * @author Ken Kwan
 */
public class SkillBowFrost extends Skill {

    public SkillBowFrost() {
        skillCode = BOW_FROST;
        maxCooldown = 20000;
        reqWeapon = Globals.ITEM_BOW;
    }

}
