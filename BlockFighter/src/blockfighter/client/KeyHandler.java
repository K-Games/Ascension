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

    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (logic.getScreen()) {
            case Globals.SCREEN_INGAME:
                ingame_keyPressed(e);
                break;
        }
    }

    private void ingame_keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                logic.setKeyDown(Globals.UP, true);
                break;
            case KeyEvent.VK_DOWN:
                logic.setKeyDown(Globals.DOWN, true);
                break;
            case KeyEvent.VK_LEFT:
                logic.setKeyDown(Globals.LEFT, true);
                break;
            case KeyEvent.VK_RIGHT:
                logic.setKeyDown(Globals.RIGHT, true);
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (logic.getScreen()) {
            case Globals.SCREEN_INGAME:
                ingame_keyReleased(e);
                break;
        }
    }
    
    public void ingame_keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                logic.setKeyDown(Globals.UP, false);
                break;
            case KeyEvent.VK_DOWN:
                logic.setKeyDown(Globals.DOWN, false);
                break;
            case KeyEvent.VK_LEFT:
                logic.setKeyDown(Globals.LEFT, false);
                break;
            case KeyEvent.VK_RIGHT:
                logic.setKeyDown(Globals.RIGHT, false);
                break;
            case KeyEvent.VK_A:
                if (logic.canAttack()) {
                    logic.sendAction();
                    logic.attack();
                }
                break;
        }
    }
}
