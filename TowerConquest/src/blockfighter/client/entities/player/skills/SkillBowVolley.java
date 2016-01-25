package blockfighter.client.entities.player.skills;

import blockfighter.client.Globals;
import blockfighter.client.entities.items.ItemEquip;
import java.awt.Color;
import java.awt.Graphics2D;

/**
 *
 * @author Ken Kwan
 */
public class SkillBowVolley extends Skill {

    public SkillBowVolley() {
        icon = Globals.SKILL_ICON[BOW_VOLLEY];
        skillCode = BOW_VOLLEY;
        maxCooldown = 7000;
        reqWeapon = Globals.ITEM_BOW;
        skillName = "Volley";
    }

    @Override
    public void drawInfo(Graphics2D g, int x, int y) {
        int boxHeight = (level < 30) ? 235 : 190, boxWidth = 410;
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

        g.drawString("Fire 20 shots over 2 seconds. Can be interrupted.", x + 10, y + 90);

        g.drawString("[Level " + level + "]", x + 10, y + 115);
        g.drawString("Deals " + (75 + 3 * level) + "% damage per hit", x + 10, y + 135);

        if (level < 30) {
            g.drawString("[Level " + (level + 1) + "]", x + 10, y + 160);
            g.drawString("Deals " + (75 + 3 * (level + 1)) + "% damage per hit", x + 10, y + 180);

            g.drawString("[Level 30 Bonus]", x + 10, y + 205);
            g.drawString("Each Critical Hit increases damage by 1% for 4 seconds.", x + 10, y + 225);
        } else {
            g.drawString("[Level 30 Bonus]", x + 10, y + 160);
            g.drawString("Each Critical Hit increases damage by 1% for 4 seconds.", x + 10, y + 180);
        }

    }

}
