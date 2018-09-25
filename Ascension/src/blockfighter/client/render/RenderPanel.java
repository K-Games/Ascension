package blockfighter.client.render;

import blockfighter.client.screen.Screen;
import blockfighter.shared.Globals;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import javax.swing.JPanel;
import org.jogamp.glg2d.GLGraphics2D;

public class RenderPanel extends JPanel {

    private static final long serialVersionUID = 6032445082094163311L;
    private int FPSCount = 0;
    private Screen screen = null;

    private long lastFPSTime = 0;
    private int fpsCountBuffer = 0;

    public RenderPanel() {
        super();
    }

    @Override
    public void paintComponent(final Graphics g) {
        GLGraphics2D g2d = (GLGraphics2D) g;

        super.paintComponent(g2d);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        final AffineTransform resetForm = g2d.getTransform();
        if (Globals.WINDOW_SCALE_ENABLED) {
            g2d.scale(Globals.WINDOW_SCALE, Globals.WINDOW_SCALE);
        }
        if (this.screen != null) {
            this.screen.draw(g2d);
        }

        if ((Boolean) Globals.ClientOptions.PERFORMANCE_DISPLAY.getValue()) {
            g2d.setFont(Globals.ARIAL_12PT);
            Screen.drawStringOutline(g2d, "FPS: " + this.FPSCount, 1220, 15, 1);
            g2d.setColor(Color.WHITE);
            g2d.drawString("FPS: " + this.FPSCount, 1220, 15);
            if (Globals.WINDOW_SCALE_ENABLED) {
                g2d.setTransform(resetForm);
            }
        }
        this.fpsCountBuffer++;

        long now = System.currentTimeMillis();
        if (now - this.lastFPSTime >= 1000) {
            setFPSCount(this.fpsCountBuffer);
            this.fpsCountBuffer = 0;
            this.lastFPSTime = now;
        }
    }

    public void setFPSCount(final int f) {
        this.FPSCount = f;
    }

    public void setScreen(final Screen s) {
        this.screen = s;
        setFocusTraversalKeysEnabled(false);
    }
}
