package blockfighter.server.entities.player.skills;

import blockfighter.server.Globals;
import blockfighter.server.LogicModule;

/**
 *
 * @author Ken Kwan
 */
public class SkillShieldReflect extends Skill {

    /**
     * Constructor for Shield Skill Reflect.
     */
    public SkillShieldReflect(final LogicModule l) {
        super(l);
        this.skillCode = SHIELD_REFLECT;
        this.maxCooldown = 15000;
        this.reqWeapon = Globals.ITEM_SHIELD;
    }

}
