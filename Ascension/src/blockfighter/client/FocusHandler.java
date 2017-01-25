package blockfighter.client;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class FocusHandler implements FocusListener {

    @Override
    public void focusGained(FocusEvent e) {
        Core.getLogicModule().getScreen().focusGained(e);
    }

    @Override
    public void focusLost(FocusEvent e) {
        Core.getLogicModule().getScreen().focusLost(e);
    }
}
