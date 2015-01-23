package blockfighter.client.entities.skills;

import java.awt.Graphics2D;

/**
 *
 * @author Ken Kwan
 */
public class SkillShieldFortify extends Skill {

    public SkillShieldFortify() {
        skillCode = SHIELD_FORTIFY;
        maxCooldown = 15000;
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
            return "Fortify";
        }
        return "Rekindling Soul";
    }

}
