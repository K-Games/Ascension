package blockfighter.server.entities.player.skills;

import blockfighter.server.LogicModule;

public class SkillPassiveWillpower extends Skill {

    public SkillPassiveWillpower(final LogicModule l) {
        super(l);
        this.skillCode = PASSIVE_WILLPOWER;
        this.isPassive = true;
    }

}
