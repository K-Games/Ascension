package blockfighter.client.entities.player.skills;

import java.awt.Color;
import java.awt.Graphics2D;

import blockfighter.client.Globals;
import blockfighter.client.entities.items.ItemEquip;

/**
 *
 * @author Ken Kwan
 */
public class SkillShieldIron extends Skill {

	public SkillShieldIron() {
		this.icon = Globals.SKILL_ICON[SHIELD_IRON];
		this.skillCode = SHIELD_IRON;
		this.maxCooldown = 20000;
		this.reqWeapon = Globals.ITEM_SHIELD;
		this.skillName = "Iron Fortress";
	}

	@Override
	public void drawInfo(final Graphics2D g, final int x, final int y) {
		final int boxHeight = (this.level < 30) ? 235 : 190, boxWidth = 400;
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

		g.drawString("Become immobile and reduce damage taken.", drawX + 10, drawY + 90);

		g.drawString("[Level " + this.level + "]", drawX + 10, drawY + 115);
		g.drawString("Reduce damage taken by " + (this.level + 55) + "% for 2 seconds.", drawX + 10, drawY + 135);
		if (this.level < 30) {
			g.drawString("[Level " + (this.level + 1) + "]", drawX + 10, drawY + 160);
			g.drawString("Reduce damage taken by " + ((this.level + 1) + 55) + "% for 2 seconds.", drawX + 10, drawY + 180);

			g.drawString("[Level 30 Bonus]", drawX + 10, drawY + 205);
			g.drawString("Allies also reduce damage taken by 40% for 2 seconds.", drawX + 10, drawY + 225);
		} else {
			g.drawString("[Level 30 Bonus]", drawX + 10, drawY + 160);
			g.drawString("Allies also reduce damage taken by 40% for 2 seconds.", drawX + 10, drawY + 180);
		}
	}

}
