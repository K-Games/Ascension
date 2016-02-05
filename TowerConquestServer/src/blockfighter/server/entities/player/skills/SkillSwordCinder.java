package blockfighter.server.entities.player.skills;

import blockfighter.server.Globals;
import blockfighter.server.LogicModule;

/**
 *
 * @author Ken Kwan
 */
public class SkillSwordCinder extends Skill {

    /**
     * Constructor for Sword Skill Cinder.
     */
    public SkillSwordCinder(final LogicModule l) {
        super(l);
        this.skillCode = SWORD_CINDER;
        this.maxCooldown = 20000;
        this.reqWeapon = Globals.ITEM_SWORD;
    }

}
