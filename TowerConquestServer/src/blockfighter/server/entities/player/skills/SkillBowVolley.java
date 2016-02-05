package blockfighter.server.entities.player.skills;

import blockfighter.server.Globals;
import blockfighter.server.LogicModule;

/**
 *
 * @author Ken Kwan
 */
public class SkillBowVolley extends Skill {

    /**
     * Constructor for Bow Skill Volley.
     */
    public SkillBowVolley(final LogicModule l) {
        super(l);
        this.skillCode = BOW_VOLLEY;
        this.maxCooldown = 17000;
        this.reqWeapon = Globals.ITEM_BOW;
    }

}
