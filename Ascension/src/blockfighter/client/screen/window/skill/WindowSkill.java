package blockfighter.client.screen.window.skill;

import blockfighter.client.Core;
import blockfighter.client.entities.player.skills.PlayerSkillData;
import blockfighter.client.savedata.SaveData;
import blockfighter.client.screen.Screen;
import blockfighter.client.screen.ScreenSkills;
import blockfighter.client.screen.window.Window;
import blockfighter.shared.Globals;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;
import javax.swing.SwingUtilities;

public abstract class WindowSkill extends Window {

    protected HashMap<Byte, Rectangle2D.Double> SKILL_SLOTS = new HashMap<>(6);
    protected HashMap<Byte, Rectangle2D.Double> ADD_SKILL_BOX = new HashMap<>(6);
    protected HashMap<Byte, Rectangle2D.Double> ADD_MAX_SKILL_BOX = new HashMap<>(6);

    protected byte draggingSkillCode = -1;

    protected byte drawInfoSkillCode = -1;
    protected Rectangle2D.Double drawInfoBox;

    // Actual skills stored
    protected final SaveData saveData;
    protected final HashMap<Byte, PlayerSkillData> skillList;

    public WindowSkill(Screen parent) {
        super(parent);
        this.saveData = Core.getLogicModule().getSelectedChar();
        this.skillList = this.saveData.getSkills();
    }

    public byte getDraggingSkillCode() {
        return this.draggingSkillCode;
    }

    public Rectangle2D.Double getInfoBox() {
        return this.drawInfoBox;
    }

    public byte getInfoSkillCode() {
        return this.drawInfoSkillCode;
    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseDragged(final MouseEvent e) {
        super.mouseDragged(e);
        if (SwingUtilities.isLeftMouseButton(e) && this.draggingSkillCode == -1) {
            for (Map.Entry<Byte, Rectangle2D.Double> entry : SKILL_SLOTS.entrySet()) {
                if (entry.getValue().contains(this.mousePos) && entry.getValue() != null) {
                    if (this.saveData.getTotalStats()[Globals.STAT_LEVEL] >= this.skillList.get(entry.getKey()).getSkillData().getReqLevel()) {
                        this.draggingSkillCode = entry.getKey();
                    }
                    return;
                }
            }
        }
    }

    @Override
    public void mouseReleased(final MouseEvent e) {
        super.mouseReleased(e);
        this.draggingSkillCode = -1;

        super.mouseReleased(e);
        if (SwingUtilities.isLeftMouseButton(e)) {
            for (Map.Entry<Byte, Rectangle2D.Double> entry : ADD_SKILL_BOX.entrySet()) {
                if (entry.getValue().contains(this.mousePos)) {
                    if (this.saveData.getTotalStats()[Globals.STAT_LEVEL] >= this.skillList.get(entry.getKey()).getSkillData().getReqLevel()) {
                        this.saveData.addSkill(entry.getKey(), false);
                    }
                    return;

                }
            }

            for (Map.Entry<Byte, Rectangle2D.Double> entry : ADD_MAX_SKILL_BOX.entrySet()) {
                if (entry.getValue().contains(this.mousePos)) {
                    if (this.saveData.getTotalStats()[Globals.STAT_LEVEL] >= this.skillList.get(entry.getKey()).getSkillData().getReqLevel()) {
                        this.saveData.addSkill(entry.getKey(), true);
                    }
                    return;
                }
            }
        }
    }

    @Override
    public void mouseMoved(final MouseEvent e) {
        super.mouseMoved(e);
        if (this.draggingSkillCode == -1 && ((ScreenSkills) this.parentScreen).getDragHotKey() == -1) {
            for (Map.Entry<Byte, Rectangle2D.Double> entry : SKILL_SLOTS.entrySet()) {
                if (entry.getValue().contains(this.mousePos) && entry.getValue() != null) {
                    this.drawInfoSkillCode = entry.getKey();
                    return;
                }
            }
        }
        this.drawInfoSkillCode = -1;
    }

    @Override
    public void update() {

    }

    public void drawSkillInfo(final Graphics2D g) {
        if (this.drawInfoSkillCode != -1) {
            if (this.saveData.getTotalStats()[Globals.STAT_LEVEL] >= this.skillList.get(this.drawInfoSkillCode).getSkillData().getReqLevel()) {
                drawSkillInfo(g, this.skillList.get(this.drawInfoSkillCode));
            }
        }
    }

    public void drawSkillInfo(final Graphics2D g, final PlayerSkillData skill) {
        skill.drawInfo(g, (int) this.mousePos.x, (int) this.mousePos.y);
    }

}
