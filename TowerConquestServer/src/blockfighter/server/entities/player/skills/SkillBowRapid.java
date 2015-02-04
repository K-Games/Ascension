package blockfighter.server.entities.player.skills;

import blockfighter.server.Globals;

/**
 *
 * @author Ken Kwan
 */
public class SkillBowRapid extends Skill {

    public SkillBowRapid() {
        skillCode = BOW_RAPID;
        maxCooldown = 1000;
        reqWeapon = Globals.ITEM_BOW;
    }

}
