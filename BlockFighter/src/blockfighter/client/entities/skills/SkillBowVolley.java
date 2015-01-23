package blockfighter.client.entities.skills;

import java.awt.Graphics2D;

/**
 *
 * @author Ken Kwan
 */
public class SkillBowVolley extends Skill {

    public SkillBowVolley() {
        skillCode = BOW_VOLLEY;
        maxCooldown = 7000;
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
            return "Volley";
        }
        return "Merciless Reign";
    }
}