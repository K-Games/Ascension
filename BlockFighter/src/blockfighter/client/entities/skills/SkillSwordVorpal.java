package blockfighter.client.entities.skills;

import java.awt.Graphics2D;

/**
 *
 * @author Ken Kwan
 */
public class SkillSwordVorpal extends Skill {

    public SkillSwordVorpal() {
        skillCode = SWORD_VORPAL;
        maxCooldown = 4000;
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
            return "Echoing Fury";
        }
        return "Vorpal Strike";
    }

}
