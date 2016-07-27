package blockfighter.client.entities.player.skills;

import blockfighter.client.Globals;
import blockfighter.client.entities.items.ItemEquip;
import java.awt.Color;
import java.awt.Graphics2D;

public class SkillSwordCinder extends Skill {

    public SkillSwordCinder() {
        this.icon = Globals.SKILL_ICON[SWORD_CINDER];
        this.skillCode = SWORD_CINDER;
        this.maxCooldown = 20000;
        this.reqWeapon = Globals.ITEM_SWORD;
        this.skillName = "Firebrand";
    }

    @Override
    public void drawInfo(final Graphics2D g, final int x, final int y) {
        final int boxHeight = (this.level < 30) ? 315 : 250, boxWidth = 430;
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

        g.drawString("Deal a single massive damage hit. Enemies hit will burn", drawX + 10, drawY + 90);
        g.drawString("and takes increased damage for 4 seconds.", drawX + 10, drawY + 110);

        g.drawString("[Level " + this.level + "]", drawX + 10, drawY + 135);
        g.drawString("Deals " + (450 + this.level * 20) + "% damage.", drawX + 10, drawY + 155);
        g.drawString("Burning enemies take " + this.level + "% increased damage.", drawX + 10, drawY + 175);
        if (this.level < 30) {
            g.drawString("[Level " + (this.level + 1) + "]", drawX + 10, drawY + 200);
            g.drawString("Deals " + (450 + (this.level + 1) * 20) + "% damage.", drawX + 10, drawY + 220);
            g.drawString("Burning enemies take " + (this.level + 1) + "% increased damage.", drawX + 10, drawY + 240);

            g.drawString("[Level 30 Bonus]", drawX + 10, drawY + 265);
            g.drawString("Burn also deals 1500% damage over 4 seconds(375%/s).", drawX + 10, drawY + 285);
            g.drawString("This attack has 100% Critical Hit Chance.", drawX + 10, drawY + 305);
        } else {
            g.drawString("[Level 30 Bonus]", drawX + 10, drawY + 200);
            g.drawString("Burn also deals 1500% damage over 4 seconds(375%/s).", drawX + 10, drawY + 220);
            g.drawString("This attack has 100% Critical Hit Chance.", drawX + 10, drawY + 240);
        }
    }

}
