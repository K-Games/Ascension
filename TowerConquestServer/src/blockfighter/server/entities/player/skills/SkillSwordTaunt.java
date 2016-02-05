package blockfighter.server.entities.player.skills;

import blockfighter.server.Globals;
import blockfighter.server.LogicModule;

/**
 *
 * @author Ken Kwan
 */
public class SkillSwordTaunt extends Skill {

    /**
     * Constructor for Sword Skill Taunt.
     *
     * @param l
     */
    public SkillSwordTaunt(final LogicModule l) {
        super(l);
        this.skillCode = SWORD_TAUNT;
        this.maxCooldown = 25000;
        this.reqWeapon = Globals.ITEM_SWORD;
    }

}
