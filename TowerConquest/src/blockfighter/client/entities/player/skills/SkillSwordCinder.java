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
        maxCooldown = 6000;
        reqWeapon = Globals.ITEM_SWORD;
    }

    @Override
    public void drawInfo(Graphics2D g, int x, int y) {
        int boxHeight = 180, boxWidth = 430;
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

        g.drawString("Deal 450% damage.", x + 10, y + 90);
        g.drawString("Applies Burn - Takes " + level + "% increased damage for 4 seconds.", x + 10, y + 110);
        g.drawString("Max:", x + 10, y + 130);
        g.drawString("Burn also deals 450% over 4 seconds(112.5%/s).", x + 10, y + 150);
        g.drawString("This attack has 100% Critical Hit Chance.", x + 10, y + 170);
    }

    @Override
    public String getSkillName() {
        if (isMaxed()) {
            return "Searing Laceration";
        }
        return "Cinder";
    }

}
