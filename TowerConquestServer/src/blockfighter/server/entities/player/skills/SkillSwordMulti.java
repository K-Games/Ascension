package blockfighter.server.entities.player.skills;

import blockfighter.server.Globals;
import blockfighter.server.LogicModule;

/**
 *
 * @author Ken Kwan
 */
public class SkillSwordMulti extends Skill {

    /**
     * Constructor for Sword Skill Whirlwind.
     *
     * @param l
     */
    public SkillSwordMulti(final LogicModule l) {
        super(l);
        this.skillCode = SWORD_MULTI;
        this.maxCooldown = 18000;
        this.reqWeapon = Globals.ITEM_SWORD;
    }

}
