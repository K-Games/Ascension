package blockfighter.client.entities.player.skills;

import blockfighter.client.Globals;
import java.awt.Color;
import java.awt.Graphics2D;

/**
 *
 * @author Ken Kwan
 */
public class SkillPassiveKeenEye extends Skill {

    public SkillPassiveKeenEye() {
        skillCode = PASSIVE_KEENEYE;
        skillName = "Keen Eye";
        icon = Globals.SKILL_ICON[PASSIVE_KEENEYE];
    }

    @Override
    public void drawInfo(Graphics2D g, int x, int y) {
        int boxHeight = (level < 30) ? 210 : 165, boxWidth = 365;
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

        g.drawString("Increases Critical Hit Chance.", x + 10, y + 90);

        g.setColor(new Color(255, 190, 0));
        g.drawString("Assign this passive to a hotkey to gain its effects.", x + 10, y + 110);

        g.setColor(Color.WHITE);
        g.drawString("[Level " + level + "]", x + 10, y + 135);
        g.drawString("Additional " + df.format(1 + level * 0.3) + "% Critical Hit Chance", x + 10, y + 155);

        if (level < 30) {
            g.drawString("[Level " + (level + 1) + "]", x + 10, y + 180);
            g.drawString("Additional " + df.format(1 + (level + 1) * 0.3) + "% Critical Hit Chance", x + 10, y + 200);
        }
    }

}
