package blockfighter.server.entities.player.skills;

import blockfighter.server.Globals;
import blockfighter.server.LogicModule;

/**
 *
 * @author Ken Kwan
 */
public class SkillSwordSlash extends Skill {

    /**
     * Constructor for Sword Skill Defensive Impact.
     *
     * @param l
     */
    public SkillSwordSlash(final LogicModule l) {
        super(l);
        this.skillCode = SWORD_SLASH;
        this.maxCooldown = 400;
        this.reqWeapon = Globals.ITEM_SWORD;
    }

}
