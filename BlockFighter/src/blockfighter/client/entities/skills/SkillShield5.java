package blockfighter.client.entities.skills;

import java.awt.Graphics2D;

/**
 *
 * @author Ken Kwan
 */
public class SkillShield5 extends Skill {

    public SkillShield5() {
        skillCode = SHIELD_5;
    }

    @Override
    public void draw(Graphics2D g, int x, int y) {
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
