package blockfighter.server.entities.player.skills;

import blockfighter.server.Globals;
import blockfighter.server.LogicModule;

/**
 *
 * @author Ken Kwan
 */
public class SkillShieldCharge extends Skill {

    /**
     * Constructor for Shield Skill Charge.
     */
    public SkillShieldCharge(final LogicModule l) {
        super(l);
        this.skillCode = SHIELD_CHARGE;
        this.maxCooldown = 17000;
        this.reqWeapon = Globals.ITEM_SHIELD;
    }

}
