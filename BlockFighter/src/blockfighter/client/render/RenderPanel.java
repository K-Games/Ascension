package blockfighter.client.render;

import blockfighter.client.Globals;
import blockfighter.client.SaveData;
import blockfighter.client.entities.particles.Particle;
import blockfighter.client.screen.Screen;
import java.awt.Color;
import java.awt.Graphics;
import java.util.concurrent.ConcurrentHashMap;
import javax.swing.JPanel;

/**
 *
 * @author ckwa290
 */
public class RenderPanel extends JPanel {

    private int FPSCount = 0;
    private Screen screen = null;

    private ConcurrentHashMap<Integer, Particle> particles;
    private SaveData[] charsData;

    public RenderPanel() {
        super();
        setBackground(Color.WHITE);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (screen != null) {
            screen.draw(g);
        }

        g.setFont(Globals.ARIAL_12PT);
        for (int i = 0; i < 2; i++) {
            g.setColor(Color.BLACK);
            g.drawString("FPS: " + FPSCount, 1199 + i * 2, 20);
            g.drawString("FPS: " + FPSCount, 1200, 19 + i * 2);
        }
        g.setColor(Color.WHITE);
        g.drawString("FPS: " + FPSCount, 1200, 20);
    }

    public void setFPSCount(int f) {
        FPSCount = f;
    }

    public void setScreen(Screen s) {
        screen = s;
    }
}
