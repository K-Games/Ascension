package blockfighter.client.screen.window;

import blockfighter.client.screen.Screen;
import blockfighter.shared.Globals;
import java.awt.Graphics2D;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;

public abstract class Window implements KeyListener, MouseListener, MouseMotionListener {

    protected final Screen parentScreen;

    protected Point2D.Double mousePos = null;

    public Window(final Screen parent) {
        this.parentScreen = parent;
    }

    public abstract void update();

    public abstract void draw(Graphics2D g);

    @Override
    public void mouseDragged(MouseEvent e) {
        mouseMoved(e);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        Point2D.Double scaled;
        if (Globals.WINDOW_SCALE_ENABLED) {
            scaled = new Point2D.Double(e.getX() / Globals.WINDOW_SCALE, e.getY() / Globals.WINDOW_SCALE);
        } else {
            scaled = new Point2D.Double(e.getX(), e.getY());
        }
        this.mousePos = scaled;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        mouseMoved(e);
    }
}
