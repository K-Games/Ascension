package blockfighter.client.entities.skills;

import java.awt.Graphics2D;

/**
 *
 * @author Ken Kwan
 */
public class SkillBowPower extends Skill {

    public SkillBowPower() {
        skillCode = BOW_POWER;
        maxCooldown = 6000;
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
            return "Power Shot";
        }
        return "Obliteration";
    }
}
