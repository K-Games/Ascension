package blockfighter.client.screen;

import blockfighter.client.Globals;
import blockfighter.client.LogicModule;
import blockfighter.client.SaveData;
import blockfighter.client.entities.skills.Skill;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import javax.swing.SwingUtilities;

/**
 *
 * @author Ken Kwan
 */
public class ScreenSkills extends ScreenMenu {

    private SaveData c;
    //Slots(x,y) in the GUI
    private Rectangle2D.Double[] hotkeySlots = new Rectangle2D.Double[12];
    private Rectangle2D.Double[] skillSlots = new Rectangle2D.Double[Skill.NUM_SKILLS];

    //Actual skills stored
    private Skill[] hotkeyList;
    private Skill[] skillList;

    private Point mousePos;

    private int drawInfoSkill = -1, drawInfoHotkey = -1;
    private int dragSkill = -1, dragHotkey = -1;

    public ScreenSkills(LogicModule l) {
        super(l);
        c = l.getSelectedChar();
        hotkeyList = c.getHotkeys();
        skillList = c.getSkills();
        for (int i = 0; i < hotkeySlots.length; i++) {
            hotkeySlots[i] = new Rectangle2D.Double(270 + (i * 62), 600, 60, 60);
        }
        //init skill slots locations
    }

    @Override
    public void draw(Graphics2D g) {
        BufferedImage bg = Globals.MENU_BG[2];
        g.drawImage(bg, 0, 0, null);

        g.setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);

        drawSlots(g);
        drawMenuButton(g);

        if (dragSkill != -1) {
            skillList[dragSkill].draw(g, mousePos.x, mousePos.y);
        } else if (dragHotkey != -1) {
            hotkeyList[dragHotkey].draw(g, mousePos.x, mousePos.y);
        }

        super.draw(g);
        drawSkillInfo(g);
    }

    private void drawSkillInfo(Graphics2D g) {
        if (drawInfoSkill != -1) {
            drawSkillInfo(g, skillSlots[drawInfoSkill], skillList[drawInfoSkill]);
        } else if (drawInfoHotkey != -1) {
            drawSkillInfo(g, hotkeySlots[drawInfoHotkey], hotkeyList[drawInfoHotkey]);
        }
    }

    private void drawSlots(Graphics2D g) {
        BufferedImage button = Globals.MENU_BUTTON[Globals.BUTTON_SLOT];

        for (int i = 0; i < hotkeySlots.length; i++) {
            g.drawImage(button, (int) hotkeySlots[i].x, (int) hotkeySlots[i].y, null);
            if (hotkeyList[i] != null) {
                hotkeyList[i].draw(g, (int) hotkeySlots[i].x, (int) hotkeySlots[i].y);
            }
        }

        for (int i = 0; i < skillList.length; i++) {
            g.drawImage(button, (int) skillSlots[i].x, (int) skillSlots[i].y, null);
            if (skillList[i] != null) {
                skillList[i].draw(g, (int) skillSlots[i].x, (int) skillSlots[i].y);
            }
        }
    }

    private void drawSkillInfo(Graphics2D g, Rectangle2D.Double box, Skill e) {
        if (e == null) {
            return;
        }
        g.setColor(new Color(30, 30, 30, 185));
        int y = (int) box.y;
        int x = (int) box.x;
        int boxHeight = 20, boxWidth = 200;

        if (y + boxHeight > 720) {
            y = 700 - boxHeight;
        }

        if (x + 30 + boxWidth > 1280) {
            x = 1040;
        }
        g.fillRect(x + 30, y, boxWidth, boxHeight);
        g.setColor(Color.BLACK);
        g.drawRect(x + 30, y, boxWidth, boxHeight);
        g.drawRect(x + 31, y + 1, 198, boxHeight - 2);

        g.setFont(Globals.ARIAL_15PT);
        //draw Skill Info text
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
        int drSkill = dragSkill, drHK = dragHotkey;
        dragSkill = -1;
        dragHotkey = -1;

        super.mouseReleased(e);
        if (SwingUtilities.isLeftMouseButton(e)) {
            for (int i = 0; i < hotkeySlots.length; i++) {
                if (hotkeySlots[i].contains(e.getPoint())) {
                    if (drSkill != -1) {
                        hotkeyList[i] = skillList[drSkill];
                        return;
                    }
                    if (drHK != -1) {
                        Skill temp = hotkeyList[i];
                        hotkeyList[i] = hotkeyList[drHK];
                        hotkeyList[drHK] = temp;
                        return;
                    }
                    return;
                }
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
        mouseMoved(e);
        if (SwingUtilities.isLeftMouseButton(e)) {
            if (dragSkill == -1 && dragHotkey == -1) {
                for (int i = 0; i < hotkeySlots.length; i++) {
                    if (hotkeySlots[i].contains(e.getPoint()) && hotkeyList[i] != null) {
                        dragHotkey = i;
                        return;
                    }
                }

                for (byte i = 0; i < skillSlots.length; i++) {
                    if (skillSlots[i].contains(e.getPoint()) && skillSlots[i] != null) {
                        dragSkill = i;
                        return;
                    }
                }
            }
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mousePos = e.getPoint();
        drawInfoSkill = -1;
        drawInfoHotkey = -1;
        for (int i = 0; i < hotkeySlots.length; i++) {
            if (hotkeySlots[i].contains(e.getPoint()) && hotkeyList[i] != null) {
                drawInfoHotkey = i;
                return;
            }
        }

        for (byte i = 0; i < skillSlots.length; i++) {
            if (skillSlots[i].contains(e.getPoint()) && skillSlots[i] != null) {
                drawInfoSkill = i;
                return;
            }
        }
    }

}
