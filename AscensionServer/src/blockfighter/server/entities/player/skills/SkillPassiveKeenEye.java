package blockfighter.server.entities.player.skills;

import blockfighter.server.LogicModule;

public class SkillPassiveKeenEye extends Skill {

    public SkillPassiveKeenEye(final LogicModule l) {
        super(l);
        this.skillCode = PASSIVE_KEENEYE;
        this.isPassive = true;
    }

}
