package blockfighter.server.entities.player.skills;

import blockfighter.server.LogicModule;

/**
 *
 * @author Ken Kwan
 */
public class SkillShieldFortify extends Skill {

    /**
     * Constructor for Shield Skill Fortify.
     */
    public SkillShieldFortify(final LogicModule l) {
        super(l);
        this.skillCode = SHIELD_FORTIFY;
        this.maxCooldown = 24000;
        // reqWeapon = Globals.ITEM_SHIELD;
    }

}
