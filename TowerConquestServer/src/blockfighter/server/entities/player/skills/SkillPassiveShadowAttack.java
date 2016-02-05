package blockfighter.server.entities.player.skills;

import blockfighter.server.LogicModule;

/**
 *
 * @author Ken Kwan
 */
public class SkillPassiveShadowAttack extends Skill {

    public SkillPassiveShadowAttack(final LogicModule l) {
        super(l);
        this.skillCode = PASSIVE_SHADOWATTACK;
        this.maxCooldown = 200;
    }

}
