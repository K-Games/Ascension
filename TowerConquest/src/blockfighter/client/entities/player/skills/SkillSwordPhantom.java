package blockfighter.client.entities.player.skills;

import blockfighter.client.Globals;
import blockfighter.client.entities.items.ItemEquip;
import java.awt.Color;
import java.awt.Graphics2D;

public class SkillSwordPhantom extends Skill {

    public SkillSwordPhantom() {
        this.icon = Globals.SKILL_ICON[SWORD_PHANTOM];
        this.skillCode = SWORD_PHANTOM;
        this.maxCooldown = 20000;
        this.reqWeapon = Globals.ITEM_SWORD;
        this.skillName = "Phantom Reaper";
    }

    @Override
    public void drawInfo(final Graphics2D g, final int x, final int y) {
        final int boxHeight = (this.level < 30) ? 275 : 230, boxWidth = 350;
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

        g.drawString("Teleport multiple times to a random enemy", drawX + 10, drawY + 90);
        g.drawString("within a 350 radius and strike in their direction.", drawX + 10, drawY + 110);
        g.drawString("Invulnerable during the skill duration.", drawX + 10, drawY + 130);

        g.drawString("[Level " + this.level + "]", drawX + 10, drawY + 155);
        g.drawString("Perform " + (5 + (this.level / 2)) + " attacks for " + (75 + (this.level * 2)) + "% damage.", drawX + 10, drawY + 175);
        if (this.level < 30) {
            g.drawString("[Level " + (this.level + 1) + "]", drawX + 10, drawY + 200);
            g.drawString("Perform " + (5 + ((this.level + 1) / 2)) + " attacks for " + (75 + ((this.level + 1) * 2)) + "% damage.", drawX + 10, drawY + 220);

            g.drawString("[Level 30 Bonus]", drawX + 10, drawY + 245);
            g.drawString("", drawX + 10, drawY + 265);
        } else {
            g.drawString("[Level 30 Bonus]", drawX + 10, drawY + 200);
            g.drawString("", drawX + 10, drawY + 220);
        }
    }

}
