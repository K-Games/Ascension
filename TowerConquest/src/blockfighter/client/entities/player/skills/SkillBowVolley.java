package blockfighter.client.entities.player.skills;

import blockfighter.client.Globals;
import blockfighter.client.entities.items.ItemEquip;
import java.awt.Color;
import java.awt.Graphics2D;

public class SkillBowVolley extends Skill {

    public SkillBowVolley() {
        this.icon = Globals.SKILL_ICON[BOW_VOLLEY];
        this.skillCode = BOW_VOLLEY;
        this.maxCooldown = 17000;
        this.reqWeapon = Globals.ITEM_BOW;
        this.skillName = "Vortex Bolts";
    }

    @Override
    public void drawInfo(final Graphics2D g, final int x, final int y) {
        final int boxHeight = (this.level < 30) ? 235 : 190, boxWidth = 410;
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

        g.drawString("Fire 20 bolts over 2 seconds. Can be interrupted.", drawX + 10, drawY + 90);

        g.drawString("[Level " + this.level + "]", drawX + 10, drawY + 115);
        g.drawString("Deals " + (85 + 3 * this.level) + "% damage per hit", drawX + 10, drawY + 135);

        if (this.level < 30) {
            g.drawString("[Level " + (this.level + 1) + "]", drawX + 10, drawY + 160);
            g.drawString("Deals " + (85 + 3 * (this.level + 1)) + "% damage per hit", drawX + 10, drawY + 180);

            g.drawString("[Level 30 Bonus]", drawX + 10, drawY + 205);
            g.drawString("Each Critical Hit increases damage by 1% for 4 seconds.", drawX + 10, drawY + 225);
        } else {
            g.drawString("[Level 30 Bonus]", drawX + 10, drawY + 160);
            g.drawString("Each Critical Hit increases damage by 1% for 4 seconds.", drawX + 10, drawY + 180);
        }

    }

}
