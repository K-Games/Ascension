package blockfighter.client.entities.player.skills;

import java.awt.Graphics2D;

/**
 *
 * @author Ken Kwan
 */
public class SkillShield6 extends Skill {

    public SkillShield6() {
        skillCode = SHIELD_6;
    }

    @Override
    public void drawInfo(Graphics2D g, int x, int y) {
    }

    @Override
    public String getSkillName() {
        if (isMaxed()) {
            return "Max";
        }
        return "normal";
    }
}
