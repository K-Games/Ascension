package blockfighter.server.entities.player.skills;

import blockfighter.server.Globals;
import blockfighter.server.LogicModule;

/**
 *
 * @author Ken Kwan
 */
public class SkillShieldToss extends Skill {

    /**
     * Constructor for Shield Skill Shield Throw.
     */
    public SkillShieldToss(final LogicModule l) {
        super(l);
        this.skillCode = SHIELD_TOSS;
        this.maxCooldown = 13000;
        this.reqWeapon = Globals.ITEM_SHIELD;
    }

}
