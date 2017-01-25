package blockfighter.client.render;

import blockfighter.client.Core;

public class RenderModule implements Runnable {

    private final RenderPanel panel;
    private int FPSCount = 0;
    double lastUpdateTime; // Last time we rendered
    double lastFPSTime; // Last time FPS count reset

    public RenderModule(final RenderPanel p) {
        this.panel = p;
    }

    @Override
    public void run() {
        final long now = Core.getLogicModule().getTime(); // Get time now
        this.panel.setScreen(Core.getLogicModule().getScreen());
        this.panel.repaint();
        this.FPSCount++;

        if (now - this.lastFPSTime >= 1000000000) {
            this.panel.setFPSCount(this.FPSCount);
            this.FPSCount = 0;
            this.lastFPSTime = now;
        }
    }
}
