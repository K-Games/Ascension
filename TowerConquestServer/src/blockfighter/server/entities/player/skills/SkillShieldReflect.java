package blockfighter.server.entities.player.skills;

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
    }

}
