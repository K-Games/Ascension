package blockfighter.server.entities.player.skills;

import blockfighter.server.Globals;

/**
 *
 * @author Ken Kwan
 */
public class SkillSwordVorpal extends Skill {

    /**
     * Constructor for Sword Skill Vorpal Strike.
     */
    public SkillSwordVorpal() {
        this.skillCode = SWORD_VORPAL;
        this.maxCooldown = 14000;
        this.reqWeapon = Globals.ITEM_SWORD;
    }

}
