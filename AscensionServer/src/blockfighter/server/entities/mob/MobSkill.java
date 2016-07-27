package blockfighter.server.entities.mob;

import blockfighter.server.LogicModule;
import blockfighter.server.entities.player.skills.Skill;

public abstract class MobSkill extends Skill {

    public MobSkill(LogicModule l) {
        super(l);
    }

    public void updateSkillUse(Mob mob) {

    }
}
