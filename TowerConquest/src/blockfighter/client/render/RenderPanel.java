package blockfighter.client.render;

import blockfighter.client.Globals;
import blockfighter.client.screen.Screen;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.VolatileImage;
import javax.swing.JPanel;

/**
 *
 * @author Ken Kwan
 */
public class RenderPanel extends JPanel {

    /**
     *
     */
    private static final long serialVersionUID = 6032445082094163311L;
    private int FPSCount = 0;
    private Screen screen = null;
    private final boolean useGPU = true;
    private VolatileImage vBuffer;
    private Graphics2D bufferGraphics;

    public RenderPanel() {
        super();

    }

    @Override
    public void paintComponent(final Graphics g) {
        Graphics2D g2d;
        if (useGPU && vBuffer == null) {
            vBuffer = this.createVolatileImage(Globals.WINDOW_WIDTH, Globals.WINDOW_HEIGHT);
            bufferGraphics = vBuffer.createGraphics();
        }

        if (useGPU && vBuffer != null) {
            g2d = bufferGraphics;
        } else {
            g2d = (Graphics2D) g;
        }

        super.paintComponent(g2d);
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
