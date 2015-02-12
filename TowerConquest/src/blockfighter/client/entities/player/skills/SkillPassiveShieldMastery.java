package blockfighter.client.entities.player.skills;

import blockfighter.client.Globals;
import java.awt.Color;
import java.awt.Graphics2D;

/**
 *
 * @author Ken Kwan
 */
public class SkillPassiveShieldMastery extends Skill {

    public SkillPassiveShieldMastery() {
        skillCode = PASSIVE_SHIELDMASTERY;
    }

    @Override
    public void drawInfo(Graphics2D g, int x, int y) {
        int boxHeight = 160, boxWidth = 365;
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

        g.drawString("When equipped with a Sword and Shield", x + 10, y + 90);
        g.drawString("You gain increased 15% + " + df.format(level * 0.5) + "%(" + df.format(15 + level * 0.5) + "%) damage.", x + 10, y + 110);
        g.drawString("You take 5% + " + df.format(level * 0.5) + "%(" + df.format(5 + level * 0.5) + "%) reduced damage.", x + 10, y + 130);
        g.drawString("Assign this passive to a hotkey to gain its effects.", x + 10, y + 150);
    }

    @Override
    public String getSkillName() {
        return "Defender Mastery";
    }
}
