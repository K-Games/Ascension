package blockfighter.client.render;

import blockfighter.client.LogicModule;
import blockfighter.client.Globals;
import javax.swing.JFrame;

/**
 *
 * @author ckwa290
 */
public class RenderModule extends Thread {

    private final RenderPanel panel;
    private final LogicModule logic;
    private boolean isRunning = false;
    private int FPSCount = 0;
    private JFrame mainFrame;

    public RenderModule(RenderPanel p, LogicModule l, JFrame f) {
        panel = p;
        logic = l;
        isRunning = true;
        mainFrame = f;
    }

    @Override
    public void run() {

        double lastUpdateTime = System.nanoTime(); //Last time we rendered
        double lastFPSTime = lastUpdateTime; //Last time FPS count reset
        panel.setLayout(null);

        while (isRunning) {
            double now = System.nanoTime(); //Get time now
            if (now - lastUpdateTime >= Globals.RENDER_UPDATE) {
                panel.setScreen(logic.getScreen());
                logic.getScreen().setRenderPanel(panel);
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
}
