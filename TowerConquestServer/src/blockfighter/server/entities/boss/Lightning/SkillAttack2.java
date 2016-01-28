package blockfighter.server.entities.boss.Lightning;

import blockfighter.server.entities.player.skills.Skill;

/**
 *
 * @author Ken Kwan
 */
public class SkillAttack2 extends Skill {

    public SkillAttack2() {
        this.skillCode = BossLightning.SKILL_ATT2;
        this.maxCooldown = 2000;
    }

}
