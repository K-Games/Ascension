package blockfighter.client.entities.skills;

import blockfighter.client.Globals;
import java.awt.Color;
import java.awt.Graphics2D;

/**
 *
 * @author Ken Kwan
 */
public class SkillBowPower extends Skill {

    public SkillBowPower() {
        skillCode = BOW_POWER;
        maxCooldown = 6000;
    }

    @Override
    public void drawInfo(Graphics2D g, int x, int y) {
        int boxHeight = 180, boxWidth = 380;

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
        
        g.drawString("Charges a shot for 0.8 seconds. Can be interrupted.", x + 10, y + 90);
        g.drawString("Deal 500 + " + 100 * level + "%(" + (500 + 100 * level) + "%) damage.", x + 10, y + 110);
        g.drawString("Max:", x + 10, y + 130);
        g.drawString("Can no longer be interrupted.", x + 10, y + 150);
        g.drawString("Critical Hits deal +300% Critical Hit damage.", x + 10, y + 170);
    }

    @Override
    public String getSkillName() {
        if (isMaxed()) {
            return "Power Shot";
        }
        return "Obliteration";
    }
}
