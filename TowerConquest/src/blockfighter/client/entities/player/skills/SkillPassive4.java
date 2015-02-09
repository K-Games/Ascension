package blockfighter.client.entities.player.skills;

import java.awt.Graphics2D;

/**
 *
 * @author Ken Kwan
 */
public class SkillPassive4 extends Skill {

    public SkillPassive4() {
        skillCode = PASSIVE_4;
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
