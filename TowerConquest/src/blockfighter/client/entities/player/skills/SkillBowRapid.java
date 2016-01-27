package blockfighter.client.entities.player.skills;

import java.awt.Color;
import java.awt.Graphics2D;

import blockfighter.client.Globals;
import blockfighter.client.entities.items.ItemEquip;

/**
 *
 * @author Ken Kwan
 */
public class SkillBowRapid extends Skill {

	public SkillBowRapid() {
		this.icon = Globals.SKILL_ICON[BOW_RAPID];
		this.skillCode = BOW_RAPID;
		this.maxCooldown = 700;
		this.reqWeapon = Globals.ITEM_BOW;
		this.skillName = "Rapid Fire";
	}

	@Override
	public void drawInfo(final Graphics2D g, final int x, final int y) {
		final int boxHeight = (this.level < 30) ? 235 : 190, boxWidth = 340;
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
		g.drawString("Cooldown: 0.7 Second", drawX + 80, drawY + 70);

		g.drawString("Fire 3 shots over 0.5 seconds.", drawX + 10, drawY + 90);

		g.drawString("[Level " + this.level + "]", drawX + 10, drawY + 115);
		g.drawString("Deals " + (75 + this.level * 2) + "% damage per hit.", drawX + 10, drawY + 135);
		if (this.level < 30) {
			g.drawString("[Level " + (this.level + 1) + "]", drawX + 10, drawY + 160);
			g.drawString("Deals " + (75 + (this.level + 1) * 2) + "% damage per hit.", drawX + 10, drawY + 180);

			g.drawString("[Level 30 Bonus]", drawX + 10, drawY + 205);
			g.drawString("Each shot has 50% Chance to deal 2x damage.", drawX + 10, drawY + 225);
		} else {
			g.drawString("[Level 30 Bonus]", drawX + 10, drawY + 160);
			g.drawString("Each shot has 50% Chance to deal 2x damage.", drawX + 10, drawY + 180);
		}
	}

}
