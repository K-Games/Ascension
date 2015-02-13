package blockfighter.client.entities.player.skills;

import blockfighter.client.Globals;
import java.awt.Color;
import java.awt.Graphics2D;

/**
 *
 * @author Ken Kwan
 */
public class SkillSwordSlash extends Skill {

    public SkillSwordSlash() {
        icon = Globals.SKILL_ICON[SWORD_SLASH];
        skillCode = SWORD_SLASH;
        maxCooldown = 1000;
    }

    @Override
    public void drawInfo(Graphics2D g, int x, int y) {
        int boxHeight = 140, boxWidth = 320;
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
        g.drawString("Cooldown: " + maxCooldown / 1000 + " Second", x + 80, y + 70);

        g.drawString("Slash 3 times.", x + 10, y + 90);
        g.drawString("Deals 100 + " + 4 * level + "%(" + (4 * level + 100) + "%) damage per hit.", x + 10, y + 110);
        g.drawString("Max: Take 10% less damage for 4 seconds.", x + 10, y + 130);
    }

    @Override
    public String getSkillName() {
        if (isMaxed()) {
            return "Guardian's Might";
        }
        return "Defensive Impact";
    }

}