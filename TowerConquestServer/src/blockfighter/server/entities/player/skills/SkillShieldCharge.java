package blockfighter.server.entities.player.skills;

/**
 *
 * @author Ken Kwan
 */
public class SkillShieldCharge extends Skill {

    /**
     * Constructor for Shield Skill Charge.
     */
    public SkillShieldCharge() {
        skillCode = SHIELD_CHARGE;
        maxCooldown = 8000;
    }

}
