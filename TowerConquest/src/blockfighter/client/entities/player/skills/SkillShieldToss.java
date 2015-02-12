package blockfighter.client.entities.player.skills;

import blockfighter.client.Globals;
import java.awt.Color;
import java.awt.Graphics2D;

/**
 *
 * @author Ken Kwan
 */
public class SkillShieldToss extends Skill {

    public SkillShieldToss() {
        icon = Globals.SKILL_ICON[Skill.SHIELD_TOSS];
        skillCode = SHIELD_TOSS;
        maxCooldown = 5000;
    }

    @Override
    public void drawInfo(Graphics2D g, int x, int y) {
        int boxHeight = 140, boxWidth = 420;
        if (y + boxHeight > 720) {
            y = 700 - boxHeight;
        }

        if (x + 30 + boxWidth > 1280) {
            x = 1240 - boxWidth;
        }
        g.setColor(new Color(30, 30, 30, 185));
        g.fillRect(x, y, boxWidth, boxHeight);
        g.setColor(Color.BLACK);
        g.drawRect(x, y, boxWidth, boxHeight);
        g.drawRect(x + 1, y + 1, boxWidth - 2, boxHeight - 2);
        g.drawImage(icon, x + 10, y + 10, null);
        g.setColor(Color.WHITE);
        g.setFont(Globals.ARIAL_18PT);
        g.drawString(getSkillName(), x + 80, y + 30);
        g.setFont(Globals.ARIAL_15PT);
        g.drawString("Level: " + level, x + 80, y + 50);
        g.drawString("Cooldown: " + maxCooldown / 1000 + " Seconds", x + 80, y + 70);

        g.drawString("Throw your shield in front of you.", x + 10, y + 90);
        g.drawString("Deals damage multiplied by 80% + " + level + "%(" + (level + 80) + "%) of Defense.", x + 10, y + 110);
        g.drawString("Max: Throws 3 shields.", x + 10, y + 130);
    }

    @Override
    public String getSkillName() {
        if (isMaxed()) {
            return "Guardian's Shield";
        }
        return "Shield Throw";
    }
}
