package blockfighter.client;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

/**
 *
 * @author Ken Kwan
 */
public class FocusHandler implements FocusListener {

    private static LogicModule logic = null;

    public static void setLogic(final LogicModule l) {
        logic = l;
    }

    @Override
    public void focusGained(FocusEvent e) {
        logic.getScreen().focusGained(e);
    }

    @Override
    public void focusLost(FocusEvent e) {
        logic.getScreen().focusLost(e);
    }
}
