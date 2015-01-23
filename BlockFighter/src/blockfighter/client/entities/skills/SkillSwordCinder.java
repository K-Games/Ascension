package blockfighter.client.entities.skills;

import java.awt.Graphics2D;

/**
 *
 * @author Ken Kwan
 */
public class SkillSwordCinder extends Skill {

    public SkillSwordCinder() {
        skillCode = SWORD_CINDER;
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
            return "Searing Laceration";
        }
        return "Cinder";
    }

}
