package blockfighter.server.entities.player.skills;

import blockfighter.server.LogicModule;

/**
 *
 * @author Ken Kwan
 */
public class SkillShieldDash extends Skill {

    public SkillShieldDash(final LogicModule l) {
        super(l);
        this.skillCode = SHIELD_DASH;
        this.maxCooldown = 13000;
    }

}
