package blockfighter.client.entities.skills;

import java.awt.Graphics2D;

/**
 *
 * @author Ken Kwan
 */
public class SkillSwordSlash extends Skill {

    public SkillSwordSlash() {
        skillCode = SWORD_SLASH;
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
            return "Guardian's Might";
        }
        return "Defensive Impact";
    }

}
