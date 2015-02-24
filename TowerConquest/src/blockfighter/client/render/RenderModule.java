package blockfighter.client.render;

import blockfighter.client.Globals;
import blockfighter.client.LogicModule;
import javax.swing.JFrame;

/**
 *
 * @author Ken Kwan
 */
public class RenderModule extends Thread {

    private final RenderPanel panel;
    private static LogicModule logic;
    private int FPSCount = 0;
    private JFrame mainFrame;
    double lastUpdateTime; //Last time we rendered
    double lastFPSTime; //Last time FPS count reset

    public RenderModule(RenderPanel p, JFrame f) {
        panel = p;
        mainFrame = f;
    }

    public static void setLogic(LogicModule l) {
        logic = l;
    }

    @Override
    public void run() {
        double now = System.nanoTime(); //Get time now
        panel.setScreen(logic.getScreen());
        panel.repaint();
        FPSCount++;

        if (now - lastFPSTime >= 1000000000) {
            panel.setFPSCount(FPSCount);
            FPSCount = 0;
            lastFPSTime = now;
        }
    }
}
