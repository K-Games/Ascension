package blockfighter.server.entities.player.skills;

import blockfighter.server.LogicModule;

/**
 *
 * @author Ken Kwan
 */
public class SkillPassiveShieldMastery extends Skill {

    public SkillPassiveShieldMastery(final LogicModule l) {
        super(l);
        this.skillCode = PASSIVE_SHIELDMASTERY;
        this.isPassive = true;
    }

}
