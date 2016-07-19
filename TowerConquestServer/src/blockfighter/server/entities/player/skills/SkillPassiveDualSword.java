package blockfighter.server.entities.player.skills;

import blockfighter.server.LogicModule;

/**
 *
 * @author Ken Kwan
 */
public class SkillPassiveDualSword extends Skill {

    public SkillPassiveDualSword(final LogicModule l) {
        super(l);
        this.skillCode = PASSIVE_DUALSWORD;
        this.isPassive = true;
    }

}
