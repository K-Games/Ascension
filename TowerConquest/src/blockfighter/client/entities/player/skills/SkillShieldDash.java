package blockfighter.client.entities.player.skills;

import java.awt.Color;
import java.awt.Graphics2D;

import blockfighter.client.Globals;

/**
 *
 * @author Ken Kwan
 */
public class SkillShieldDash extends Skill {

	public SkillShieldDash() {
		this.icon = Globals.SKILL_ICON[SHIELD_DASH];
		this.skillCode = SHIELD_DASH;
		this.maxCooldown = 13000;
		// reqWeapon = Globals.ITEM_SHIELD;
		this.skillName = "Dash";
	}

	@Override
	public void drawInfo(final Graphics2D g, final int x, final int y) {
		final int boxHeight = (this.level < 30) ? 255 : 210, boxWidth = 310;
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
		g.drawString("Level: " + this.level, drawX + 80, drawY + 50);
		g.drawString("Cooldown: " + this.maxCooldown / 1000 + " Seconds", drawX + 80, drawY + 70);

		g.drawString("Dash a short distance over 0.25 seconds.", drawX + 10, drawY + 90);
		g.drawString("Increases damage dealt for 5 seconds.", drawX + 10, drawY + 110);

		g.drawString("[Level " + this.level + "]", drawX + 10, drawY + 135);
		g.drawString("Increases damage dealt by " + this.df.format(1 + this.level * 0.3) + "%.", drawX + 10, drawY + 155);
		if (this.level < 30) {
			g.drawString("[Level " + (this.level + 1) + "]", drawX + 10, drawY + 180);
			g.drawString("Increases damage dealt by " + this.df.format(1 + (this.level + 1) * 0.3) + "%.", drawX + 10, drawY + 200);

			g.drawString("[Level 30 Bonus]", drawX + 10, drawY + 225);
			g.drawString("Invulnerable during dash.", drawX + 10, drawY + 245);
		} else {
			g.drawString("[Level 30 Bonus]", drawX + 10, drawY + 180);
			g.drawString("Invulnerable during dash.", drawX + 10, drawY + 200);
		}
	}

}
