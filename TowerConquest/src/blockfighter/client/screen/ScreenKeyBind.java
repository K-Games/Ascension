package blockfighter.client.screen;

import blockfighter.client.Globals;
import blockfighter.client.LogicModule;
import blockfighter.client.SaveData;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

/**
 *
 * @author Ken Kwan
 */
public class ScreenKeyBind extends ScreenMenu {

    private SaveData c;
    private Rectangle2D.Double[] keyBox = new Rectangle2D.Double[Globals.NUM_KEYBINDS];
    private int selectedKeyBox = -1;

    public ScreenKeyBind(LogicModule l) {
        super(l);
        c = l.getSelectedChar();

        for (int i = 0; i < 12; i++) {
            keyBox[i] = new Rectangle2D.Double(365, 45 + (i * 50), 180, 30);
        }
        for (int i = 12; i < keyBox.length; i++) {
            keyBox[i] = new Rectangle2D.Double(800, 45 + ((i - 12) * 50), 180, 30);
        }
    }

    @Override
    public void draw(Graphics2D g) {
        BufferedImage bg = Globals.MENU_BG[4];
        g.drawImage(bg, 0, 0, null);

        g.setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
        drawButtons(g);

        drawMenuButton(g);
        super.draw(g);
    }

    private void drawButtons(Graphics2D g) {
        BufferedImage button = Globals.MENU_BUTTON[Globals.BUTTON_SMALLRECT];
        for (int i = 0; i < keyBox.length; i++) {
            g.drawImage(button, (int) keyBox[i].x, (int) keyBox[i].y, null);
            g.setFont(Globals.ARIAL_18PT);
            if (selectedKeyBox == i) {
                drawStringOutline(g, "Assign a key", (int) (keyBox[i].x + 40), (int) (keyBox[i].y + 25), 1);
                g.setColor(Color.WHITE);
                g.drawString("Assign a key", (int) (keyBox[i].x + 40), (int) (keyBox[i].y + 25));
            } else {
                String key = "Not Assigned";
                if (c.getKeyBind()[i] != -1) {
                    key = KeyEvent.getKeyText(c.getKeyBind()[i]);
                }
                int width = g.getFontMetrics().stringWidth(key);
                drawStringOutline(g, key, (int) (keyBox[i].x + 90 - width / 2), (int) (keyBox[i].y + 25), 1);
                g.setColor(Color.WHITE);
                g.drawString(key, (int) (keyBox[i].x + 90 - width / 2), (int) (keyBox[i].y + 25));
            }
        }

        for (int i = 0; i < 12; i++) {
            g.setFont(Globals.ARIAL_18PT);
            drawStringOutline(g, "Hotkey Bar " + (i + 1) + ": ", 240, (int) (keyBox[i].y + 25), 1);
            g.setColor(Color.WHITE);
            g.drawString("Hotkey Bar " + (i + 1) + ": ", 240, (int) (keyBox[i].y + 25));
        }

        g.setFont(Globals.ARIAL_18PT);
        drawStringOutline(g, "Walk Left: ", 690, (int) (keyBox[Globals.KEYBIND_LEFT].y + 25), 1);
        drawStringOutline(g, "Walk Right: ", 690, (int) (keyBox[Globals.KEYBIND_RIGHT].y + 25), 1);
        drawStringOutline(g, "Jump: ", 690, (int) (keyBox[Globals.KEYBIND_JUMP].y + 25), 1);
        drawStringOutline(g, "Down: ", 690, (int) (keyBox[Globals.KEYBIND_DOWN].y + 25), 1);

        g.setColor(Color.WHITE);
        g.drawString("Walk Left: ", 690, (int) (keyBox[Globals.KEYBIND_LEFT].y + 25));
        g.drawString("Walk Right: ", 690, (int) (keyBox[Globals.KEYBIND_RIGHT].y + 25));
        g.drawString("Jump: ", 690, (int) (keyBox[Globals.KEYBIND_JUMP].y + 25));
        g.drawString("Down: ", 690, (int) (keyBox[Globals.KEYBIND_DOWN].y + 25));
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            selectedKeyBox = -1;
            return;
        }

        if (selectedKeyBox != -1) {
            if ((e.getKeyCode() >= KeyEvent.VK_0
                    && e.getKeyCode() <= KeyEvent.VK_9)
                    || (e.getKeyCode() >= KeyEvent.VK_A
                    && e.getKeyCode() <= KeyEvent.VK_Z)
                    || e.getKeyCode() <= KeyEvent.VK_SPACE
                    || (e.getKeyCode() >= KeyEvent.VK_LEFT
                    && e.getKeyCode() <= KeyEvent.VK_DOWN)) {
                c.setKeyBind(selectedKeyBox, e.getKeyCode());
                selectedKeyBox = -1;
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        super.mouseReleased(e);
        for (int i = 0; i < keyBox.length; i++) {
            if (keyBox[i].contains(e.getPoint())) {
                selectedKeyBox = i;
                return;
            }
        }
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

    @Override
    public void unload() {
    }

}
