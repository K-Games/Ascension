package blockfighter.client.screen;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import blockfighter.client.Globals;
import blockfighter.client.SaveData;

/**
 *
 * @author Ken Kwan
 */
public class ScreenStats extends ScreenMenu {

	private final SaveData c;
	private final double[] stats, bs;
	Rectangle2D.Double[] addBox = new Rectangle2D.Double[6];
	Rectangle2D.Double resetBox;

	public ScreenStats() {
		this.addBox[0] = new Rectangle2D.Double(418, 148, 30, 23);
		this.addBox[1] = new Rectangle2D.Double(418, 173, 30, 23);
		this.addBox[2] = new Rectangle2D.Double(418, 198, 30, 23);
		this.addBox[3] = new Rectangle2D.Double(453, 148, 30, 23);
		this.addBox[4] = new Rectangle2D.Double(453, 173, 30, 23);
		this.addBox[5] = new Rectangle2D.Double(453, 198, 30, 23);
		this.resetBox = new Rectangle2D.Double(255, 570, 180, 30);
		this.c = logic.getSelectedChar();
		this.stats = this.c.getTotalStats();
		this.bs = this.c.getBaseStats();
	}

	@Override
	public void draw(final Graphics2D g) {
		final BufferedImage bg = Globals.MENU_BG[1];
		g.drawImage(bg, 0, 0, null);

		g.setRenderingHint(
				RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);

		g.setFont(Globals.ARIAL_30PT);
		drawStringOutline(g, this.c.getPlayerName(), 255, 76, 2);
		g.setColor(Color.WHITE);
		g.drawString(this.c.getPlayerName(), 255, 76);

		final int mainStat = 165, secStat = 295;
		BufferedImage button = Globals.MENU_BUTTON[Globals.BUTTON_ADDSTAT];
		if (this.bs[Globals.STAT_POINTS] >= 1) {
			g.drawImage(button, 418, mainStat - 17, null);
			g.drawImage(button, 418, mainStat + 25 - 17, null);
			g.drawImage(button, 418, mainStat + 50 - 17, null);
		}

		if (this.bs[Globals.STAT_POINTS] >= 5) {
			g.drawImage(button, 453, mainStat - 17, null);
			g.drawImage(button, 453, mainStat + 25 - 17, null);
			g.drawImage(button, 453, mainStat + 50 - 17, null);
		}

		g.setFont(Globals.ARIAL_15PT);
		if (this.bs[Globals.STAT_POINTS] >= 1) {
			drawStringOutline(g, "+1", 425, mainStat, 1);
			drawStringOutline(g, "+1", 425, mainStat + 25, 1);
			drawStringOutline(g, "+1", 425, mainStat + 50, 1);
		}
		if (this.bs[Globals.STAT_POINTS] >= 5) {
			drawStringOutline(g, "+5", 460, mainStat, 2);
			drawStringOutline(g, "+5", 460, mainStat + 25, 1);
			drawStringOutline(g, "+5", 460, mainStat + 50, 1);
		}

		g.setColor(Color.WHITE);
		if (this.bs[Globals.STAT_POINTS] >= 1) {
			g.drawString("+1", 425, mainStat);
			g.drawString("+1", 425, mainStat + 25);
			g.drawString("+1", 425, mainStat + 50);
		}
		if (this.bs[Globals.STAT_POINTS] >= 5) {
			g.drawString("+5", 460, mainStat);
			g.drawString("+5", 460, mainStat + 25);
			g.drawString("+5", 460, mainStat + 50);
		}

		g.setFont(Globals.ARIAL_18PT);
		drawStringOutline(g, "Level: " + (int) this.stats[Globals.STAT_LEVEL], 255, 130, 1);
		drawStringOutline(g, "Power: " + (int) this.stats[Globals.STAT_POWER], 255, mainStat, 1);
		drawStringOutline(g, "Defense: " + (int) this.stats[Globals.STAT_DEFENSE], 255, mainStat + 25, 1);
		drawStringOutline(g, "Spirit: " + (int) this.stats[Globals.STAT_SPIRIT], 255, mainStat + 50, 1);
		drawStringOutline(g, "Points: " + (int) this.stats[Globals.STAT_POINTS], 255, mainStat + 85, 1);

		drawStringOutline(g, "HP: " + (int) this.stats[Globals.STAT_MAXHP], 255, secStat, 1);
		drawStringOutline(g, "Damage: " + (int) this.stats[Globals.STAT_MINDMG] + " - " + (int) this.stats[Globals.STAT_MAXDMG], 255,
				secStat + 25,
				1);
		drawStringOutline(g, "Armor: " + (int) this.stats[Globals.STAT_ARMOR], 255, secStat + 50, 1);
		drawStringOutline(g, "Regen: " + this.df.format(this.stats[Globals.STAT_REGEN]) + " HP/Sec", 255, secStat + 75, 1);
		drawStringOutline(g, "Critical Hit Chance: " + this.df.format(this.stats[Globals.STAT_CRITCHANCE] * 100) + "%", 255, secStat + 100,
				1);
		drawStringOutline(g, "Critical Hit Damage: " + this.df.format((1 + this.stats[Globals.STAT_CRITDMG]) * 100) + "%", 255,
				secStat + 125, 1);
		drawStringOutline(g, "Effective HP: " + this.df.format((int) Globals.calcEHP(
				this.stats[Globals.STAT_DAMAGEREDUCT],
				this.stats[Globals.STAT_MAXHP])), 255, secStat + 180, 1);
		drawStringOutline(g, "Exp: " + this.df.format((this.bs[Globals.STAT_EXP])) + "/"
				+ this.df.format(Globals.calcEXPtoNxtLvl(this.bs[Globals.STAT_LEVEL]))
				+ "(" + this.df.format((this.bs[Globals.STAT_EXP] / Globals.calcEXPtoNxtLvl(this.bs[Globals.STAT_LEVEL])) * 100) + "%)",
				255,
				secStat + 205, 1);

		g.setColor(Color.WHITE);
		g.drawString("Level: " + (int) this.stats[Globals.STAT_LEVEL], 255, 130);
		g.drawString("Power: " + (int) this.stats[Globals.STAT_POWER], 255, mainStat);
		g.drawString("Defense: " + (int) this.stats[Globals.STAT_DEFENSE], 255, mainStat + 25);
		g.drawString("Spirit: " + (int) this.stats[Globals.STAT_SPIRIT], 255, mainStat + 50);
		g.drawString("Points: " + (int) this.stats[Globals.STAT_POINTS], 255, mainStat + 85);

		g.drawString("HP: " + (int) this.stats[Globals.STAT_MAXHP], 255, secStat);
		g.drawString("Damage: " + (int) this.stats[Globals.STAT_MINDMG] + " - " + (int) this.stats[Globals.STAT_MAXDMG], 255, secStat + 25);
		g.drawString("Armor: " + (int) this.stats[Globals.STAT_ARMOR], 255, secStat + 50);
		g.drawString("Regen: " + this.df.format(this.stats[Globals.STAT_REGEN]) + " HP/Sec", 255, secStat + 75);
		g.drawString("Critical Hit Chance: " + this.df.format(this.stats[Globals.STAT_CRITCHANCE] * 100) + "%", 255, secStat + 100);
		g.drawString("Critical Hit Damage: " + this.df.format((1 + this.stats[Globals.STAT_CRITDMG]) * 100) + "%", 255, secStat + 125);

		g.drawString("Effective HP: " + this.df.format((int) Globals.calcEHP(
				this.stats[Globals.STAT_DAMAGEREDUCT],
				this.stats[Globals.STAT_MAXHP])), 255, secStat + 180);

		g.drawString("Exp: " + this.df.format((this.bs[Globals.STAT_EXP])) + "/"
				+ this.df.format(Globals.calcEXPtoNxtLvl(this.bs[Globals.STAT_LEVEL]))
				+ "(" + this.df.format((this.bs[Globals.STAT_EXP] / Globals.calcEXPtoNxtLvl(this.bs[Globals.STAT_LEVEL])) * 100) + "%)",
				255,
				secStat + 205);

		g.setColor(Color.BLACK);
		g.fillRect(255, secStat + 215, 450, 40);
		g.setColor(Color.WHITE);
		g.fillRect(256, secStat + 216, 448, 38);
		g.setColor(Color.BLACK);
		g.fillRect(257, secStat + 217, 446, 36);
		g.setColor(new Color(255, 175, 0));
		g.fillRect(258, secStat + 218, (int) (this.bs[Globals.STAT_EXP] / Globals.calcEXPtoNxtLvl(this.bs[Globals.STAT_LEVEL]) * 444), 34);

		button = Globals.MENU_BUTTON[Globals.BUTTON_SMALLRECT];
		g.drawImage(button, (int) this.resetBox.x, (int) this.resetBox.y, null);
		g.setFont(Globals.ARIAL_18PT);
		drawStringOutline(g, "Reset Stats", (int) (this.resetBox.x + 45), (int) (this.resetBox.y + 25), 1);
		g.setColor(Color.WHITE);
		g.drawString("Reset Stats", (int) (this.resetBox.x + 45), (int) (this.resetBox.y + 25));

		drawMenuButton(g);
		super.draw(g);
	}

	@Override
	public void keyTyped(final KeyEvent e) {

	}

	@Override
	public void keyPressed(final KeyEvent e) {

	}

	@Override
	public void keyReleased(final KeyEvent e) {

	}

	@Override
	public void mouseClicked(final MouseEvent e) {

	}

	@Override
	public void mousePressed(final MouseEvent e) {

	}

	@Override
	public void mouseReleased(final MouseEvent e) {
		super.mouseReleased(e);
		if (this.bs[Globals.STAT_POINTS] >= 1) {
			for (int i = 0; i < 3; i++) {
				if (this.addBox[i].contains(e.getPoint())) {
					switch (i) {
						case 0:
							this.c.addStat(Globals.STAT_POWER, 1);
							break;
						case 1:
							this.c.addStat(Globals.STAT_DEFENSE, 1);
							break;
						case 2:
							this.c.addStat(Globals.STAT_SPIRIT, 1);
							break;
					}
				}
			}
		}
		if (this.bs[Globals.STAT_POINTS] >= 5) {
			for (int i = 3; i < 6; i++) {
				if (this.addBox[i].contains(e.getPoint())) {
					switch (i) {
						case 3:
							this.c.addStat(Globals.STAT_POWER, 5);
							break;
						case 4:
							this.c.addStat(Globals.STAT_DEFENSE, 5);
							break;
						case 5:
							this.c.addStat(Globals.STAT_SPIRIT, 5);
							break;
					}
				}
			}
		}
		if (this.resetBox.contains(e.getPoint())) {
			this.c.resetStat();
		}
	}

	@Override
	public void mouseEntered(final MouseEvent e) {

	}

	@Override
	public void mouseExited(final MouseEvent e) {

	}

	@Override
	public void mouseDragged(final MouseEvent e) {

	}

	@Override
	public void mouseMoved(final MouseEvent e) {

	}

	@Override
	public void unload() {
	}

}
