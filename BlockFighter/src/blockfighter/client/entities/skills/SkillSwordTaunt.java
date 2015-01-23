package blockfighter.client.entities.skills;

import java.awt.Graphics2D;

/**
 *
 * @author Ken Kwan
 */
public class SkillSwordTaunt extends Skill {

    public SkillSwordTaunt() {
        skillCode = SWORD_TAUNT;
        maxCooldown = 20000;
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
            return "Taunt";
        }
        return "Roaring Challenge";
    }
}
