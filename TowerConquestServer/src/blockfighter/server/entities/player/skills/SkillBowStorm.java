package blockfighter.server.entities.player.skills;

import blockfighter.server.Globals;
import blockfighter.server.LogicModule;

/**
 *
 * @author Ken Kwan
 */
public class SkillBowStorm extends Skill {

    /**
     * Constructor for Bow Skill Arrow Storm.
     */
    public SkillBowStorm(final LogicModule l) {
        super(l);
        this.skillCode = BOW_STORM;
        this.maxCooldown = 20000;
        this.reqWeapon = Globals.ITEM_BOW;
    }

}
