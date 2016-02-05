package blockfighter.server.entities.player.skills;

import blockfighter.server.Globals;
import blockfighter.server.LogicModule;

/**
 *
 * @author Ken Kwan
 */
public class SkillBowRapid extends Skill {

    /**
     * Constructor for Bow Skill Rapid Fire.
     */
    public SkillBowRapid(final LogicModule l) {
        super(l);
        this.skillCode = BOW_RAPID;
        this.maxCooldown = 700;
        this.reqWeapon = Globals.ITEM_BOW;
    }

}
