package blockfighter.client;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class MouseHandler implements MouseListener, MouseMotionListener {

    private static LogicModule logic;

    public static void init() {
        logic = Main.getLogicModule();
    }

    @Override
    public void mouseClicked(final MouseEvent e) {
        logic.getScreen().mouseClicked(e);
    }

    @Override
    public void mousePressed(final MouseEvent e) {
        logic.getScreen().mousePressed(e);
    }

    @Override
    public void mouseReleased(final MouseEvent e) {
        logic.getScreen().mouseReleased(e);
    }

    @Override
    public void mouseEntered(final MouseEvent e) {
        logic.getScreen().mouseEntered(e);
    }

    @Override
    public void mouseExited(final MouseEvent e) {
        logic.getScreen().mouseExited(e);
    }

    @Override
    public void mouseDragged(final MouseEvent e) {
        logic.getScreen().mouseDragged(e);
    }

    @Override
    public void mouseMoved(final MouseEvent e) {
        logic.getScreen().mouseMoved(e);
    }

}
