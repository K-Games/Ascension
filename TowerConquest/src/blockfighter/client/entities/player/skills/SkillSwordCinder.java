package blockfighter.client.entities.player.skills;

import blockfighter.client.Globals;
import blockfighter.client.entities.items.ItemEquip;
import java.awt.Color;
import java.awt.Graphics2D;

/**
 *
 * @author Ken Kwan
 */
public class SkillSwordCinder extends Skill {

    public SkillSwordCinder() {
        icon = Globals.SKILL_ICON[SWORD_CINDER];
        skillCode = SWORD_CINDER;
        maxCooldown = 20000;
        reqWeapon = Globals.ITEM_SWORD;
        skillName = "Firebrand";
    }

    @Override
    public void drawInfo(Graphics2D g, int x, int y) {
        int boxHeight = (level < 30) ? 315 : 250, boxWidth = 430;
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

        g.drawString("Deal a single massive damage hit. Enemies hit will burn", x + 10, y + 90);
        g.drawString("and takes increased damage for 4 seconds.", x + 10, y + 110);

        g.drawString("[Level " + level + "]", x + 10, y + 135);
        g.drawString("Deals " + (450 + level * 20) + "% damage.", x + 10, y + 155);
        g.drawString("Burning enemies take " + level + "% increased damage.", x + 10, y + 175);
        if (level < 30) {
            g.drawString("[Level " + (level + 1) + "]", x + 10, y + 200);
            g.drawString("Deals " + (450 + (level + 1) * 20) + "% damage.", x + 10, y + 220);
            g.drawString("Burning enemies take " + (level + 1) + "% increased damage.", x + 10, y + 240);

            g.drawString("[Level 30 Bonus]", x + 10, y + 265);
            g.drawString("Burn also deals 1500% damage over 4 seconds(375%/s).", x + 10, y + 285);
            g.drawString("This attack has 100% Critical Hit Chance.", x + 10, y + 305);
        } else {
            g.drawString("[Level 30 Bonus]", x + 10, y + 200);
            g.drawString("Burn also deals 1500% damage over 4 seconds(375%/s).", x + 10, y + 220);
            g.drawString("This attack has 100% Critical Hit Chance.", x + 10, y + 240);
        }
    }

}
