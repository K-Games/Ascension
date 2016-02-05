package blockfighter.server.entities.player.skills;

import blockfighter.server.Globals;
import blockfighter.server.LogicModule;

/**
 *
 * @author Ken Kwan
 */
public class SkillBowFrost extends Skill {

    /**
     * Constructor for Bow Skill Frost Bind.
     *
     * @param l
     */
    public SkillBowFrost(final LogicModule l) {
        super(l);
        this.skillCode = BOW_FROST;
        this.maxCooldown = 22000;
        this.reqWeapon = Globals.ITEM_BOW;
    }

}
