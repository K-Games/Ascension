package blockfighter.client.entities.skills;

import java.awt.Graphics2D;

/**
 *
 * @author Ken Kwan
 */
public class SkillSwordMulti extends Skill {

    public SkillSwordMulti() {
        skillCode = SWORD_MULTI;
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
            return "Relentless Barrage";
        }
        return "Multi-Strike";
    }

}
