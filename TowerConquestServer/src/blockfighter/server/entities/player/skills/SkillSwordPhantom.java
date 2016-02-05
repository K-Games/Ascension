package blockfighter.server.entities.player.skills;

import blockfighter.server.Globals;
import blockfighter.server.LogicModule;

/**
 *
 * @author Ken Kwan
 */
public class SkillSwordPhantom extends Skill {

    public SkillSwordPhantom(final LogicModule l) {
        super(l);
        this.skillCode = SWORD_PHANTOM;
        this.maxCooldown = 20000;
        this.reqWeapon = Globals.ITEM_SWORD;
    }
}
