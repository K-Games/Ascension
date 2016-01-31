package blockfighter.server.entities.player.skills;

import blockfighter.server.Globals;

/**
 *
 * @author Ken Kwan
 */
public class SkillSwordPhantom extends Skill {

    public SkillSwordPhantom() {
        this.skillCode = SWORD_PHANTOM;
        this.maxCooldown = 20000;
        this.reqWeapon = Globals.ITEM_SWORD;
    }
}
