package blockfighter.server.entities.player.skills;

import blockfighter.server.Globals;

/**
 *
 * @author Ken Kwan
 */
public class SkillShieldReflect extends Skill {

    /**
     * Constructor for Shield Skill Reflect.
     */
    public SkillShieldReflect() {
        skillCode = SHIELD_REFLECT;
        maxCooldown = 15000;
        reqWeapon = Globals.ITEM_SHIELD;
    }

}
