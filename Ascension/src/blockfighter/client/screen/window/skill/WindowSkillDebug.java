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

public class WindowSkillDebug extends WindowSkill {

    private static final String MAX_BUTTON_TEXT = "Max";
    private static final String ADD_POINT_BUTTON_TEXT = "+";

    public WindowSkillDebug(Screen parent) {
        super(parent);

        SKILL_SLOTS = new HashMap<>();
        ADD_SKILL_BOX = new HashMap<>();
        ADD_MAX_SKILL_BOX = new HashMap<>();
        double x = 230, y = 30;
        for (Globals.SkillClassMap skill : Globals.SkillClassMap.values()) {
            SKILL_SLOTS.put(skill.getByteCode(), new Rectangle2D.Double(x, y, 60, 60));
            y += 70;
            if (y >= 550) {
                x += 100;
                y = 30;
            }
        }

        for (Entry<Byte, Rectangle2D.Double> entry : SKILL_SLOTS.entrySet()) {
            ADD_SKILL_BOX.put(entry.getKey(), new Rectangle2D.Double(entry.getValue().x + 60, entry.getValue().y + 37, 30, 23));
            ADD_MAX_SKILL_BOX.put(entry.getKey(), new Rectangle2D.Double(ADD_SKILL_BOX.get(entry.getKey()).x, ADD_SKILL_BOX.get(entry.getKey()).y - 28, 30, 23));
        }
    }

    @Override
    public void draw(Graphics2D g) {
        drawSlots(g);
    }

    private void drawSlots(final Graphics2D g) {
        BufferedImage button = Globals.MENU_BUTTON[Globals.BUTTON_SLOT];

        g.setColor(Screen.BOX_BG_COLOR);
        for (Entry<Byte, Rectangle2D.Double> entry : SKILL_SLOTS.entrySet()) {
            g.drawImage(button, (int) entry.getValue().x, (int) entry.getValue().y, null);
            boolean disabled = this.saveData.getTotalStats()[Globals.STAT_LEVEL] < this.skillList.get(entry.getKey()).getSkillData().getReqLevel();
            this.skillList.get(entry.getKey()).draw(g, (int) entry.getValue().x, (int) entry.getValue().y, disabled);
            if (!disabled) {
                drawSkillAddButton(g, entry.getKey());
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
