package blockfighter.client.screen;

import blockfighter.client.Globals;
import blockfighter.client.SaveData;
import blockfighter.client.entities.player.skills.Skill;
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
    private Rectangle2D.Double[] addBox = new Rectangle2D.Double[Skill.NUM_SKILLS];
    private Rectangle2D.Double resetBox;

    //Actual skills stored
    private Skill[] hotkeyList;
    private Skill[] skillList;

    private Point mousePos;

    private int drawInfoSkill = -1, drawInfoHotkey = -1;
    private int dragSkill = -1, dragHotkey = -1;

    public ScreenSkills() {
        c = logic.getSelectedChar();
        hotkeyList = c.getHotkeys();
        skillList = c.getSkills();

        skillSlots[Skill.SWORD_DRIVE] = new Rectangle2D.Double(241, 55, 60, 60);
        skillSlots[Skill.SWORD_SLASH] = new Rectangle2D.Double(241, 145, 60, 60);
        skillSlots[Skill.SWORD_MULTI] = new Rectangle2D.Double(241, 235, 60, 60);
        skillSlots[Skill.SWORD_VORPAL] = new Rectangle2D.Double(241, 325, 60, 60);
        skillSlots[Skill.SWORD_CINDER] = new Rectangle2D.Double(241, 415, 60, 60);
        skillSlots[Skill.SWORD_TAUNT] = new Rectangle2D.Double(241, 505, 60, 60);

        skillSlots[Skill.BOW_ARC] = new Rectangle2D.Double(506, 55, 60, 60);
        skillSlots[Skill.BOW_RAPID] = new Rectangle2D.Double(506, 145, 60, 60);
        skillSlots[Skill.BOW_POWER] = new Rectangle2D.Double(506, 235, 60, 60);
        skillSlots[Skill.BOW_VOLLEY] = new Rectangle2D.Double(506, 325, 60, 60);
        skillSlots[Skill.BOW_STORM] = new Rectangle2D.Double(506, 415, 60, 60);
        skillSlots[Skill.BOW_FROST] = new Rectangle2D.Double(506, 505, 60, 60);

        skillSlots[Skill.SHIELD_FORTIFY] = new Rectangle2D.Double(767, 55, 60, 60);
        skillSlots[Skill.SHIELD_IRONFORT] = new Rectangle2D.Double(767, 145, 60, 60);
        skillSlots[Skill.SHIELD_CHARGE] = new Rectangle2D.Double(767, 235, 60, 60);
        skillSlots[Skill.SHIELD_REFLECT] = new Rectangle2D.Double(767, 325, 60, 60);
        skillSlots[Skill.SHIELD_5] = new Rectangle2D.Double(767, 415, 60, 60);
        skillSlots[Skill.SHIELD_6] = new Rectangle2D.Double(767, 505, 60, 60);

        skillSlots[Skill.PASSIVE_1] = new Rectangle2D.Double(1050, 55, 60, 60);
        skillSlots[Skill.PASSIVE_2] = new Rectangle2D.Double(1050, 140, 60, 60);
        skillSlots[Skill.PASSIVE_3] = new Rectangle2D.Double(1050, 225, 60, 60);
        skillSlots[Skill.PASSIVE_4] = new Rectangle2D.Double(1050, 310, 60, 60);
        skillSlots[Skill.PASSIVE_5] = new Rectangle2D.Double(1050, 395, 60, 60);
        skillSlots[Skill.PASSIVE_6] = new Rectangle2D.Double(1050, 480, 60, 60);

        skillSlots[Skill.PASSIVE_7] = new Rectangle2D.Double(1160, 55, 60, 60);
        skillSlots[Skill.PASSIVE_8] = new Rectangle2D.Double(1160, 140, 60, 60);
        skillSlots[Skill.PASSIVE_9] = new Rectangle2D.Double(1160, 225, 60, 60);
        skillSlots[Skill.PASSIVE_10] = new Rectangle2D.Double(1160, 310, 60, 60);
        skillSlots[Skill.PASSIVE_11] = new Rectangle2D.Double(1160, 395, 60, 60);
        skillSlots[Skill.PASSIVE_12] = new Rectangle2D.Double(1160, 480, 60, 60);
        for (int i = 0; i < hotkeySlots.length; i++) {
            hotkeySlots[i] = new Rectangle2D.Double(240 + (i * 64), 605, 60, 60);
        }
        for (int i = 0; i < 18; i++) {
            addBox[i] = new Rectangle2D.Double(skillSlots[i].x + 140, skillSlots[i].y + 32, 30, 23);
        }

        for (int i = 18; i < addBox.length; i++) {
            addBox[i] = new Rectangle2D.Double(skillSlots[i].x + 58, skillSlots[i].y + 37, 30, 23);
        }
        resetBox = new Rectangle2D.Double(1050, 630, 180, 30);
    }

    @Override
    public void draw(Graphics2D g) {
        BufferedImage bg = Globals.MENU_BG[3];
        g.drawImage(bg, 0, 0, null);

        g.setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);

        g.setFont(Globals.ARIAL_18PT);
        drawStringOutline(g, "Skill Points: " + (int) c.getBaseStats()[Globals.STAT_SKILLPOINTS], 1080, 620, 1);
        g.setColor(Color.WHITE);
        g.drawString("Skill Points: " + (int) c.getBaseStats()[Globals.STAT_SKILLPOINTS], 1080, 620);

        BufferedImage button = Globals.MENU_BUTTON[Globals.BUTTON_SMALLRECT];
        g.drawImage(button, (int) resetBox.x, (int) resetBox.y, null);
        g.setFont(Globals.ARIAL_18PT);
        drawStringOutline(g, "Reset Skills", 1090, 657, 1);
        g.setColor(Color.WHITE);
        g.drawString("Reset Skills", 1090, 657);
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
        g.setFont(Globals.ARIAL_18PT);
        drawStringOutline(g, "Sword", 325, 45, 1);
        g.setColor(Color.WHITE);
        g.drawString("Sword", 325, 45);

        drawStringOutline(g, "Bow", 600, 45, 1);
        g.setColor(Color.WHITE);
        g.drawString("Bow", 600, 45);

        drawStringOutline(g, "Shield", 850, 45, 1);
        g.setColor(Color.WHITE);
        g.drawString("Shield", 850, 45);

        drawStringOutline(g, "Passive", 1105, 45, 1);
        g.setColor(Color.WHITE);
        g.drawString("Passive", 1105, 45);
        for (int i = 0; i < hotkeySlots.length; i++) {
            g.drawImage(button, (int) hotkeySlots[i].x, (int) hotkeySlots[i].y, null);
            if (hotkeyList[i] != null) {
                hotkeyList[i].draw(g, (int) hotkeySlots[i].x, (int) hotkeySlots[i].y);
            }
            String key = "?";
            if (c.getKeyBind()[i] != -1) {
                key = KeyEvent.getKeyText(c.getKeyBind()[i]);
            }
            int width = g.getFontMetrics().stringWidth(key);
            g.setFont(Globals.ARIAL_15PT);
            drawStringOutline(g, key, (int) hotkeySlots[i].x + 30 - width / 2, (int) hotkeySlots[i].y + 75, 1);
            g.setColor(Color.WHITE);
            g.drawString(key, (int) hotkeySlots[i].x + 30 - width / 2, (int) hotkeySlots[i].y + 75);
        }

        for (int i = 0; i < 18; i++) {
            g.drawImage(button, (int) skillSlots[i].x, (int) skillSlots[i].y, null);
            skillList[i].draw(g, (int) skillSlots[i].x, (int) skillSlots[i].y);
            g.setFont(Globals.ARIAL_15PT);
            drawStringOutline(g, skillList[i].getSkillName(), (int) skillSlots[i].x + 70, (int) skillSlots[i].y + 20, 1);
            drawStringOutline(g, "Level: " + skillList[i].getLevel(), (int) skillSlots[i].x + 70, (int) skillSlots[i].y + 50, 1);
            g.setColor(Color.WHITE);
            g.drawString(skillList[i].getSkillName(), (int) skillSlots[i].x + 70, (int) skillSlots[i].y + 20);
            g.drawString("Level: " + skillList[i].getLevel(), (int) skillSlots[i].x + 70, (int) skillSlots[i].y + 50);

            if (c.getBaseStats()[Globals.STAT_SKILLPOINTS] > 0 && !skillList[i].isMaxed()) {
                button = Globals.MENU_BUTTON[Globals.BUTTON_ADDSTAT];
                g.drawImage(button, (int) addBox[i].x, (int) addBox[i].y, null);
                g.setFont(Globals.ARIAL_15PT);
                drawStringOutline(g, "+", (int) addBox[i].x + 11, (int) addBox[i].y + 18, 1);
                g.setColor(Color.WHITE);
                g.drawString("+", (int) addBox[i].x + 11, (int) addBox[i].y + 18);
            }
        }

        for (int i = 18; i < skillSlots.length; i++) {
            g.drawImage(button, (int) skillSlots[i].x, (int) skillSlots[i].y, null);
            skillList[i].draw(g, (int) skillSlots[i].x, (int) skillSlots[i].y);
            g.setFont(Globals.ARIAL_15PT);
            drawStringOutline(g, "Level: " + skillList[i].getLevel(), (int) skillSlots[i].x, (int) skillSlots[i].y + 80, 1);
            g.setColor(Color.WHITE);
            g.drawString("Level: " + skillList[i].getLevel(), (int) skillSlots[i].x, (int) skillSlots[i].y + 80);

            if (c.getBaseStats()[Globals.STAT_SKILLPOINTS] > 0 && !skillList[i].isMaxed()) {
                button = Globals.MENU_BUTTON[Globals.BUTTON_ADDSTAT];
                g.drawImage(button, (int) addBox[i].x, (int) addBox[i].y, null);
                g.setFont(Globals.ARIAL_15PT);
                drawStringOutline(g, "+", (int) addBox[i].x + 11, (int) addBox[i].y + 18, 1);
                g.setColor(Color.WHITE);
                g.drawString("+", (int) addBox[i].x + 11, (int) addBox[i].y + 18);
            }
        }
    }

    private void drawSkillInfo(Graphics2D g, Rectangle2D.Double box, Skill skill) {
        skill.drawInfo(g, (int) box.x, (int) box.y);
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
            if (resetBox.contains(e.getPoint())) {
                c.resetSkill();
                return;
            }
            for (byte i = 0; i < addBox.length; i++) {
                if (addBox[i].contains(e.getPoint())) {
                    if (c.getBaseStats()[Globals.STAT_SKILLPOINTS] > 0 && !skillList[i].isMaxed()) {
                        c.addSkill(i);
                        return;
                    }
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

    @Override
    public void unload() {
    }

}
