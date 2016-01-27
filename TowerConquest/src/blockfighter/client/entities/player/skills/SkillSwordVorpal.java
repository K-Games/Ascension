package blockfighter.client.entities.player.skills;

import java.awt.Color;
import java.awt.Graphics2D;

import blockfighter.client.Globals;
import blockfighter.client.entities.items.ItemEquip;

/**
 *
 * @author Ken Kwan
 */
public class SkillSwordVorpal extends Skill {

	public SkillSwordVorpal() {
		this.icon = Globals.SKILL_ICON[SWORD_VORPAL];
		this.skillCode = SWORD_VORPAL;
		this.maxCooldown = 14000;
		this.reqWeapon = Globals.ITEM_SWORD;
		this.skillName = "Echoing Fury";
	}

	@Override
	public void drawInfo(final Graphics2D g, final int x, final int y) {
		final int boxHeight = (this.level < 30) ? 295 : 230, boxWidth = 370;
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

		g.drawString("Stab rapidly 3 times.", drawX + 10, drawY + 90);

		g.drawString("[Level " + this.level + "]", drawX + 10, drawY + 115);
		g.drawString("Deals " + (6 * this.level + 100) + "% damage per hit.", drawX + 10, drawY + 135);
		g.drawString("Critical Hits deal additional +" + (3 * this.level + 40) + "% Critical Damage.", drawX + 10, drawY + 155);
		if (this.level < 30) {
			g.drawString("[Level " + (this.level + 1) + "]", drawX + 10, drawY + 180);
			g.drawString("Deals " + (6 * (this.level + 1) + 100) + "% damage per hit.", drawX + 10, drawY + 200);
			g.drawString("Critical Hits deal additional +" + (3 * (this.level + 1) + 40) + "% Critical Damage.", drawX + 10, drawY + 220);

			g.drawString("[Level 30 Bonus]", drawX + 10, drawY + 245);
			g.drawString("This attack has +30% Critical Hit Chance.", drawX + 10, drawY + 265);
			g.drawString("Stab rapidly hit 5 times.", drawX + 10, drawY + 285);
		} else {
			g.drawString("[Level 30 Bonus]", drawX + 10, drawY + 180);
			g.drawString("This attack has +30% Critical Hit Chance.", drawX + 10, drawY + 200);
			g.drawString("Stab rapidly hit 5 times.", drawX + 10, drawY + 220);
		}
	}

}
