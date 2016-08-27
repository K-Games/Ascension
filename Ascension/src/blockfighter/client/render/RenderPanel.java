package blockfighter.client.render;

import blockfighter.client.Globals;
import blockfighter.client.screen.Screen;
import java.awt.AWTException;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.ImageCapabilities;
import java.awt.RenderingHints;
import java.awt.image.VolatileImage;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;

public class RenderPanel extends JPanel {

    private static final long serialVersionUID = 6032445082094163311L;
    private int FPSCount = 0;
    private Screen screen = null;
    private boolean useGPU = true;
    private VolatileImage vBuffer;
    private Graphics2D bufferGraphics;

    public RenderPanel() {
        super();

    }

    @Override
    public void paintComponent(final Graphics g) {
        Graphics2D g2d;
        if (useGPU && vBuffer == null) {
            try {
                vBuffer = getGraphicsConfiguration().createCompatibleVolatileImage(Globals.WINDOW_WIDTH, Globals.WINDOW_HEIGHT, new ImageCapabilities(true));
            } catch (AWTException ex) {
                useGPU = false;
                Logger.getLogger(RenderPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
            bufferGraphics = vBuffer.createGraphics();
        }

        if (useGPU && vBuffer != null) {
            g2d = bufferGraphics;
        } else {
            g2d = (Graphics2D) g;
        }

        super.paintComponent(g2d);

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
        if (this.screen != null) {
            this.screen.draw(g2d);
        }

        g2d.setFont(Globals.ARIAL_12PT);
        g2d.setColor(Color.WHITE);
        g2d.drawString("FPS: " + this.FPSCount, 1220, 15);

        if (useGPU && vBuffer != null) {
            g.drawImage(vBuffer, 0, 0, null);
        }
    }

    public void setFPSCount(final int f) {
        this.FPSCount = f;
    }

    public void setScreen(final Screen s) {
        this.screen = s;
    }
}
