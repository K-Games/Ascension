package blockfighter.client.entities.player.skills;

import blockfighter.client.Globals;
import java.awt.Color;
import java.awt.Graphics2D;

/**
 *
 * @author Ken Kwan
 */
public class SkillShieldDash extends Skill {

    public SkillShieldDash() {
        icon = Globals.SKILL_ICON[SHIELD_DASH];
        skillCode = SHIELD_DASH;
        maxCooldown = 2000;
        //reqWeapon = Globals.ITEM_SHIELD;
        skillName = "Dash";
        maxSkillName = "Unrivaled Haste";
    }

    @Override
    public void drawInfo(Graphics2D g, int x, int y) {
        int boxHeight = (level < 30) ? 255 : 210, boxWidth = 310;
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

        g.drawString("Dash a short distance over 0.25 seconds.", x + 10, y + 90);
        g.drawString("Increases damage dealt for 5 seconds.", x + 10, y + 110);

        g.drawString("[Level " + level + "]", x + 10, y + 135);
        g.drawString("Increases damage dealt by " + df.format(1 + level * 0.3) + "%.", x + 10, y + 155);
        if (level < 30) {
            g.drawString("[Level " + (level + 1) + "]", x + 10, y + 180);
            g.drawString("Increases damage dealt by " + df.format(1 + (level + 1) * 0.3) + "%.", x + 10, y + 200);

            g.drawString("[Level 30 Bonus]", x + 10, y + 225);
            g.drawString("Invulnerable during dash.", x + 10, y + 245);
        } else {
            g.drawString("[Level 30 Bonus]", x + 10, y + 180);
            g.drawString("Invulnerable during dash.", x + 10, y + 200);
        }
    }

}
