package blockfighter.client.entities.skills;

import java.awt.Graphics2D;

/**
 *
 * @author Ken Kwan
 */
public class SkillBowArc extends Skill {

    public SkillBowArc() {
        skillCode = BOW_ARC;
        maxCooldown = 1000;
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
            return "Arc Shot";
        }
        return "Vampiric Shot";
    }
}
