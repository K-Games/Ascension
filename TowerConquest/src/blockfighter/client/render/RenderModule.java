package blockfighter.client.render;

import blockfighter.client.LogicModule;

/**
 *
 * @author Ken Kwan
 */
public class RenderModule implements Runnable {

	private final RenderPanel panel;
	private static LogicModule logic;
	private int FPSCount = 0;
	double lastUpdateTime; // Last time we rendered
	double lastFPSTime; // Last time FPS count reset

	public RenderModule(final RenderPanel p) {
		this.panel = p;
	}

	public static void setLogic(final LogicModule l) {
		logic = l;
	}

	@Override
	public void run() {
		final double now = System.nanoTime(); // Get time now
		this.panel.setScreen(logic.getScreen());
		this.panel.repaint();
		this.FPSCount++;

		if (now - this.lastFPSTime >= 1000000000) {
			this.panel.setFPSCount(this.FPSCount);
			this.FPSCount = 0;
			this.lastFPSTime = now;
		}
	}
}
