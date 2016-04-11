package blockfighter.server.entities.player.skills;

import blockfighter.server.LogicModule;

/**
 *
 * @author Ken Kwan
 */
public class SkillPassiveTactical extends Skill {

    public SkillPassiveTactical(final LogicModule l) {
        super(l);
        this.skillCode = PASSIVE_TACTICAL;
        this.isPassive = true;
    }

}
