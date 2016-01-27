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
public class ScreenKeyBind extends ScreenMenu {

	private final SaveData c;
	private final Rectangle2D.Double[] keyBox = new Rectangle2D.Double[Globals.NUM_KEYBINDS];
	private int selectedKeyBox = -1;

	public ScreenKeyBind() {
		this.c = logic.getSelectedChar();

		for (int i = 0; i < 12; i++) {
			this.keyBox[i] = new Rectangle2D.Double(365, 45 + (i * 50), 180, 30);
		}
		for (int i = 12; i < this.keyBox.length; i++) {
			this.keyBox[i] = new Rectangle2D.Double(800, 45 + ((i - 12) * 50), 180, 30);
		}
	}

	@Override
	public void draw(final Graphics2D g) {
		final BufferedImage bg = Globals.MENU_BG[4];
		g.drawImage(bg, 0, 0, null);

		g.setRenderingHint(
				RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
		drawButtons(g);

		drawMenuButton(g);
		super.draw(g);
	}

	private void drawButtons(final Graphics2D g) {
		final BufferedImage button = Globals.MENU_BUTTON[Globals.BUTTON_SMALLRECT];
		for (int i = 0; i < this.keyBox.length; i++) {
			g.drawImage(button, (int) this.keyBox[i].x, (int) this.keyBox[i].y, null);
			g.setFont(Globals.ARIAL_18PT);
			if (this.selectedKeyBox == i) {
				drawStringOutline(g, "Assign a key", (int) (this.keyBox[i].x + 40), (int) (this.keyBox[i].y + 25), 1);
				g.setColor(Color.WHITE);
				g.drawString("Assign a key", (int) (this.keyBox[i].x + 40), (int) (this.keyBox[i].y + 25));
			} else {
				String key = "Not Assigned";
				if (this.c.getKeyBind()[i] != -1) {
					key = KeyEvent.getKeyText(this.c.getKeyBind()[i]);
				}
				final int width = g.getFontMetrics().stringWidth(key);
				drawStringOutline(g, key, (int) (this.keyBox[i].x + 90 - width / 2), (int) (this.keyBox[i].y + 25), 1);
				g.setColor(Color.WHITE);
				g.drawString(key, (int) (this.keyBox[i].x + 90 - width / 2), (int) (this.keyBox[i].y + 25));
			}
		}

		for (int i = 0; i < 12; i++) {
			g.setFont(Globals.ARIAL_18PT);
			drawStringOutline(g, "Hotkey Bar " + (i + 1) + ": ", 240, (int) (this.keyBox[i].y + 25), 1);
			g.setColor(Color.WHITE);
			g.drawString("Hotkey Bar " + (i + 1) + ": ", 240, (int) (this.keyBox[i].y + 25));
		}

		g.setFont(Globals.ARIAL_18PT);
		drawStringOutline(g, "Walk Left: ", 690, (int) (this.keyBox[Globals.KEYBIND_LEFT].y + 25), 1);
		drawStringOutline(g, "Walk Right: ", 690, (int) (this.keyBox[Globals.KEYBIND_RIGHT].y + 25), 1);
		drawStringOutline(g, "Jump: ", 690, (int) (this.keyBox[Globals.KEYBIND_JUMP].y + 25), 1);
		drawStringOutline(g, "Down: ", 690, (int) (this.keyBox[Globals.KEYBIND_DOWN].y + 25), 1);

		g.setColor(Color.WHITE);
		g.drawString("Walk Left: ", 690, (int) (this.keyBox[Globals.KEYBIND_LEFT].y + 25));
		g.drawString("Walk Right: ", 690, (int) (this.keyBox[Globals.KEYBIND_RIGHT].y + 25));
		g.drawString("Jump: ", 690, (int) (this.keyBox[Globals.KEYBIND_JUMP].y + 25));
		g.drawString("Down: ", 690, (int) (this.keyBox[Globals.KEYBIND_DOWN].y + 25));
	}

	@Override
	public void keyTyped(final KeyEvent e) {

	}

	@Override
	public void keyPressed(final KeyEvent e) {

	}

	@Override
	public void keyReleased(final KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			this.selectedKeyBox = -1;
			return;
		}

		if (this.selectedKeyBox != -1) {
			if ((e.getKeyCode() >= KeyEvent.VK_0
					&& e.getKeyCode() <= KeyEvent.VK_9)
					|| (e.getKeyCode() >= KeyEvent.VK_A
							&& e.getKeyCode() <= KeyEvent.VK_Z)
					|| e.getKeyCode() <= KeyEvent.VK_SPACE
					|| (e.getKeyCode() >= KeyEvent.VK_LEFT
							&& e.getKeyCode() <= KeyEvent.VK_DOWN)) {
				this.c.setKeyBind(this.selectedKeyBox, e.getKeyCode());
				this.selectedKeyBox = -1;
			}
		}
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
		for (int i = 0; i < this.keyBox.length; i++) {
			if (this.keyBox[i].contains(e.getPoint())) {
				this.selectedKeyBox = i;
				return;
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

	}

	@Override
	public void mouseMoved(final MouseEvent e) {
	}

	@Override
	public void unload() {
	}

}
