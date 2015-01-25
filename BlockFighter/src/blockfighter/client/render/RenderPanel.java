package blockfighter.client.render;

import blockfighter.client.Globals;
import blockfighter.client.screen.Screen;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;

/**
 *
 * @author Ken Kwan
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
        Graphics2D g2d = (Graphics2D) g;
        super.paintComponent(g);
        if (screen != null) {
            screen.draw(g2d);
        }

        g2d.setFont(Globals.ARIAL_12PT);
        g2d.setColor(Color.WHITE);
        g2d.drawString("FPS: " + FPSCount, 1220, 15);
    }

    public void setFPSCount(int f) {
        FPSCount = f;
    }

    public void setScreen(Screen s) {
        screen = s;
    }
}
