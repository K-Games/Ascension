package blockfighter.client.render;

import blockfighter.client.LogicModule;
import blockfighter.client.Globals;

/**
 *
 * @author ckwa290
 */
public class RenderModule extends Thread {

    private final RenderPanel panel;
    private final LogicModule logic;
    private boolean isRunning = false;
    private int FPSCount = 0;

    public RenderModule(RenderPanel p, LogicModule l) {
        panel = p;
        logic = l;
        isRunning = true;
    }

    @Override
    public void run() {

        double lastUpdateTime = System.nanoTime(); //Last time we rendered
        double lastFPSTime = lastUpdateTime; //Last time FPS count reset

        while (isRunning) {
            double now = System.nanoTime(); //Get time now
            if (now - lastUpdateTime >= Globals.RENDER_UPDATE) {
                panel.setScreen(logic.getScreen());
                switch (logic.getScreen()) {
                    case Globals.SCREEN_CHAR_SELECT:
                        setRenderMenuSelect();
                        break;
                    case Globals.SCREEN_INGAME:
                        setRenderIngame();
                        break;
                }
                panel.repaint();
                FPSCount++;
                lastUpdateTime = now;
            }

            if (now - lastFPSTime >= 1000000000) {
                panel.setFPSCount(FPSCount);
                FPSCount = 0;
                lastFPSTime = now;
            }

            //Yield until rendering again
            while (now - lastUpdateTime < Globals.RENDER_UPDATE && now - lastFPSTime < 1000000000) {
                Thread.yield();
                now = System.nanoTime();
            }

        }
    }

    private void setRenderIngame() {
        panel.setPlayers(logic.getPlayers());
        panel.setPing(logic.getPing());
        panel.setMyIndex(logic.getMyIndex());
        panel.setParticles(logic.getParticles());
    }

    private void setRenderMenuSelect() {
        panel.setParticles(logic.getParticles());
    }
}
