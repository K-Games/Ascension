package blockfighter.client.entities.player.skills;

import blockfighter.client.Globals;
import java.awt.Color;
import java.awt.Graphics2D;

/**
 *
 * @author Ken Kwan
 */
public class SkillPassiveShadowAttack extends Skill {

    public SkillPassiveShadowAttack() {
        skillName = "Piercing Shadows";
        skillCode = PASSIVE_SHADOWATTACK;
        maxCooldown = 200;
        icon = Globals.SKILL_ICON[PASSIVE_SHADOWATTACK];
    }

    @Override
    public void drawInfo(Graphics2D g, int x, int y) {
        int boxHeight = (level < 30) ? 230 : 185, boxWidth = 415;
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
        g.drawString("Cooldown: 0.2 Second", x + 80, y + 70);

        g.drawString("When you deal damage there is a chance a shadow blade", x + 10, y + 90);
        g.drawString("pierces that same target for 50% of the damage dealt.", x + 10, y + 110);

        g.setColor(new Color(255, 190, 0));
        g.drawString("Assign this passive to a hotkey to gain its effects.", x + 10, y + 130);

        g.setColor(Color.WHITE);
        g.drawString("[Level " + level + "]", x + 10, y + 155);
        g.drawString((20 + level) + "% chance to summon a shadow blade.", x + 10, y + 175);

        if (level < 30) {
            g.drawString("[Level " + (level + 1) + "]", x + 10, y + 200);
            g.drawString((20 + (level + 1)) + "% chance to summon a shadow blade.", x + 10, y + 220);
        }
    }
}
