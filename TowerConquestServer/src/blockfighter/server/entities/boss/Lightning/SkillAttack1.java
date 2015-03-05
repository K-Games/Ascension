package blockfighter.server.entities.boss.Lightning;

import blockfighter.server.entities.player.skills.Skill;

/**
 *
 * @author Ken Kwan
 */
public class SkillAttack1 extends Skill {

    public SkillAttack1() {
        skillCode = BossLightning.SKILL_ATT1;
        maxCooldown = 3000;
    }

}
