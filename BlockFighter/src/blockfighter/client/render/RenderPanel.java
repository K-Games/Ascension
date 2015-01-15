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
        drawStringOutline(g, "FPS: " + FPSCount, 1220, 20, 1);

        g.setColor(Color.WHITE);
        g.drawString("FPS: " + FPSCount, 1220, 20);
    }

    private void drawStringOutline(Graphics g, String s, int x, int y, int width) {
        for (int i = 0; i < 2; i++) {
            g.setColor(Color.BLACK);
            g.drawString(s, x - width + i * 2 * width, y);
            g.drawString(s, x, y - width + i * 2 * width);
        }
    }

    public void setFPSCount(int f) {
        FPSCount = f;
    }

    public void setScreen(Screen s) {
        screen = s;
    }
}
