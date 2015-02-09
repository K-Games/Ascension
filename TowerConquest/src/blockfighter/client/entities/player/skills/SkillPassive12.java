package blockfighter.client.entities.player.skills;

import java.awt.Graphics2D;

/**
 *
 * @author Ken Kwan
 */
public class SkillPassive12 extends Skill {

    public SkillPassive12() {
        skillCode = PASSIVE_12;
    }

    @Override
    public void drawInfo(Graphics2D g, int x, int y) {
    }

    @Override
    public String getSkillName() {
        if (isMaxed()) {
            return "Max";
        }
        return "normal";
    }
}
