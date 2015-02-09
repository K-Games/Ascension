package blockfighter.client;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 *
 * @author Ken Kwan
 */
public class KeyHandler implements KeyListener {

    private static LogicModule logic = null;

    public static void setLogic(LogicModule l) {
        logic = l;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        logic.getScreen().keyTyped(e);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        logic.getScreen().keyPressed(e);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        logic.getScreen().keyReleased(e);
    }
}
