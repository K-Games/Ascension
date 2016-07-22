package blockfighter.server.entities.player.skills;

import blockfighter.server.LogicModule;

/**
 *
 * @author Ken Kwan
 */
public class SkillPassiveStatic extends Skill {

    public SkillPassiveStatic(final LogicModule l) {
        super(l);
        this.skillCode = PASSIVE_STATIC;
        this.isPassive = true;
    }

}
