package blockfighter.client.entities.player.skills;

import blockfighter.client.Globals;
import blockfighter.client.entities.items.ItemEquip;
import java.awt.Color;
import java.awt.Graphics2D;

public class SkillBowFrost extends Skill {

    public SkillBowFrost() {
        this.icon = Globals.SKILL_ICON[BOW_FROST];
        this.skillCode = BOW_FROST;
        this.maxCooldown = 22000;
        this.reqWeapon = Globals.ITEM_BOW;
        this.skillName = "Frost Bind";
    }

    @Override
    public void drawInfo(final Graphics2D g, final int x, final int y) {
        final int boxHeight = (this.level < 30) ? 255 : 210, boxWidth = 400;
        int drawX = x, drawY = y;
        if (drawY + boxHeight > 700) {
            drawY = 700 - boxHeight;
        }

        if (drawX + 30 + boxWidth > 1240) {
            drawX = 1240 - boxWidth;
        }
        g.setColor(new Color(30, 30, 30, 185));
        g.fillRect(drawX, drawY, boxWidth, boxHeight);
        g.setColor(Color.BLACK);
        g.drawRect(drawX, drawY, boxWidth, boxHeight);
        g.drawRect(drawX + 1, drawY + 1, boxWidth - 2, boxHeight - 2);
        g.drawImage(this.icon, drawX + 10, drawY + 10, null);
        g.setColor(Color.WHITE);
        g.setFont(Globals.ARIAL_18PT);
        g.drawString(getSkillName(), drawX + 80, drawY + 30);
        g.setFont(Globals.ARIAL_15PT);
        g.drawString("Level: " + this.level + " - Requires " + ItemEquip.getItemTypeName(this.reqWeapon), drawX + 80, drawY + 50);
        g.drawString("Cooldown: " + this.maxCooldown / 1000 + " Seconds", drawX + 80, drawY + 70);

        g.drawString("Shoot a frost arrow freezing targets hit for 1.5 second.", drawX + 10, drawY + 90);

        g.drawString("[Level " + this.level + "]", drawX + 10, drawY + 115);
        g.drawString("Deals " + (100 + 20 * this.level) + "% damage.", drawX + 10, drawY + 135);
        if (this.level < 30) {
            g.drawString("[Level " + (this.level + 1) + "]", drawX + 10, drawY + 160);
            g.drawString("Deals " + (100 + 20 * (this.level + 1)) + "% damage.", drawX + 10, drawY + 180);

            g.drawString("[Level 30 Bonus]", drawX + 10, drawY + 205);
            g.drawString("Freeze now lasts for 2.5 seconds.", drawX + 10, drawY + 225);
            g.drawString("Additional 2 shots that deals 250% damage.", drawX + 10, drawY + 245);
        } else {
            g.drawString("[Level 30 Bonus]", drawX + 10, drawY + 160);
            g.drawString("Freeze now lasts for 2.5 seconds.", drawX + 10, drawY + 180);
            g.drawString("Additional 2 shots that deals 250% damage.", drawX + 10, drawY + 200);
        }
    }

}