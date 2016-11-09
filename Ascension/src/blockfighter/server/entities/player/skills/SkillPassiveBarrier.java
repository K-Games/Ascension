package blockfighter.server.entities.player.skills;

import blockfighter.server.LogicModule;

public class SkillPassiveBarrier extends Skill {

    public SkillPassiveBarrier(final LogicModule l) {
        super(l);
        this.skillCode = PASSIVE_BARRIER;
        this.maxCooldown = 30000;
        this.isPassive = true;
    }

}
