package blockfighter.client.entities.player.skills;

import blockfighter.client.Globals;
import blockfighter.client.entities.items.ItemEquip;
import java.awt.Color;
import java.awt.Graphics2D;

/**
 *
 * @author Ken Kwan
 */
public class SkillSwordTaunt extends Skill {

    public SkillSwordTaunt() {
        icon = Globals.SKILL_ICON[SWORD_TAUNT];
        skillCode = SWORD_TAUNT;
        maxCooldown = 20000;
        reqWeapon = Globals.ITEM_SWORD;
        skillName = "Taunt";
        maxSkillName = "Roaring Challenge";
    }

    @Override
    public void drawInfo(Graphics2D g, int x, int y) {
        int boxHeight = (level < 30) ? 255 : 210, boxWidth = 385;
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

        g.drawString("Deal damage and aggro equal to 100x damage dealt.", x + 10, y + 90);

        g.drawString("[Level " + level + "]", x + 10, y + 115);
        g.drawString("Deals " + (10 * level + 250) + "% damage.", x + 10, y + 135);
        if (level < 30) {
            g.drawString("[Level " + (level + 1) + "]", x + 10, y + 160);
            g.drawString("Deals " + (10 * (level + 1) + 250) + "% damage.", x + 10, y + 180);

            g.drawString("[Level 30 Bonus]", x + 10, y + 205);
            g.drawString("Take 20% less damage for 10 seconds.", x + 10, y + 225);
            g.drawString("Deal 20% increased damage for 10 seconds.", x + 10, y + 245);
        } else {
            g.drawString("[Level 30 Bonus]", x + 10, y + 160);
            g.drawString("Take 20% less damage for 10 seconds.", x + 10, y + 180);
            g.drawString("Deal 20% increased damage for 10 seconds.", x + 10, y + 200);
        }
    }

}
