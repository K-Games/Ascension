package blockfighter.client;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class FocusHandler implements FocusListener {

    private static LogicModule logic = null;

    public static void init() {
        logic = Main.getLogicModule();
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
