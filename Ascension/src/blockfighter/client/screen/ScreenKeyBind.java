package blockfighter.client.screen;

import blockfighter.client.Core;
import blockfighter.client.entities.emotes.Emote;
import blockfighter.client.savedata.SaveData;
import blockfighter.shared.Globals;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public class ScreenKeyBind extends ScreenMenu {

    private final static Rectangle2D.Double[] KEY_BOX = new Rectangle2D.Double[Globals.NUM_KEYBINDS];
    private static final String ASSIGN_KEY_TEXT = "Assign a key";
    private static final String DOWN_TEXT = "Down: ";
    private static final String SCOREBOARD_TEXT = "Scoreboard: ";
    private static final String HOTKEY_BAR_TEXT = "Hotkey Bar ";
    private static final String JUMP_TEXT = "Jump: ";
    private static final String UNASSIGNED_KEY_TEXT = "Not Assigned";
    private static final String WALK_LEFT_TEXT = "Walk Left: ";
    private static final String WALK_RIGHT_TEXT = "Walk Right: ";

    static {
        for (int i = 0; i < 12; i++) {
            KEY_BOX[i] = new Rectangle2D.Double(365, 45 + (i * 50), 180, 40);
        }
        for (int i = 12; i < 16; i++) {
            KEY_BOX[i] = new Rectangle2D.Double(800, 45 + ((i - 12) * 50), 180, 40);
        }

        for (int i = 16; i < 16 + Globals.Emotes.values().length / 2; i++) {
            KEY_BOX[i] = new Rectangle2D.Double(650, 300 + ((i - 16) * 50), 180, 40);
        }
        for (int i = 16 + Globals.Emotes.values().length / 2; i < 16 + Globals.Emotes.values().length; i++) {
            KEY_BOX[i] = new Rectangle2D.Double(890, 300 + ((i - 16 - Globals.Emotes.values().length / 2) * 50), 180, 40);
        }
        KEY_BOX[Globals.KEYBIND_SCOREBOARD] = new Rectangle2D.Double(800, 245, 180, 40);
    }

    private final SaveData c;
    private int selectedKeyBox = -1;

    public ScreenKeyBind() {
        this.c = Core.getLogicModule().getSelectedChar();
        Emote.loadEmotes();
    }

    @Override
    public void draw(final Graphics2D g) {
        final BufferedImage bg = Globals.MENU_BG[1];
        g.drawImage(bg, 0, 0, null);

        drawButtons(g);
        drawEmotes(g);
        drawMenuButton(g);
        super.draw(g);
    }

    @Override
    public void keyPressed(final KeyEvent e) {

    }

    @Override
    public void keyReleased(final KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            this.selectedKeyBox = -1;
            return;
        }

        if (this.selectedKeyBox != -1) {
            this.c.setKeyBind(this.selectedKeyBox, e.getKeyCode());
            this.selectedKeyBox = -1;
        }
    }

    @Override
    public void keyTyped(final KeyEvent e) {

    }

    @Override
    public void mouseClicked(final MouseEvent e) {

    }

    @Override
    public void mouseDragged(final MouseEvent e) {

    }

    @Override
    public void mouseEntered(final MouseEvent e) {

    }

    @Override
    public void mouseExited(final MouseEvent e) {

    }

    @Override
    public void mouseMoved(final MouseEvent e) {
    }

    @Override
    public void mousePressed(final MouseEvent e) {

    }

    @Override
    public void mouseReleased(final MouseEvent e) {
        Point2D.Double scaled;
        if (Globals.WINDOW_SCALE_ENABLED) {
            scaled = new Point2D.Double(e.getX() / Globals.WINDOW_SCALE, e.getY() / Globals.WINDOW_SCALE);
        } else {
            scaled = new Point2D.Double(e.getX(), e.getY());
        }
        super.mouseReleased(e);
        for (int i = 0; i < KEY_BOX.length; i++) {
            if (KEY_BOX[i].contains(scaled)) {
                this.selectedKeyBox = i;
                return;
            }
        }
    }

    @Override
    public void unload() {
        Emote.unloadEmotes();
    }

    private void drawButtons(final Graphics2D g) {
        final BufferedImage button = Globals.MENU_BUTTON[Globals.BUTTON_SMALLRECT];
        g.setColor(BOX_BG_COLOR);
        g.fillRoundRect((int) KEY_BOX[16].x - 10 - 50, (int) KEY_BOX[16].y - 10,
                (int) ((KEY_BOX[16 + Globals.Emotes.values().length - 1].x + KEY_BOX[16 + Globals.Emotes.values().length - 1].width + 10) - (KEY_BOX[16].x - 10 - 50)),
                (int) ((KEY_BOX[16 + Globals.Emotes.values().length - 1].y + KEY_BOX[16 + Globals.Emotes.values().length - 1].height + 10) - (KEY_BOX[16].y - 10)), 15, 15);

        for (int i = 0; i < KEY_BOX.length; i++) {
            g.drawImage(button, (int) KEY_BOX[i].x, (int) KEY_BOX[i].y, null);
            g.setFont(Globals.ARIAL_18PT);
            if (this.selectedKeyBox == i) {
                drawStringOutline(g, ASSIGN_KEY_TEXT, (int) (KEY_BOX[i].x + 40), (int) (KEY_BOX[i].y + 25), 1);
                g.setColor(Color.WHITE);
                g.drawString(ASSIGN_KEY_TEXT, (int) (KEY_BOX[i].x + 40), (int) (KEY_BOX[i].y + 25));
            } else {
                String key = UNASSIGNED_KEY_TEXT;
                if (this.c.getKeyBind()[i] != -1) {
                    key = KeyEvent.getKeyText(this.c.getKeyBind()[i]);
                }
                final int width = g.getFontMetrics().stringWidth(key);
                drawStringOutline(g, key, (int) (KEY_BOX[i].x + 90 - width / 2), (int) (KEY_BOX[i].y + 25), 1);
                g.setColor(Color.WHITE);
                g.drawString(key, (int) (KEY_BOX[i].x + 90 - width / 2), (int) (KEY_BOX[i].y + 25));
            }
        }

        for (int i = 0; i < 12; i++) {
            g.setFont(Globals.ARIAL_18PT);
            drawStringOutline(g, HOTKEY_BAR_TEXT + (i + 1) + Globals.COLON_SPACE_TEXT, 240, (int) (KEY_BOX[i].y + 25), 1);
            g.setColor(Color.WHITE);
            g.drawString(HOTKEY_BAR_TEXT + (i + 1) + Globals.COLON_SPACE_TEXT, 240, (int) (KEY_BOX[i].y + 25));
        }

        g.setFont(Globals.ARIAL_18PT);
        drawStringOutline(g, WALK_LEFT_TEXT, 690, (int) (KEY_BOX[Globals.KEYBIND_LEFT].y + 25), 1);
        drawStringOutline(g, WALK_RIGHT_TEXT, 690, (int) (KEY_BOX[Globals.KEYBIND_RIGHT].y + 25), 1);
        drawStringOutline(g, JUMP_TEXT, 690, (int) (KEY_BOX[Globals.KEYBIND_JUMP].y + 25), 1);
        drawStringOutline(g, DOWN_TEXT, 690, (int) (KEY_BOX[Globals.KEYBIND_DOWN].y + 25), 1);
        drawStringOutline(g, SCOREBOARD_TEXT, 690, (int) (KEY_BOX[Globals.KEYBIND_SCOREBOARD].y + 25), 1);

        g.setColor(Color.WHITE);
        g.drawString(WALK_LEFT_TEXT, 690, (int) (KEY_BOX[Globals.KEYBIND_LEFT].y + 25));
        g.drawString(WALK_RIGHT_TEXT, 690, (int) (KEY_BOX[Globals.KEYBIND_RIGHT].y + 25));
        g.drawString(JUMP_TEXT, 690, (int) (KEY_BOX[Globals.KEYBIND_JUMP].y + 25));
        g.drawString(DOWN_TEXT, 690, (int) (KEY_BOX[Globals.KEYBIND_DOWN].y + 25));
        g.drawString(SCOREBOARD_TEXT, 690, (int) (KEY_BOX[Globals.KEYBIND_SCOREBOARD].y + 25));

    }

    private void drawEmotes(final Graphics2D g) {
        int i = 16;
        for (Globals.Emotes emote : Globals.Emotes.values()) {
            if (emote.getSprite() != null) {
                BufferedImage sprite = emote.getSprite()[emote.getSprite().length - 1];
                if (sprite != null) {
                    g.drawImage(sprite, (int) KEY_BOX[i].x - 15 - sprite.getWidth(), (int) KEY_BOX[i].y + 5, null);
                }
            }
            i++;
        }
    }

}
