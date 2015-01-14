package blockfighter.client;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 *
 * @author Ken
 */
public class KeyHandler implements KeyListener {

    LogicModule logic = null;

    public KeyHandler(LogicModule logic) {
        this.logic = logic;
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
