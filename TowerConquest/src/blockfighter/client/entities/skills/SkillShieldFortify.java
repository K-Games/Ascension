package blockfighter.client.entities.skills;

import blockfighter.client.Globals;
import java.awt.Color;
import java.awt.Graphics2D;

/**
 *
 * @author Ken Kwan
 */
public class SkillShieldFortify extends Skill {

    public SkillShieldFortify() {
        skillCode = SHIELD_FORTIFY;
        maxCooldown = 15000;
    }

    @Override
    public void drawInfo(Graphics2D g, int x, int y) {
        int boxHeight = 120, boxWidth = 350;

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

        g.drawString("Increase Armor by 20 + " + level + "%(" + (level + 20) + "%) for 5 seconds.", x + 10, y + 90);
        g.drawString("Max: Restore 20% HP over 5 seconds.", x + 10, y + 110);
    }

    @Override
    public String getSkillName() {
        if (isMaxed()) {
            return "Rekindling Soul";
        }
        return "Fortify";
    }

}
