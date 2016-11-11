package blockfighter.client.render;

import blockfighter.client.AscensionClient;
import blockfighter.client.LogicModule;

public class RenderModule implements Runnable {

    private final RenderPanel panel;
    private static LogicModule logic;
    private int FPSCount = 0;
    double lastUpdateTime; // Last time we rendered
    double lastFPSTime; // Last time FPS count reset

    public RenderModule(final RenderPanel p) {
        this.panel = p;
    }

    public static void init() {
        logic = AscensionClient.getLogicModule();
    }

    @Override
    public void run() {
        final long now = logic.getTime(); // Get time now
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
