package blockfighter.client.entities.player.skills;

import blockfighter.client.Globals;
import blockfighter.client.entities.items.ItemEquip;
import java.awt.Color;
import java.awt.Graphics2D;

/**
 *
 * @author Ken Kwan
 */
public class SkillSwordVorpal extends Skill {

    public SkillSwordVorpal() {
        icon = Globals.SKILL_ICON[SWORD_VORPAL];
        skillCode = SWORD_VORPAL;
        maxCooldown = 4000;
        reqWeapon = Globals.ITEM_SWORD;
        skillName = "Vorpal Strike";
        maxSkillName = "Echoing Fury";
    }

    @Override
    public void drawInfo(Graphics2D g, int x, int y) {
        int boxHeight = (level < 30) ? 295 : 230, boxWidth = 370;
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

        g.drawString("Stab rapidly 3 times.", x + 10, y + 90);

        g.drawString("[Level " + level + "]", x + 10, y + 115);
        g.drawString("Deals " + (6 * level + 100) + "% damage per hit.", x + 10, y + 135);
        g.drawString("Critical Hits deal additional +" + (3 * level + 40) + "% Critical Damage.", x + 10, y + 155);
        if (level < 30) {
            g.drawString("[Level " + (level + 1) + "]", x + 10, y + 180);
            g.drawString("Deals " + (6 * (level + 1) + 100) + "% damage per hit.", x + 10, y + 200);
            g.drawString("Critical Hits deal additional +" + (3 * (level + 1) + 40) + "% Critical Damage.", x + 10, y + 220);

            g.drawString("[Level 30 Bonus]", x + 10, y + 245);
            g.drawString("This attack has +30% Critical Hit Chance.", x + 10, y + 265);
            g.drawString("Stab rapidly hit 5 times.", x + 10, y + 285);
        } else {
            g.drawString("[Level 30 Bonus]", x + 10, y + 180);
            g.drawString("This attack has +30% Critical Hit Chance.", x + 10, y + 200);
            g.drawString("Stab rapidly hit 5 times.", x + 10, y + 220);
        }
    }

}
