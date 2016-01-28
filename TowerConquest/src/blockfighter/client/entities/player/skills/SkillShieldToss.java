package blockfighter.client.entities.player.skills;

import blockfighter.client.Globals;
import blockfighter.client.entities.items.ItemEquip;
import java.awt.Color;
import java.awt.Graphics2D;

/**
 *
 * @author Ken Kwan
 */
public class SkillShieldToss extends Skill {

    public SkillShieldToss() {
        this.icon = Globals.SKILL_ICON[SHIELD_TOSS];
        this.skillCode = SHIELD_TOSS;
        this.maxCooldown = 13000;
        this.reqWeapon = Globals.ITEM_SHIELD;
        this.skillName = "Shield Toss";
    }

    @Override
    public void drawInfo(final Graphics2D g, final int x, final int y) {
        final int boxHeight = (this.level < 30) ? 255 : 210, boxWidth = 395;
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

        g.drawString("Throw your shield in front of you.", drawX + 10, drawY + 90);
        g.drawString("Deals damage with Defense as additional damage.", drawX + 10, drawY + 110);

        g.drawString("[Level " + this.level + "]", drawX + 10, drawY + 135);
        g.drawString("Deals " + (150 + 15 * this.level) + "% damage + Defense multiplied by " + this.df.format(15 + this.level) + ".",
                drawX + 10, drawY + 155);
        if (this.level < 30) {
            g.drawString("[Level " + (this.level + 1) + "]", drawX + 10, drawY + 180);
            g.drawString("Deals " + (150 + 15 * (this.level + 1)) + "% damage + Defense multiplied by "
                    + this.df.format(15 + (this.level + 1)) + ".",
                    drawX + 10, drawY + 200);

            g.drawString("[Level 30 Bonus]", drawX + 10, drawY + 225);
            g.drawString("Throws 3 shields.", drawX + 10, drawY + 245);
        } else {
            g.drawString("[Level 30 Bonus]", drawX + 10, drawY + 180);
            g.drawString("Throws 3 shields.", drawX + 10, drawY + 200);
        }
    }

}
