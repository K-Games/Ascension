package blockfighter.client.entities.skills;

import java.awt.Graphics2D;

/**
 *
 * @author Ken Kwan
 */
public class SkillBowStorm extends Skill {

    public SkillBowStorm() {
        skillCode = BOW_STORM;
        maxCooldown = 13000;
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
            return "Arrow Storm";
        }
        return "Hailing Bombardment";
    }
}
