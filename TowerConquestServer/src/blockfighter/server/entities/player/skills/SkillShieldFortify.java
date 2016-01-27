package blockfighter.server.entities.player.skills;

/**
 *
 * @author Ken Kwan
 */
public class SkillShieldFortify extends Skill {

    /**
     * Constructor for Shield Skill Fortify.
     */
    public SkillShieldFortify() {
        skillCode = SHIELD_FORTIFY;
        maxCooldown = 24000;
        //reqWeapon = Globals.ITEM_SHIELD;
    }

}
