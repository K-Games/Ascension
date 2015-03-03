package blockfighter.client.entities.player.skills;

import blockfighter.client.Globals;
import blockfighter.client.entities.items.ItemEquip;
import java.awt.Color;
import java.awt.Graphics2D;

/**
 *
 * @author Ken Kwan
 */
public class SkillSwordSlash extends Skill {

    public SkillSwordSlash() {
        icon = Globals.SKILL_ICON[SWORD_SLASH];
        skillCode = SWORD_SLASH;
        maxCooldown = 600;
        reqWeapon = Globals.ITEM_SWORD;
        skillName = "Defensive Impact";
        maxSkillName = "Guardian's Might";
    }

    @Override
    public void drawInfo(Graphics2D g, int x, int y) {
        int boxHeight = (level < 30) ? 235 : 190, boxWidth = 280;
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
        g.drawString("Cooldown: 0.6 Second", x + 80, y + 70);

        g.drawString("Slash 3 times.", x + 10, y + 90);

        g.drawString("[Level " + level + "]", x + 10, y + 115);
        g.drawString("Deals " + (4 * level + 100) + "% damage per hit.", x + 10, y + 135);
        if (level < 30) {
            g.drawString("[Level " + (level + 1) + "]", x + 10, y + 160);
            g.drawString("Deals " + (4 * (level + 1) + 100) + "% damage per hit.", x + 10, y + 180);

            g.drawString("[Level 30 Bonus]", x + 10, y + 205);
            g.drawString("Take 10% less damage for 4 seconds.", x + 10, y + 225);
        } else {
            g.drawString("[Level 30 Bonus]", x + 10, y + 160);
            g.drawString("Take 10% less damage for 4 seconds.", x + 10, y + 180);
        }
    }

}
