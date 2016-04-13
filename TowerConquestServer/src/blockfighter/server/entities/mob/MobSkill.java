/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package blockfighter.server.entities.mob;

import blockfighter.server.LogicModule;
import blockfighter.server.entities.player.skills.Skill;

/**
 *
 * @author kenk
 */
public abstract class MobSkill extends Skill {

    public MobSkill(LogicModule l) {
        super(l);
    }

    public void updateSkillUse(Mob mob) {

    }
}
