package blockfighter.client.screen;

import blockfighter.client.Globals;
import blockfighter.client.LogicModule;
import blockfighter.client.SaveData;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

/**
 *
 * @author Ken
 */
public class ScreenUpgrade extends ScreenMenu {

    private SaveData c;

    public ScreenUpgrade(LogicModule l) {
        super(l);
        c = l.getSelectedChar();
    }

    @Override
    public void draw(Graphics g) {
        BufferedImage bg = Globals.MENU_BG[0];
        g.drawImage(bg, 0, 0, null);

        super.draw(g);
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void mouseClicked(MouseEvent e) {

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
