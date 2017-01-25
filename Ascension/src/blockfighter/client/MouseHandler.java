package blockfighter.client;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class MouseHandler implements MouseListener, MouseMotionListener {

    @Override
    public void mouseClicked(final MouseEvent e) {
        Core.getLogicModule().getScreen().mouseClicked(e);
    }

    @Override
    public void mousePressed(final MouseEvent e) {
        Core.getLogicModule().getScreen().mousePressed(e);
    }

    @Override
    public void mouseReleased(final MouseEvent e) {
        Core.getLogicModule().getScreen().mouseReleased(e);
    }

    @Override
    public void mouseEntered(final MouseEvent e) {
        Core.getLogicModule().getScreen().mouseEntered(e);
    }

    @Override
    public void mouseExited(final MouseEvent e) {
        Core.getLogicModule().getScreen().mouseExited(e);
    }

    @Override
    public void mouseDragged(final MouseEvent e) {
        Core.getLogicModule().getScreen().mouseDragged(e);
    }

    @Override
    public void mouseMoved(final MouseEvent e) {
        Core.getLogicModule().getScreen().mouseMoved(e);
    }

}
