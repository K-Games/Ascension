package blockfighter.server.entities.player.skills;

import blockfighter.server.Globals;
import blockfighter.server.LogicModule;

/**
 *
 * @author Ken Kwan
 */
public class SkillBowArc extends Skill {

    /**
     * Constructor for Bow Skill Arcshot.
     *
     * @param l Logic(room) this skill owner's belong to
     */
    public SkillBowArc(final LogicModule l) {
        super(l);
        this.skillCode = BOW_ARC;
        this.maxCooldown = 500;
        this.reqWeapon = Globals.ITEM_BOW;
    }

}
