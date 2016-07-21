package blockfighter.server.entities.player.skills;

import blockfighter.server.LogicModule;

/**
 *
 * @author Ken Kwan
 */
public class SkillPassiveTough extends Skill {

    public SkillPassiveTough(final LogicModule l) {
        super(l);
        this.skillCode = PASSIVE_TOUGH;
        this.isPassive = true;
    }

}
