package blockfighter.client.render;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import blockfighter.client.Globals;
import blockfighter.client.screen.Screen;

/**
 *
 * @author Ken Kwan
 */
public class RenderPanel extends JPanel {

	/**
	 *
	 */
	private static final long serialVersionUID = 6032445082094163311L;
	private int FPSCount = 0;
	private Screen screen = null;

	public RenderPanel() {
		super();
		setBackground(Color.WHITE);
	}

	@Override
	public void paintComponent(final Graphics g) {
		final Graphics2D g2d = (Graphics2D) g;
		super.paintComponent(g);
		if (this.screen != null) {
			this.screen.draw(g2d);
		}

		g2d.setFont(Globals.ARIAL_12PT);
		g2d.setColor(Color.WHITE);
		g2d.drawString("FPS: " + this.FPSCount, 1220, 15);
	}

	public void setFPSCount(final int f) {
		this.FPSCount = f;
	}

	public void setScreen(final Screen s) {
		this.screen = s;
	}
}
