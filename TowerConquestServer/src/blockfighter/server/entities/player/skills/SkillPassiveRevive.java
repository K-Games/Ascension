package blockfighter.server.entities.player.skills;

import blockfighter.server.LogicModule;

/**
 *
 * @author Ken Kwan
 */
public class SkillPassiveRevive extends Skill {

    public SkillPassiveRevive(final LogicModule l) {
        super(l);
        this.skillCode = PASSIVE_REVIVE;
        this.maxCooldown = 120000;
    }

}
