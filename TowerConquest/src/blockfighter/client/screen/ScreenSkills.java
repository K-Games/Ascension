package blockfighter.client.screen;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import javax.swing.SwingUtilities;

import blockfighter.client.Globals;
import blockfighter.client.SaveData;
import blockfighter.client.entities.player.skills.Skill;

/**
 *
 * @author Ken Kwan
 */
public class ScreenSkills extends ScreenMenu {

	private final SaveData c;
	// Slots(x,y) in the GUI
	private final Rectangle2D.Double[] hotkeySlots = new Rectangle2D.Double[12];
	private final Rectangle2D.Double[] skillSlots = new Rectangle2D.Double[Skill.NUM_SKILLS];
	private final Rectangle2D.Double[] addBox = new Rectangle2D.Double[Skill.NUM_SKILLS];
	private final Rectangle2D.Double resetBox;

	// Actual skills stored
	private final Skill[] hotkeyList;
	private final Skill[] skillList;

	private Point mousePos;

	private int drawInfoSkill = -1, drawInfoHotkey = -1;
	private int dragSkill = -1, dragHotkey = -1;

	public ScreenSkills() {
		this.c = logic.getSelectedChar();
		this.hotkeyList = this.c.getHotkeys();
		this.skillList = this.c.getSkills();

		this.skillSlots[Skill.SWORD_DRIVE] = new Rectangle2D.Double(241, 55, 60, 60);
		this.skillSlots[Skill.SWORD_SLASH] = new Rectangle2D.Double(241, 145, 60, 60);
		this.skillSlots[Skill.SWORD_MULTI] = new Rectangle2D.Double(241, 235, 60, 60);
		this.skillSlots[Skill.SWORD_VORPAL] = new Rectangle2D.Double(241, 325, 60, 60);
		this.skillSlots[Skill.SWORD_CINDER] = new Rectangle2D.Double(241, 415, 60, 60);
		this.skillSlots[Skill.SWORD_TAUNT] = new Rectangle2D.Double(241, 505, 60, 60);

		this.skillSlots[Skill.BOW_ARC] = new Rectangle2D.Double(506, 55, 60, 60);
		this.skillSlots[Skill.BOW_RAPID] = new Rectangle2D.Double(506, 145, 60, 60);
		this.skillSlots[Skill.BOW_POWER] = new Rectangle2D.Double(506, 235, 60, 60);
		this.skillSlots[Skill.BOW_VOLLEY] = new Rectangle2D.Double(506, 325, 60, 60);
		this.skillSlots[Skill.BOW_STORM] = new Rectangle2D.Double(506, 415, 60, 60);
		this.skillSlots[Skill.BOW_FROST] = new Rectangle2D.Double(506, 505, 60, 60);

		this.skillSlots[Skill.SHIELD_FORTIFY] = new Rectangle2D.Double(767, 55, 60, 60);
		this.skillSlots[Skill.SHIELD_IRON] = new Rectangle2D.Double(767, 145, 60, 60);
		this.skillSlots[Skill.SHIELD_CHARGE] = new Rectangle2D.Double(767, 235, 60, 60);
		this.skillSlots[Skill.SHIELD_REFLECT] = new Rectangle2D.Double(767, 325, 60, 60);
		this.skillSlots[Skill.SHIELD_TOSS] = new Rectangle2D.Double(767, 415, 60, 60);
		this.skillSlots[Skill.SHIELD_DASH] = new Rectangle2D.Double(767, 505, 60, 60);

		this.skillSlots[Skill.PASSIVE_DUALSWORD] = new Rectangle2D.Double(1050, 55, 60, 60);
		this.skillSlots[Skill.PASSIVE_KEENEYE] = new Rectangle2D.Double(1050, 140, 60, 60);
		this.skillSlots[Skill.PASSIVE_VITALHIT] = new Rectangle2D.Double(1050, 225, 60, 60);

		this.skillSlots[Skill.PASSIVE_SHIELDMASTERY] = new Rectangle2D.Double(1050, 310, 60, 60);
		this.skillSlots[Skill.PASSIVE_BARRIER] = new Rectangle2D.Double(1050, 395, 60, 60);
		this.skillSlots[Skill.PASSIVE_RESIST] = new Rectangle2D.Double(1050, 480, 60, 60);

		this.skillSlots[Skill.PASSIVE_BOWMASTERY] = new Rectangle2D.Double(1160, 55, 60, 60);
		this.skillSlots[Skill.PASSIVE_WILLPOWER] = new Rectangle2D.Double(1160, 140, 60, 60);
		this.skillSlots[Skill.PASSIVE_TACTICAL] = new Rectangle2D.Double(1160, 225, 60, 60);

		this.skillSlots[Skill.PASSIVE_REVIVE] = new Rectangle2D.Double(1160, 310, 60, 60);
		this.skillSlots[Skill.PASSIVE_SHADOWATTACK] = new Rectangle2D.Double(1160, 395, 60, 60);
		this.skillSlots[Skill.PASSIVE_12] = new Rectangle2D.Double(1160, 480, 60, 60);
		for (int i = 0; i < this.hotkeySlots.length; i++) {
			this.hotkeySlots[i] = new Rectangle2D.Double(240 + (i * 64), 605, 60, 60);
		}
		for (int i = 0; i < 18; i++) {
			this.addBox[i] = new Rectangle2D.Double(this.skillSlots[i].x + 140, this.skillSlots[i].y + 32, 30, 23);
		}

		for (int i = 18; i < this.addBox.length; i++) {
			this.addBox[i] = new Rectangle2D.Double(this.skillSlots[i].x + 59, this.skillSlots[i].y + 37, 30, 23);
		}
		this.resetBox = new Rectangle2D.Double(1050, 630, 180, 30);
	}

	@Override
	public void draw(final Graphics2D g) {
		final BufferedImage bg = Globals.MENU_BG[3];
		g.drawImage(bg, 0, 0, null);

		g.setRenderingHint(
				RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);

		g.setFont(Globals.ARIAL_18PT);
		drawStringOutline(g, "Skill Points: " + (int) this.c.getBaseStats()[Globals.STAT_SKILLPOINTS], 1080, 620, 1);
		g.setColor(Color.WHITE);
		g.drawString("Skill Points: " + (int) this.c.getBaseStats()[Globals.STAT_SKILLPOINTS], 1080, 620);

		final BufferedImage button = Globals.MENU_BUTTON[Globals.BUTTON_SMALLRECT];
		g.drawImage(button, (int) this.resetBox.x, (int) this.resetBox.y, null);
		g.setFont(Globals.ARIAL_18PT);
		drawStringOutline(g, "Reset Skills", 1090, 657, 1);
		g.setColor(Color.WHITE);
		g.drawString("Reset Skills", 1090, 657);
		drawSlots(g);
		drawMenuButton(g);

		if (this.dragSkill != -1) {
			this.skillList[this.dragSkill].draw(g, this.mousePos.x, this.mousePos.y);
		} else if (this.dragHotkey != -1) {
			this.hotkeyList[this.dragHotkey].draw(g, this.mousePos.x, this.mousePos.y);
		}

		super.draw(g);
		drawSkillInfo(g);
	}

	private void drawSkillInfo(final Graphics2D g) {
		if (this.drawInfoSkill != -1) {
			drawSkillInfo(g, this.skillSlots[this.drawInfoSkill], this.skillList[this.drawInfoSkill]);
		} else if (this.drawInfoHotkey != -1) {
			drawSkillInfo(g, this.hotkeySlots[this.drawInfoHotkey], this.hotkeyList[this.drawInfoHotkey]);
		}
	}

	private void drawSlots(final Graphics2D g) {
		BufferedImage button = Globals.MENU_BUTTON[Globals.BUTTON_SLOT];
		g.setFont(Globals.ARIAL_18PT);
		drawStringOutline(g, "Sword", 325, 45, 1);
		g.setColor(Color.WHITE);
		g.drawString("Sword", 325, 45);

		drawStringOutline(g, "Bow", 600, 45, 1);
		g.setColor(Color.WHITE);
		g.drawString("Bow", 600, 45);

		drawStringOutline(g, "Shield", 850, 45, 1);
		g.setColor(Color.WHITE);
		g.drawString("Shield", 850, 45);

		drawStringOutline(g, "Passive", 1105, 45, 1);
		g.setColor(Color.WHITE);
		g.drawString("Passive", 1105, 45);
		for (int i = 0; i < this.hotkeySlots.length; i++) {
			g.drawImage(button, (int) this.hotkeySlots[i].x, (int) this.hotkeySlots[i].y, null);
			if (this.hotkeyList[i] != null) {
				this.hotkeyList[i].draw(g, (int) this.hotkeySlots[i].x, (int) this.hotkeySlots[i].y);
			}
			String key = "?";
			if (this.c.getKeyBind()[i] != -1) {
				key = KeyEvent.getKeyText(this.c.getKeyBind()[i]);
			}
			final int width = g.getFontMetrics().stringWidth(key);
			g.setFont(Globals.ARIAL_15PT);
			drawStringOutline(g, key, (int) this.hotkeySlots[i].x + 30 - width / 2, (int) this.hotkeySlots[i].y + 75, 1);
			g.setColor(Color.WHITE);
			g.drawString(key, (int) this.hotkeySlots[i].x + 30 - width / 2, (int) this.hotkeySlots[i].y + 75);
		}

		for (int i = 0; i < 18; i++) {
			g.drawImage(button, (int) this.skillSlots[i].x, (int) this.skillSlots[i].y, null);
			this.skillList[i].draw(g, (int) this.skillSlots[i].x, (int) this.skillSlots[i].y);
			g.setFont(Globals.ARIAL_15PT);
			drawStringOutline(g, this.skillList[i].getSkillName(), (int) this.skillSlots[i].x + 70, (int) this.skillSlots[i].y + 20, 1);
			drawStringOutline(g, "Level: " + this.skillList[i].getLevel(), (int) this.skillSlots[i].x + 70, (int) this.skillSlots[i].y + 50,
					1);
			g.setColor(Color.WHITE);
			g.drawString(this.skillList[i].getSkillName(), (int) this.skillSlots[i].x + 70, (int) this.skillSlots[i].y + 20);
			g.drawString("Level: " + this.skillList[i].getLevel(), (int) this.skillSlots[i].x + 70, (int) this.skillSlots[i].y + 50);

			if (this.c.getBaseStats()[Globals.STAT_SKILLPOINTS] > 0 && !this.skillList[i].isMaxed()) {
				button = Globals.MENU_BUTTON[Globals.BUTTON_ADDSTAT];
				g.drawImage(button, (int) this.addBox[i].x, (int) this.addBox[i].y, null);
				g.setFont(Globals.ARIAL_15PT);
				drawStringOutline(g, "+", (int) this.addBox[i].x + 11, (int) this.addBox[i].y + 18, 1);
				g.setColor(Color.WHITE);
				g.drawString("+", (int) this.addBox[i].x + 11, (int) this.addBox[i].y + 18);
			}
		}

		for (int i = 18; i < this.skillSlots.length; i++) {
			g.drawImage(button, (int) this.skillSlots[i].x, (int) this.skillSlots[i].y, null);
			this.skillList[i].draw(g, (int) this.skillSlots[i].x, (int) this.skillSlots[i].y);
			g.setFont(Globals.ARIAL_15PT);
			drawStringOutline(g, "Level: " + this.skillList[i].getLevel(), (int) this.skillSlots[i].x, (int) this.skillSlots[i].y + 80, 1);
			g.setColor(Color.WHITE);
			g.drawString("Level: " + this.skillList[i].getLevel(), (int) this.skillSlots[i].x, (int) this.skillSlots[i].y + 80);

			if (this.c.getBaseStats()[Globals.STAT_SKILLPOINTS] > 0 && !this.skillList[i].isMaxed()) {
				button = Globals.MENU_BUTTON[Globals.BUTTON_ADDSTAT];
				g.drawImage(button, (int) this.addBox[i].x, (int) this.addBox[i].y, null);
				g.setFont(Globals.ARIAL_15PT);
				drawStringOutline(g, "+", (int) this.addBox[i].x + 11, (int) this.addBox[i].y + 18, 1);
				g.setColor(Color.WHITE);
				g.drawString("+", (int) this.addBox[i].x + 11, (int) this.addBox[i].y + 18);
			}
		}
	}

	private void drawSkillInfo(final Graphics2D g, final Rectangle2D.Double box, final Skill skill) {
		skill.drawInfo(g, (int) box.x, (int) box.y);
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
		final int drSkill = this.dragSkill, drHK = this.dragHotkey;
		this.dragSkill = -1;
		this.dragHotkey = -1;

		super.mouseReleased(e);
		if (SwingUtilities.isLeftMouseButton(e)) {
			for (int i = 0; i < this.hotkeySlots.length; i++) {
				if (this.hotkeySlots[i].contains(e.getPoint())) {
					if (drSkill != -1) {
						this.hotkeyList[i] = this.skillList[drSkill];
						return;
					}
					if (drHK != -1) {
						final Skill temp = this.hotkeyList[i];
						this.hotkeyList[i] = this.hotkeyList[drHK];
						this.hotkeyList[drHK] = temp;
						return;
					}
					return;
				}
			}
			if (this.resetBox.contains(e.getPoint())) {
				this.c.resetSkill();
				return;
			}
			for (byte i = 0; i < this.addBox.length; i++) {
				if (this.addBox[i].contains(e.getPoint())) {
					if (this.c.getBaseStats()[Globals.STAT_SKILLPOINTS] > 0 && !this.skillList[i].isMaxed()) {
						this.c.addSkill(i);
						return;
					}
				}
			}

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
		mouseMoved(e);
		if (SwingUtilities.isLeftMouseButton(e)) {
			if (this.dragSkill == -1 && this.dragHotkey == -1) {
				for (int i = 0; i < this.hotkeySlots.length; i++) {
					if (this.hotkeySlots[i].contains(e.getPoint()) && this.hotkeyList[i] != null) {
						this.dragHotkey = i;
						return;
					}
				}

				for (byte i = 0; i < this.skillSlots.length; i++) {
					if (this.skillSlots[i].contains(e.getPoint()) && this.skillSlots[i] != null) {
						this.dragSkill = i;
						return;
					}
				}
			}
		}
	}

	@Override
	public void mouseMoved(final MouseEvent e) {
		this.mousePos = e.getPoint();
		this.drawInfoSkill = -1;
		this.drawInfoHotkey = -1;
		for (int i = 0; i < this.hotkeySlots.length; i++) {
			if (this.hotkeySlots[i].contains(e.getPoint()) && this.hotkeyList[i] != null) {
				this.drawInfoHotkey = i;
				return;
			}
		}

		for (byte i = 0; i < this.skillSlots.length; i++) {
			if (this.skillSlots[i].contains(e.getPoint()) && this.skillSlots[i] != null) {
				this.drawInfoSkill = i;
				return;
			}
		}
	}

	@Override
	public void unload() {
	}

}
