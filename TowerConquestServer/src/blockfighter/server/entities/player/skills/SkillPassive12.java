package blockfighter.server.entities.player.skills;

import blockfighter.server.LogicModule;

/**
 *
 * @author Ken Kwan
 */
public class SkillPassive12 extends Skill {

    public SkillPassive12(final LogicModule l) {
        super(l);
        this.skillCode = PASSIVE_12;
        this.isPassive = true;
    }

}
