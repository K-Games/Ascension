package blockfighter.client.entities.player.skills;

import blockfighter.client.Globals;
import java.awt.Color;
import java.awt.Graphics2D;

/**
 *
 * @author Ken Kwan
 */
public class SkillPassiveBarrier extends Skill {

    public SkillPassiveBarrier() {
        skillCode = PASSIVE_BARRIER;
        maxCooldown = 30000;
        skillName = "Barrier";
        icon = Globals.SKILL_ICON[PASSIVE_BARRIER];
    }

    @Override
    public void drawInfo(Graphics2D g, int x, int y) {
        int boxHeight = (level < 30) ? 230 : 185, boxWidth = 385;
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
        g.drawString("Cooldown: " + maxCooldown / 1000 + " Seconds", x + 80, y + 70);

        g.drawString("After taking damage over 50% of HP, gain a barrier", x + 10, y + 90);
        g.drawString("that can absorb up to a percentage of HP in damage.", x + 10, y + 110);

        g.setColor(new Color(255, 190, 0));
        g.drawString("Assign this passive to a hotkey to gain its effects.", x + 10, y + 130);

        g.setColor(Color.WHITE);
        g.drawString("[Level " + level + "]", x + 10, y + 155);
        g.drawString("Absorbs up to " + df.format(10 + level * 0.5) + "% of HP.", x + 10, y + 175);

        if (level < 30) {
            g.drawString("[Level " + (level + 1) + "]", x + 10, y + 200);
            g.drawString("Absorbs up to " + (10 + (level + 1) * 0.5) + "% of HP.", x + 10, y + 220);
        }
    }
}
