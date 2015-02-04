package blockfighter.server.entities.player.skills;

import blockfighter.server.Globals;

/**
 *
 * @author Ken Kwan
 */
public class SkillSwordVorpal extends Skill {

    public SkillSwordVorpal() {
        skillCode = SWORD_VORPAL;
        maxCooldown = 4000;
        reqWeapon = Globals.ITEM_WEAPON;
    }


}
