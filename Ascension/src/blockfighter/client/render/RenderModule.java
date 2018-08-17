package blockfighter.client.render;

import blockfighter.client.Core;

public class RenderModule implements Runnable {

    private final RenderPanel panel;

    public RenderModule(final RenderPanel p) {
        this.panel = p;
    }

    @Override
    public void run() {
        this.panel.setScreen(Core.getLogicModule().getScreen());
        this.panel.repaint();
    }
}
