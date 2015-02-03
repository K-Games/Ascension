package blockfighter.client.entities.skills;

import blockfighter.client.Globals;
import java.awt.Color;
import java.awt.Graphics2D;

/**
 *
 * @author Ken Kwan
 */
public class SkillBowArc extends Skill {

    public SkillBowArc() {
        icon = Globals.SKILL_ICON[BOW_ARC];
        skillCode = BOW_ARC;
        maxCooldown = 500;
    }

    @Override
    public void drawInfo(Graphics2D g, int x, int y) {
        int boxHeight = 140, boxWidth = 390;

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
        g.drawString("Cooldown: 0.5 Second", x + 80, y + 70);

        g.drawString("Fire 3 shots in an arc.", x + 10, y + 90);
        g.drawString("Deal 37 + " + level + "%(" + (37 + level) + "%) damage per hit", x + 10, y + 110);
        g.drawString("Max: Restore 5% damage to HP. Maximum of 10% HP.", x + 10, y + 130);
    }

    @Override
    public String getSkillName() {
        if (isMaxed()) {
            return "Vampiric Shot";
        }
        return "Arc Shot";
    }
}