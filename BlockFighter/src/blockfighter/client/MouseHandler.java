package blockfighter.client;

import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/**
 *
 * @author Ken
 */
public class MouseHandler implements MouseListener, MouseMotionListener {

    private LogicModule logic;

    public MouseHandler(LogicModule l) {
        logic = l;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        switch (logic.getScreen()) {
            case Globals.SCREEN_CHAR_SELECT:
                charsSelect_mouseClick(e);
                break;
        }
    }

    private void charsSelect_mouseClick(MouseEvent e) {
        if (new Rectangle(550,550,214,112).contains(e.getPoint())){
            logic.sendLogin();
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

}
