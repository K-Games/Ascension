package blockfighter.client.entities.player.skills;

import blockfighter.client.Globals;
import java.awt.Color;
import java.awt.Graphics2D;

/**
 *
 * @author Ken Kwan
 */
public class SkillPassiveDualSword extends Skill {

    public SkillPassiveDualSword() {
        skillCode = PASSIVE_DUALSWORD;
        skillName = "Dual Wield Mastery";
        icon = Globals.SKILL_ICON[PASSIVE_DUALSWORD];
    }

    @Override
    public void drawInfo(Graphics2D g, int x, int y) {
        int boxHeight = (level < 30) ? 270 : 205, boxWidth = 365;
        if (y + boxHeight > 700) {
            y = 700 - boxHeight;
        }

        if (x + 30 + boxWidth > 1240) {
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

        g.drawString("When equipped with 2 Swords you gain additional", x + 10, y + 90);
        g.drawString("Critical Hit Chance.", x + 10, y + 110);

        g.setColor(new Color(255, 190, 0));
        g.drawString("Assign this passive to a hotkey to gain its effects.", x + 10, y + 130);

        g.setColor(Color.WHITE);
        g.drawString("[Level " + level + "]", x + 10, y + 155);
        g.drawString("Additional " + df.format(6 + level * 0.3) + "% Critical Hit Chance.", x + 10, y + 175);
        g.drawString("Take " + df.format(level) + "% reduced damage.", x + 10, y + 195);
        if (level < 30) {
            g.drawString("[Level " + (level + 1) + "]", x + 10, y + 220);
            g.drawString("Additional " + df.format(6 + (level + 1) * 0.3) + "% Critical Hit Chance.", x + 10, y + 240);
            g.drawString("Take " + df.format(level + 1) + "% reduced damage.", x + 10, y + 260);
        }
    }
}
