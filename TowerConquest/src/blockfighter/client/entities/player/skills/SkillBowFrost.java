package blockfighter.client.entities.player.skills;

import blockfighter.client.Globals;
import blockfighter.client.entities.items.ItemEquip;
import java.awt.Color;
import java.awt.Graphics2D;

/**
 *
 * @author Ken Kwan
 */
public class SkillBowFrost extends Skill {

    public SkillBowFrost() {
        icon = Globals.SKILL_ICON[BOW_FROST];
        skillCode = BOW_FROST;
        maxCooldown = 20000;
        reqWeapon = Globals.ITEM_BOW;
        skillName = "Frost Bind";
    }

    @Override
    public void drawInfo(Graphics2D g, int x, int y) {
        int boxHeight = (level < 30) ? 255 : 210, boxWidth = 400;
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

        g.drawString("Shoot a frost arrow freezing targets hit for 1.5 second.", x + 10, y + 90);

        g.drawString("[Level " + level + "]", x + 10, y + 115);
        g.drawString("Deals " + (100 + 20 * level) + "% damage.", x + 10, y + 135);
        if (level < 30) {
            g.drawString("[Level " + (level + 1) + "]", x + 10, y + 160);
            g.drawString("Deals " + (100 + 20 * (level + 1)) + "% damage.", x + 10, y + 180);

            g.drawString("[Level 30 Bonus]", x + 10, y + 205);
            g.drawString("Freeze now lasts for 2.5 seconds.", x + 10, y + 225);
            g.drawString("Additional 2 shots that deals 250% damage.", x + 10, y + 245);
        } else {
            g.drawString("[Level 30 Bonus]", x + 10, y + 160);
            g.drawString("Freeze now lasts for 2.5 seconds.", x + 10, y + 180);
            g.drawString("Additional 2 shots that deals 250% damage.", x + 10, y + 200);
        }
    }

}
