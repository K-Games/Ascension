package blockfighter.client.entities.player.skills;

import java.awt.Color;
import java.awt.Graphics2D;

import blockfighter.client.Globals;

/**
 *
 * @author Ken Kwan
 */
public class SkillPassiveDualSword extends Skill {

	public SkillPassiveDualSword() {
		this.skillCode = PASSIVE_DUALSWORD;
		this.skillName = "Dual Wield Mastery";
		this.icon = Globals.SKILL_ICON[PASSIVE_DUALSWORD];
	}

	@Override
	public void drawInfo(final Graphics2D g, final int x, final int y) {
		final int boxHeight = (this.level < 30) ? 270 : 205, boxWidth = 365;
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

		g.drawString("When equipped with 2 Swords you gain additional", drawX + 10, drawY + 90);
		g.drawString("Critical Hit Chance.", drawX + 10, drawY + 110);

		g.setColor(new Color(255, 190, 0));
		g.drawString("Assign this passive to a hotkey to gain its effects.", drawX + 10, drawY + 130);

		g.setColor(Color.WHITE);
		g.drawString("[Level " + this.level + "]", drawX + 10, drawY + 155);
		g.drawString("Additional " + this.df.format(6 + this.level * 0.3) + "% Critical Hit Chance.", drawX + 10, drawY + 175);
		g.drawString("Take " + this.df.format(this.level) + "% reduced damage.", drawX + 10, drawY + 195);
		if (this.level < 30) {
			g.drawString("[Level " + (this.level + 1) + "]", drawX + 10, drawY + 220);
			g.drawString("Additional " + this.df.format(6 + (this.level + 1) * 0.3) + "% Critical Hit Chance.", drawX + 10, drawY + 240);
			g.drawString("Take " + this.df.format(this.level + 1) + "% reduced damage.", drawX + 10, drawY + 260);
		}
	}
}
