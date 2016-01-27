package blockfighter.client.entities.player.skills;

import blockfighter.client.Globals;
import blockfighter.client.entities.items.ItemEquip;
import java.awt.Color;
import java.awt.Graphics2D;

/**
 *
 * @author Ken Kwan
 */
public class SkillShieldReflect extends Skill {

    public SkillShieldReflect() {
        icon = Globals.SKILL_ICON[SHIELD_REFLECT];
        skillCode = SHIELD_REFLECT;
        maxCooldown = 15000;
        reqWeapon = Globals.ITEM_SHIELD;
        skillName = "Reflect Damage";
    }

    @Override
    public void drawInfo(Graphics2D g, int x, int y) {
        int boxHeight = (level < 30) ? 255 : 210, boxWidth = 380;
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
        g.drawString("Level: " + level + " - Requires " + ItemEquip.getItemTypeName(reqWeapon), x + 80, y + 50);
        g.drawString("Cooldown: " + maxCooldown / 1000 + " Seconds", x + 80, y + 70);

        g.drawString("For 3 seconds, when you take damage, you explode", x + 10, y + 90);
        g.drawString("dealing a portion of damage taken.", x + 10, y + 110);

        g.drawString("[Level " + level + "]", x + 10, y + 135);
        g.drawString("Deals " + (40 + level * 2) + "% of damage taken.", x + 10, y + 155);
        if (level < 30) {
            g.drawString("[Level " + (level + 1) + "]", x + 10, y + 180);
            g.drawString("Deals " + (40 + (level + 1) * 2) + "% of damage taken.", x + 10, y + 200);

            g.drawString("[Level 30 Bonus]", x + 10, y + 225);
            g.drawString("You reflect 40% of damage taken by other players.", x + 10, y + 245);
        } else {
            g.drawString("[Level 30 Bonus]", x + 10, y + 180);
            g.drawString("You reflect 40% of damage taken by other players.", x + 10, y + 200);
        }
    }

}
