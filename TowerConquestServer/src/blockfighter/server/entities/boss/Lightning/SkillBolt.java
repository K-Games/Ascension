package blockfighter.server.entities.boss.Lightning;

import blockfighter.server.entities.player.skills.Skill;

/**
 *
 * @author Ken Kwan
 */
public class SkillBolt extends Skill {

    public SkillBolt() {
        skillCode = BossLightning.SKILL_BOLT;
        maxCooldown = 20000;
    }

}
