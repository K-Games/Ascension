package blockfighter.server.entities.player.skills;

import blockfighter.server.LogicModule;

/**
 *
 * @author Ken Kwan
 */
public class SkillPassiveVitalHit extends Skill {

    public SkillPassiveVitalHit(final LogicModule l) {
        super(l);
        this.skillCode = PASSIVE_VITALHIT;
        this.isPassive = true;
    }

}
