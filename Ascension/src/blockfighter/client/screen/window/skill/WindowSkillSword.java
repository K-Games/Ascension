package blockfighter.client.screen.window.skill;

import blockfighter.client.screen.Screen;
import static blockfighter.client.screen.Screen.drawStringOutline;
import blockfighter.shared.Globals;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map.Entry;

public class WindowSkillSword extends WindowSkill {

    private static final String MAX_BUTTON_TEXT = "Max";
    private static final String ADD_POINT_BUTTON_TEXT = "+";
    private static final int BASIC_BOX_X = 230, BASIC_BOX_Y = 30;

    public WindowSkillSword(Screen parent) {
        super(parent);

        SKILL_SLOTS = new HashMap<>(6);
        ADD_SKILL_BOX = new HashMap<>(6);
        ADD_MAX_SKILL_BOX = new HashMap<>(6);
        SKILL_SLOTS.put(Globals.SWORD_GASH, new Rectangle2D.Double(400, 50, 60, 60));
        SKILL_SLOTS.put(Globals.SWORD_SLASH, new Rectangle2D.Double(400, 130, 60, 60));
        SKILL_SLOTS.put(Globals.SWORD_VORPAL, new Rectangle2D.Double(400, 210, 60, 60));
        SKILL_SLOTS.put(Globals.SWORD_VORPAL_GHOST, new Rectangle2D.Double(700, 210, 60, 60));
        SKILL_SLOTS.put(Globals.SWORD_TAUNT, new Rectangle2D.Double(400, 290, 60, 60));
        SKILL_SLOTS.put(Globals.SWORD_PHANTOM, new Rectangle2D.Double(400, 370, 60, 60));
        SKILL_SLOTS.put(Globals.SWORD_CINDER, new Rectangle2D.Double(400, 450, 60, 60));

        for (Entry<Byte, Rectangle2D.Double> entry : SKILL_SLOTS.entrySet()) {
            ADD_SKILL_BOX.put(entry.getKey(), new Rectangle2D.Double(entry.getValue().x + 135, entry.getValue().y + 32, 30, 23));
            ADD_MAX_SKILL_BOX.put(entry.getKey(), new Rectangle2D.Double(ADD_SKILL_BOX.get(entry.getKey()).x + ADD_SKILL_BOX.get(entry.getKey()).width + 3, entry.getValue().y + 32, 30, 23));
        }
    }

    @Override
    public void draw(Graphics2D g) {
        drawSlots(g);
    }

    private void drawSlots(final Graphics2D g) {
        BufferedImage button = Globals.MENU_BUTTON[Globals.BUTTON_SLOT];

        g.setColor(Screen.BOX_BG_COLOR);
        g.fillRoundRect(BASIC_BOX_X, BASIC_BOX_Y, 210, 545, 15, 15);
        for (Entry<Byte, Rectangle2D.Double> entry : SKILL_SLOTS.entrySet()) {
            g.drawImage(button, (int) entry.getValue().x, (int) entry.getValue().y, null);
            boolean disabled = this.saveData.getTotalStats()[Globals.STAT_LEVEL] < this.skillList.get(entry.getKey()).getReqLevel();
            this.skillList.get(entry.getKey()).draw(g, (int) entry.getValue().x, (int) entry.getValue().y, disabled);
            if (!disabled) {
                this.skillList.get(entry.getKey()).draw(g, (int) entry.getValue().x, (int) entry.getValue().y);
                g.setFont(Globals.ARIAL_15PT);
                drawStringOutline(g, this.skillList.get(entry.getKey()).getSkillName(), (int) entry.getValue().x + 70, (int) entry.getValue().y + 20, 1);
                drawStringOutline(g, Globals.getStatName(Globals.STAT_LEVEL) + Globals.COLON_SPACE_TEXT + this.skillList.get(entry.getKey()).getLevel(), (int) entry.getValue().x + 70, (int) entry.getValue().y + 50,
                        1);
                g.setColor(Color.WHITE);
                g.drawString(this.skillList.get(entry.getKey()).getSkillName(), (int) entry.getValue().x + 70, (int) entry.getValue().y + 20);
                g.drawString(Globals.getStatName(Globals.STAT_LEVEL) + Globals.COLON_SPACE_TEXT + this.skillList.get(entry.getKey()).getLevel(), (int) entry.getValue().x + 70, (int) entry.getValue().y + 50);
                drawSkillAddButton(g, entry.getKey());
            } else {

            }
        }
    }

    private void drawSkillAddButton(final Graphics2D g, final byte skillIndex) {
        if (this.saveData.getBaseStats()[Globals.STAT_SKILLPOINTS] > 0 && !this.skillList.get(skillIndex).isMaxed()) {
            BufferedImage button = Globals.MENU_BUTTON[Globals.BUTTON_ADDSTAT];
            g.drawImage(button, (int) ADD_SKILL_BOX.get(skillIndex).x, (int) ADD_SKILL_BOX.get(skillIndex).y, null);
            g.setFont(Globals.ARIAL_15PT);
            drawStringOutline(g, ADD_POINT_BUTTON_TEXT, (int) ADD_SKILL_BOX.get(skillIndex).x + 11, (int) ADD_SKILL_BOX.get(skillIndex).y + 18, 1);
            g.setColor(Color.WHITE);
            g.drawString(ADD_POINT_BUTTON_TEXT, (int) ADD_SKILL_BOX.get(skillIndex).x + 11, (int) ADD_SKILL_BOX.get(skillIndex).y + 18);

            button = Globals.MENU_BUTTON[Globals.BUTTON_ADDSTAT];
            g.drawImage(button, (int) ADD_MAX_SKILL_BOX.get(skillIndex).x, (int) ADD_MAX_SKILL_BOX.get(skillIndex).y, null);
            g.setFont(Globals.ARIAL_12PT);
            drawStringOutline(g, MAX_BUTTON_TEXT, (int) ADD_MAX_SKILL_BOX.get(skillIndex).x + 4, (int) ADD_MAX_SKILL_BOX.get(skillIndex).y + 16, 1);
            g.setColor(Color.WHITE);
            g.drawString(MAX_BUTTON_TEXT, (int) ADD_MAX_SKILL_BOX.get(skillIndex).x + 4, (int) ADD_MAX_SKILL_BOX.get(skillIndex).y + 16);
        }
    }

}
