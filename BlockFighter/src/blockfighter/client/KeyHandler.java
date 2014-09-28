package blockfighter.client;

import blockfighter.client.net.PacketSender;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.DatagramSocket;

/**
 *
 * @author Ken
 */
public class KeyHandler implements KeyListener {

    private PacketSender sender = null;
    DatagramSocket socket = null;
    LogicModule logic = null;

    public KeyHandler(LogicModule logic, DatagramSocket socket) {
        this.logic = logic;
        this.socket = socket;
        sender = new PacketSender(socket);
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
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
                    sender.sendKnockTest(logic.getMyIndex());
                    logic.attack();
                }
                break;
        }
    }
}
