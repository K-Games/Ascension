package blockfighter.server.entities.player.skills;

import blockfighter.server.Globals;

/**
 *
 * @author Ken Kwan
 */
public class SkillSwordTaunt extends Skill {

    public SkillSwordTaunt() {
        skillCode = SWORD_TAUNT;
        maxCooldown = 20000;
        reqWeapon = Globals.ITEM_WEAPON;
    }

}
