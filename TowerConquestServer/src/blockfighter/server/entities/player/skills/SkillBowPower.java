package blockfighter.server.entities.player.skills;

import blockfighter.server.Globals;
import blockfighter.server.LogicModule;

/**
 *
 * @author Ken Kwan
 */
public class SkillBowPower extends Skill {

    /**
     * Constructor for Bow Skill Power Shot.
     */
    public SkillBowPower(final LogicModule l) {
        super(l);
        this.skillCode = BOW_POWER;
        this.maxCooldown = 16000;
        this.reqWeapon = Globals.ITEM_BOW;
    }

}
