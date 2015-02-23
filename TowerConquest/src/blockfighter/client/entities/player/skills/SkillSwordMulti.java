package blockfighter.client.entities.player.skills;

import blockfighter.client.Globals;
import blockfighter.client.entities.items.ItemEquip;
import java.awt.Color;
import java.awt.Graphics2D;

/**
 *
 * @author Ken Kwan
 */
public class SkillSwordMulti extends Skill {

    public SkillSwordMulti() {
        icon = Globals.SKILL_ICON[SWORD_MULTI];
        skillCode = SWORD_MULTI;
        maxCooldown = 6000;
        reqWeapon = Globals.ITEM_SWORD;
        skillName = "Whirlwind";
        maxSkillName = "Relentless Barrage";
    }

    @Override
    public void drawInfo(Graphics2D g, int x, int y) {
        int boxHeight = (level < 30) ? 255 : 210, boxWidth = 300;
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

        g.drawString("Perform numerous consecutive attacks.", x + 10, y + 90);
        g.drawString("Deals 100% damage per hit.", x + 10, y + 110);

        g.drawString("[Level " + level + "]", x + 10, y + 135);
        g.drawString("Perform " + (6 + level) + " attacks.", x + 10, y + 155);
        if (level < 30) {
            g.drawString("[Level " + (level + 1) + "]", x + 10, y + 180);
            g.drawString("Perform " + (6 + (level + 1)) + " attacks.", x + 10, y + 200);

            g.drawString("[Level 30 Bonus]", x + 10, y + 225);
            g.drawString("Invulnerable during the attack duration.", x + 10, y + 245);
        } else {
            g.drawString("[Level 30 Bonus]", x + 10, y + 180);
            g.drawString("Invulnerable during the attack duration.", x + 10, y + 200);
        }
    }

}
