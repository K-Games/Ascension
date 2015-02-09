package blockfighter.client.entities.player.skills;

import java.awt.Graphics2D;

/**
 *
 * @author Ken Kwan
 */
public class SkillPassive10 extends Skill {

    public SkillPassive10() {
        skillCode = PASSIVE_10;
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
