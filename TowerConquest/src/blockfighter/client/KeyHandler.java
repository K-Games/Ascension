package blockfighter.client;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 *
 * @author Ken Kwan
 */
public class KeyHandler implements KeyListener {

    private static LogicModule logic = null;

    public static void setLogic(final LogicModule l) {
        logic = l;
    }

    @Override
    public void keyTyped(final KeyEvent e) {
        logic.getScreen().keyTyped(e);
    }

    @Override
    public void keyPressed(final KeyEvent e) {
        logic.getScreen().keyPressed(e);
    }

    @Override
    public void keyReleased(final KeyEvent e) {
        logic.getScreen().keyReleased(e);
    }
}
