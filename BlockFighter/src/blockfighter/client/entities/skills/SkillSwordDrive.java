package blockfighter.client.entities.skills;

import java.awt.Graphics2D;

/**
 *
 * @author Ken Kwan
 */
public class SkillSwordDrive extends Skill {

    public SkillSwordDrive() {
        skillCode = SWORD_DRIVE;
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
            return "Glaring Blow";
        }
        return "Drive";
    }

}
