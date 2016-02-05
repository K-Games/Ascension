package blockfighter.server.entities.player.skills;

import blockfighter.server.Globals;
import blockfighter.server.LogicModule;

/**
 *
 * @author Ken Kwan
 */
public class SkillSwordGash extends Skill {

    /**
     * Constructor for Sword Skill Gash.
     */
    public SkillSwordGash(final LogicModule l) {
        super(l);
        this.skillCode = SWORD_GASH;
        this.maxCooldown = 500;
        this.reqWeapon = Globals.ITEM_SWORD;
    }

}
