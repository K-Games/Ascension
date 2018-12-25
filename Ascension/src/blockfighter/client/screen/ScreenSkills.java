package blockfighter.client.screen;

import blockfighter.client.Core;
import blockfighter.client.entities.player.skills.PlayerSkillData;
import blockfighter.client.savedata.SaveData;
import blockfighter.client.screen.window.skill.WindowSkill;
import blockfighter.client.screen.window.skill.WindowSkillAll;
import blockfighter.client.screen.window.skill.WindowSkillDebug;
import blockfighter.shared.Globals;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import javax.swing.SwingUtilities;

public class ScreenSkills extends ScreenMenu {

    private static final String RESET_SKILLS_TEXT = "Reset Skills";
    private static final String SKILL_POINTS_TEXT = "Skill Points: ";
    private static final String UNKNOWN_KEY_TEXT = "?";

    private final SaveData saveData;
    // Slots(x,y) in the GUI
    private static final Rectangle2D.Double[] HOTKEY_SLOTS = new Rectangle2D.Double[12];

    private static final Rectangle2D.Double RESET_BOX;
    private static final int HOTKEY_BOX_X = 240, HOTKEY_BOX_Y = 605;

    // Actual skills stored
    private final HashMap<Byte, Byte> hotkeyList;
    private final HashMap<Byte, PlayerSkillData> skillList;

    private byte drawInfoHotkey = -1;
    private byte dragHotkey = -1;
    private WindowSkill skillWindow = (!Globals.DEBUG_MODE) ? new WindowSkillAll(this) : new WindowSkillDebug(this);

    static {
        for (int i = 0; i < HOTKEY_SLOTS.length; i++) {
            HOTKEY_SLOTS[i] = new Rectangle2D.Double(HOTKEY_BOX_X + (i * 64), HOTKEY_BOX_Y, 60, 60);
        }
        RESET_BOX = new Rectangle2D.Double(1050, 630, 180, 40);
    }

    public ScreenSkills() {
        this.saveData = Core.getLogicModule().getSelectedChar();
        this.hotkeyList = this.saveData.getHotkeys();
        this.skillList = this.saveData.getSkills();
    }

    @Override
    public void draw(final Graphics2D g) {
        final BufferedImage bg = Globals.MENU_BG[1];
        g.drawImage(bg, 0, 0, null);

        g.setFont(Globals.ARIAL_18PT);
        drawStringOutline(g, SKILL_POINTS_TEXT + (int) this.saveData.getBaseStats()[Globals.STAT_SKILLPOINTS], 1080, 620, 1);
        g.setColor(Color.WHITE);
        g.drawString(SKILL_POINTS_TEXT + (int) this.saveData.getBaseStats()[Globals.STAT_SKILLPOINTS], 1080, 620);

        final BufferedImage button = Globals.MENU_BUTTON[Globals.BUTTON_SMALLRECT];
        g.drawImage(button, (int) RESET_BOX.x, (int) RESET_BOX.y, null);
        g.setFont(Globals.ARIAL_18PT);
        drawStringOutline(g, RESET_SKILLS_TEXT, 1090, 657, 1);
        g.setColor(Color.WHITE);
        g.drawString(RESET_SKILLS_TEXT, 1090, 657);
        drawSlots(g);
        this.skillWindow.draw(g);

        drawMenuButton(g);

        if (this.dragHotkey != -1) {
            this.skillList.get(this.hotkeyList.get(this.dragHotkey)).draw(g, (int) this.mousePos.x, (int) this.mousePos.y);
        }

        if (this.skillWindow.getDraggingSkillCode() != -1) {
            this.skillList.get(this.skillWindow.getDraggingSkillCode()).draw(g, (int) this.mousePos.x, (int) this.mousePos.y);
        }

        super.draw(g);
        drawSkillInfo(g);
        this.skillWindow.drawSkillInfo(g);
    }

    private void drawSkillInfo(final Graphics2D g) {
        if (this.drawInfoHotkey != -1) {
            PlayerSkillData skill = this.skillList.get(this.hotkeyList.get(this.drawInfoHotkey));
            if (this.saveData.getTotalStats()[Globals.STAT_LEVEL] >= skill.getSkillData().getReqLevel()) {
                drawSkillInfo(g, HOTKEY_SLOTS[this.drawInfoHotkey], skill);
            }
        }
    }

    private void drawSlots(final Graphics2D g) {
        BufferedImage button = Globals.MENU_BUTTON[Globals.BUTTON_SLOT];

        g.setColor(Screen.BOX_BG_COLOR);
        g.fillRoundRect(HOTKEY_BOX_X - 10, HOTKEY_BOX_Y - 10, 784, 90, 15, 15);

        for (byte i = 0; i < HOTKEY_SLOTS.length; i++) {
            g.drawImage(button, (int) HOTKEY_SLOTS[i].x, (int) HOTKEY_SLOTS[i].y, null);
            if (this.hotkeyList.get(i) != null) {
                PlayerSkillData skill = this.skillList.get(this.hotkeyList.get(i));
                skill.draw(g, (int) HOTKEY_SLOTS[i].x, (int) HOTKEY_SLOTS[i].y);
            }
            String key = UNKNOWN_KEY_TEXT;
            if (this.saveData.getKeyBind().get(i) != null) {
                key = KeyEvent.getKeyText(this.saveData.getKeyBind().get(i));
            }
            final int width = g.getFontMetrics().stringWidth(key);
            g.setFont(Globals.ARIAL_15PT);
            drawStringOutline(g, key, (int) HOTKEY_SLOTS[i].x + 30 - width / 2, (int) HOTKEY_SLOTS[i].y + 75, 1);
            g.setColor(Color.WHITE);
            g.drawString(key, (int) HOTKEY_SLOTS[i].x + 30 - width / 2, (int) HOTKEY_SLOTS[i].y + 75);
        }

    }

    private void drawSkillInfo(final Graphics2D g, final Rectangle2D.Double box, final PlayerSkillData skill) {
        skill.drawInfo(g, (int) box.x, (int) box.y);
    }

    @Override
    public void keyTyped(final KeyEvent e) {

    }

    @Override
    public void keyPressed(final KeyEvent e) {

    }

    @Override
    public void keyReleased(final KeyEvent e) {
    }

    @Override
    public void mouseClicked(final MouseEvent e) {

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
        final byte drSkill = this.skillWindow.getDraggingSkillCode(), drHK = this.dragHotkey;
        this.dragHotkey = -1;

        super.mouseReleased(e);
        this.skillWindow.mouseReleased(e);

        if (SwingUtilities.isLeftMouseButton(e)) {
            for (byte i = 0; i < HOTKEY_SLOTS.length; i++) {
                if (HOTKEY_SLOTS[i].contains(scaled)) {
                    if (drSkill != -1) {
                        if (this.saveData.getTotalStats()[Globals.STAT_LEVEL] >= this.skillList.get(drSkill).getSkillData().getReqLevel()) {
                            this.hotkeyList.put(i, this.skillList.get(drSkill).getSkillCode());
                        }
                        return;
                    }
                    if (drHK != -1) {
                        final Byte temp = this.hotkeyList.get(i);
                        this.hotkeyList.put(i, this.hotkeyList.get(drHK));
                        this.hotkeyList.put(drHK, temp);
                        return;
                    }
                    return;
                }
            }
            if (RESET_BOX.contains(scaled)) {
                this.saveData.resetSkill();
            }
        }

    }

    @Override
    public void mouseEntered(final MouseEvent e) {

    }

    @Override
    public void mouseExited(final MouseEvent e) {

    }

    @Override
    public void mouseDragged(final MouseEvent e) {
        Point2D.Double scaled;
        if (Globals.WINDOW_SCALE_ENABLED) {
            scaled = new Point2D.Double(e.getX() / Globals.WINDOW_SCALE, e.getY() / Globals.WINDOW_SCALE);
        } else {
            scaled = new Point2D.Double(e.getX(), e.getY());
        }
        mouseMoved(e);

        final byte drSkill = this.skillWindow.getDraggingSkillCode();
        if (SwingUtilities.isLeftMouseButton(e)) {
            if (drSkill == -1 && this.dragHotkey == -1) {
                for (byte i = 0; i < HOTKEY_SLOTS.length; i++) {
                    if (HOTKEY_SLOTS[i].contains(scaled) && this.hotkeyList.get(i) != null) {
                        this.dragHotkey = i;
                        return;
                    }
                }

                this.skillWindow.mouseDragged(e);
            }
        }
    }

    @Override
    public void mouseMoved(final MouseEvent e) {
        Point2D.Double scaled;
        if (Globals.WINDOW_SCALE_ENABLED) {
            scaled = new Point2D.Double(e.getX() / Globals.WINDOW_SCALE, e.getY() / Globals.WINDOW_SCALE);
        } else {
            scaled = new Point2D.Double(e.getX(), e.getY());
        }
        this.mousePos = scaled;
        this.drawInfoHotkey = -1;
        if (this.skillWindow.getDraggingSkillCode() == -1 && this.dragHotkey == -1) {
            for (byte i = 0; i < HOTKEY_SLOTS.length; i++) {
                if (HOTKEY_SLOTS[i].contains(scaled) && this.hotkeyList.get(i) != null) {
                    this.drawInfoHotkey = i;
                    return;
                }
            }
        }
        this.skillWindow.mouseMoved(e);
    }

    @Override
    public void unload() {
        SaveData.writeSaveData(Core.getLogicModule().getSelectedChar().getSaveNum(), Core.getLogicModule().getSelectedChar());
    }

    public byte getDragHotKey() {
        return this.dragHotkey;
    }
}
