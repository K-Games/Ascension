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
    }

    @Override
    public void drawInfo(Graphics2D g, int x, int y) {
        int boxHeight = 200, boxWidth = 430;
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
        g.drawString("Level: " + level + " - Requires " + ItemEquip.getItemTypeName(reqWeapon), x + 80, y + 50);
        g.drawString("Cooldown: " + maxCooldown / 1000 + " Seconds", x + 80, y + 70);

        g.drawString("Stab rapidly 3 times.", x + 10, y + 90);
        g.drawString("Deals 100 + " + 5 * level + "%(" + (5 * level + 100) + "%) damage per hit.", x + 10, y + 110);
        g.drawString("Critical hits deal additional 40 + " + 3 * level + "%(" + (3 * level + 40) + "%) Critical Damage.", x + 10, y + 130);
        g.drawString("Max:", x + 10, y + 150);
        g.drawString("This attack has +30% Critical Hit Chance.", x + 10, y + 170);
        g.drawString("Stab rapidly hit 5 times.", x + 10, y + 190);
    }

    @Override
    public String getSkillName() {
        if (isMaxed()) {
            return "Echoing Fury";
        }
        return "Vorpal Strike";
    }

}
