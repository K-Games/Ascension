package blockfighter.client.screen.window.skill;

import blockfighter.client.screen.Screen;
import static blockfighter.client.screen.Screen.drawStringOutline;
import blockfighter.shared.Globals;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;

public class WindowSkillAll extends WindowSkill {

    private static final String UNLOCK_SKILL_TEXT1 = "Unlocked at";
    private static final String UNLOCK_SKILL_TEXT2 = "level ";
    private static final String UNLOCK_SKILL_TEXT3 = "Level";
    private static final String MAX_BUTTON_TEXT = "Max";
    private static final String ADD_POINT_BUTTON_TEXT = "+";
    private static final String SKILL_PASSIVE_TEXT = "Passive";
    private static final String SKILL_SHIELD_TEXT = "Shield";
    private static final String SKILL_UTILITY_TEXT = "Utility";
    private static final String SKILL_BOW_TEXT = "Bow";
    private static final String SKILL_SWORD_TEXT = "Sword";

    private static final int SWORD_BOX_X = 260, SWORD_BOX_Y = 55;
    private static final int BOW_BOX_X = 505, BOW_BOX_Y = 55;
    private static final int UTIL_BOX_X = 770, UTIL_BOX_Y = 75;
    private static final int SHIELD_BOX_X = 770, SHIELD_BOX_Y = 270;
    private static final int PASSIVE_BOX_X = 1020, PASSIVE_BOX_Y = 55;

    public WindowSkillAll(Screen parent) {
        super(parent);

        SKILL_SLOTS = new HashMap<>(Globals.NUM_SKILLS);
        ADD_SKILL_BOX = new HashMap<>(Globals.NUM_SKILLS);
        ADD_MAX_SKILL_BOX = new HashMap<>(Globals.NUM_SKILLS);

        SKILL_SLOTS.put(Globals.SWORD_GASH, new Rectangle2D.Double(SWORD_BOX_X, SWORD_BOX_Y, 60, 60));
        SKILL_SLOTS.put(Globals.SWORD_SLASH, new Rectangle2D.Double(SWORD_BOX_X, SWORD_BOX_Y + 90, 60, 60));
        SKILL_SLOTS.put(Globals.SWORD_VORPAL, new Rectangle2D.Double(SWORD_BOX_X, SWORD_BOX_Y + 180, 60, 60));
        SKILL_SLOTS.put(Globals.SWORD_TAUNT, new Rectangle2D.Double(SWORD_BOX_X, SWORD_BOX_Y + 270, 60, 60));
        SKILL_SLOTS.put(Globals.SWORD_PHANTOM, new Rectangle2D.Double(SWORD_BOX_X, SWORD_BOX_Y + 360, 60, 60));
        SKILL_SLOTS.put(Globals.SWORD_CINDER, new Rectangle2D.Double(SWORD_BOX_X, SWORD_BOX_Y + 450, 60, 60));

        SKILL_SLOTS.put(Globals.BOW_ARC, new Rectangle2D.Double(BOW_BOX_X, BOW_BOX_Y, 60, 60));
        SKILL_SLOTS.put(Globals.BOW_RAPID, new Rectangle2D.Double(BOW_BOX_X, BOW_BOX_Y + 90, 60, 60));
        SKILL_SLOTS.put(Globals.BOW_VOLLEY, new Rectangle2D.Double(BOW_BOX_X, BOW_BOX_Y + 180, 60, 60));
        SKILL_SLOTS.put(Globals.BOW_STORM, new Rectangle2D.Double(BOW_BOX_X, BOW_BOX_Y + 270, 60, 60));
        SKILL_SLOTS.put(Globals.BOW_POWER, new Rectangle2D.Double(BOW_BOX_X, BOW_BOX_Y + 360, 60, 60));
        SKILL_SLOTS.put(Globals.BOW_FROST, new Rectangle2D.Double(BOW_BOX_X, BOW_BOX_Y + 450, 60, 60));

        SKILL_SLOTS.put(Globals.UTILITY_DASH, new Rectangle2D.Double(UTIL_BOX_X, UTIL_BOX_Y, 60, 60));
        SKILL_SLOTS.put(Globals.UTILITY_ADRENALINE, new Rectangle2D.Double(UTIL_BOX_X, UTIL_BOX_Y + 75, 60, 60));

        SKILL_SLOTS.put(Globals.SHIELD_CHARGE, new Rectangle2D.Double(SHIELD_BOX_X, SHIELD_BOX_Y, 60, 60));
        SKILL_SLOTS.put(Globals.SHIELD_REFLECT, new Rectangle2D.Double(SHIELD_BOX_X, SHIELD_BOX_Y + 75, 60, 60));
        SKILL_SLOTS.put(Globals.SHIELD_ROAR, new Rectangle2D.Double(SHIELD_BOX_X, SHIELD_BOX_Y + 150, 60, 60));
        SKILL_SLOTS.put(Globals.SHIELD_MAGNETIZE, new Rectangle2D.Double(SHIELD_BOX_X, SHIELD_BOX_Y + 225, 60, 60));

        //Specialize
        SKILL_SLOTS.put(Globals.PASSIVE_DUALSWORD, new Rectangle2D.Double(PASSIVE_BOX_X, PASSIVE_BOX_Y, 60, 60));
        SKILL_SLOTS.put(Globals.PASSIVE_BOWMASTERY, new Rectangle2D.Double(PASSIVE_BOX_X, PASSIVE_BOX_Y + 85, 60, 60));
        SKILL_SLOTS.put(Globals.PASSIVE_SHIELDMASTERY, new Rectangle2D.Double(PASSIVE_BOX_X, PASSIVE_BOX_Y + 170, 60, 60));

        //Defense
        SKILL_SLOTS.put(Globals.PASSIVE_TOUGH, new Rectangle2D.Double(PASSIVE_BOX_X, PASSIVE_BOX_Y + 255, 60, 60));
        SKILL_SLOTS.put(Globals.PASSIVE_BARRIER, new Rectangle2D.Double(PASSIVE_BOX_X, PASSIVE_BOX_Y + 340, 60, 60));
        SKILL_SLOTS.put(Globals.PASSIVE_RESIST, new Rectangle2D.Double(PASSIVE_BOX_X, PASSIVE_BOX_Y + 425, 60, 60));

        //Offense
        SKILL_SLOTS.put(Globals.PASSIVE_KEENEYE, new Rectangle2D.Double(PASSIVE_BOX_X + 110, PASSIVE_BOX_Y, 60, 60));
        SKILL_SLOTS.put(Globals.PASSIVE_VITALHIT, new Rectangle2D.Double(PASSIVE_BOX_X + 110, PASSIVE_BOX_Y + 85, 60, 60));
        SKILL_SLOTS.put(Globals.PASSIVE_SHADOWATTACK, new Rectangle2D.Double(PASSIVE_BOX_X + 110, PASSIVE_BOX_Y + 170, 60, 60));
        //Hybrid
        SKILL_SLOTS.put(Globals.PASSIVE_WILLPOWER, new Rectangle2D.Double(PASSIVE_BOX_X + 110, PASSIVE_BOX_Y + 255, 60, 60));
        SKILL_SLOTS.put(Globals.PASSIVE_STATIC, new Rectangle2D.Double(PASSIVE_BOX_X + 110, PASSIVE_BOX_Y + 340, 60, 60));
        SKILL_SLOTS.put(Globals.PASSIVE_HARMONY, new Rectangle2D.Double(PASSIVE_BOX_X + 110, PASSIVE_BOX_Y + 425, 60, 60));

        for (byte i = 0; i < 18; i++) {
            Rectangle2D.Double rect = SKILL_SLOTS.get(i);
            ADD_SKILL_BOX.put(i, new Rectangle2D.Double(rect.x + 135, rect.y + 32, 30, 23));
            ADD_MAX_SKILL_BOX.put(i, new Rectangle2D.Double(ADD_SKILL_BOX.get(i).x + ADD_SKILL_BOX.get(i).width + 3, rect.y + 32, 30, 23));
        }

        for (byte i = 18; i < SKILL_SLOTS.size(); i++) {
            Rectangle2D.Double rect = SKILL_SLOTS.get(i);
            ADD_SKILL_BOX.put(i, new Rectangle2D.Double(rect.x + 60, rect.y + 37, 30, 23));
            ADD_MAX_SKILL_BOX.put(i, new Rectangle2D.Double(ADD_SKILL_BOX.get(i).x, ADD_SKILL_BOX.get(i).y - 28, 30, 23));
        }
    }

    @Override
    public void draw(Graphics2D g) {
        drawSlots(g);
    }

    private void drawSlots(final Graphics2D g) {
        BufferedImage button = Globals.MENU_BUTTON[Globals.BUTTON_SLOT];

        g.setColor(Screen.BOX_BG_COLOR);
        g.fillRoundRect(SWORD_BOX_X - 10, SWORD_BOX_Y - 25, 210, 545, 15, 15);
        g.fillRoundRect(BOW_BOX_X - 10, BOW_BOX_Y - 25, 210, 545, 15, 15);
        g.fillRoundRect(UTIL_BOX_X - 10, UTIL_BOX_Y - 25, 210, 170, 15, 15);
        g.fillRoundRect(SHIELD_BOX_X - 10, SHIELD_BOX_Y - 25, 210, 320, 15, 15);
        g.fillRoundRect(PASSIVE_BOX_X - 10, PASSIVE_BOX_Y - 25, 220, 545, 15, 15);

        g.setFont(Globals.ARIAL_18PT);
        drawStringOutline(g, SKILL_SWORD_TEXT, SWORD_BOX_X + 65, SWORD_BOX_Y - 5, 1);
        g.setColor(Color.WHITE);
        g.drawString(SKILL_SWORD_TEXT, SWORD_BOX_X + 65, SWORD_BOX_Y - 5);

        drawStringOutline(g, SKILL_BOW_TEXT, BOW_BOX_X + 75, BOW_BOX_Y - 5, 1);
        g.setColor(Color.WHITE);
        g.drawString(SKILL_BOW_TEXT, BOW_BOX_X + 75, BOW_BOX_Y - 5);

        drawStringOutline(g, SKILL_UTILITY_TEXT, UTIL_BOX_X + 75, UTIL_BOX_Y - 5, 1);
        g.setColor(Color.WHITE);
        g.drawString(SKILL_UTILITY_TEXT, UTIL_BOX_X + 75, UTIL_BOX_Y - 5);

        drawStringOutline(g, SKILL_SHIELD_TEXT, SHIELD_BOX_X + 65, SHIELD_BOX_Y - 5, 1);
        g.setColor(Color.WHITE);
        g.drawString(SKILL_SHIELD_TEXT, SHIELD_BOX_X + 65, SHIELD_BOX_Y - 5);

        drawStringOutline(g, SKILL_PASSIVE_TEXT, PASSIVE_BOX_X + 60, PASSIVE_BOX_Y - 5, 1);
        g.setColor(Color.WHITE);
        g.drawString(SKILL_PASSIVE_TEXT, PASSIVE_BOX_X + 60, PASSIVE_BOX_Y - 5);

        for (byte i = 0; i < 18; i++) {
            g.drawImage(button, (int) SKILL_SLOTS.get(i).x, (int) SKILL_SLOTS.get(i).y, null);
            if (this.saveData.getTotalStats()[Globals.STAT_LEVEL] >= this.skillList.get(i).getReqLevel()) {
                this.skillList.get(i).draw(g, (int) SKILL_SLOTS.get(i).x, (int) SKILL_SLOTS.get(i).y);
                g.setFont(Globals.ARIAL_15PT);
                drawStringOutline(g, this.skillList.get(i).getSkillName(), (int) SKILL_SLOTS.get(i).x + 70, (int) SKILL_SLOTS.get(i).y + 20, 1);
                drawStringOutline(g, Globals.getStatName(Globals.STAT_LEVEL) + Globals.COLON_SPACE_TEXT + this.skillList.get(i).getLevel(), (int) SKILL_SLOTS.get(i).x + 70, (int) SKILL_SLOTS.get(i).y + 50,
                        1);
                g.setColor(Color.WHITE);
                g.drawString(this.skillList.get(i).getSkillName(), (int) SKILL_SLOTS.get(i).x + 70, (int) SKILL_SLOTS.get(i).y + 20);
                g.drawString(Globals.getStatName(Globals.STAT_LEVEL) + Globals.COLON_SPACE_TEXT + this.skillList.get(i).getLevel(), (int) SKILL_SLOTS.get(i).x + 70, (int) SKILL_SLOTS.get(i).y + 50);
                drawSkillAddButton(g, i);
            } else {
                this.skillList.get(i).drawDisabled(g, (int) SKILL_SLOTS.get(i).x, (int) SKILL_SLOTS.get(i).y);
                g.setFont(Globals.ARIAL_15PT);
                drawStringOutline(g, UNLOCK_SKILL_TEXT1, (int) SKILL_SLOTS.get(i).x + 70, (int) SKILL_SLOTS.get(i).y + 25, 1);
                drawStringOutline(g, UNLOCK_SKILL_TEXT2 + this.skillList.get(i).getReqLevel(), (int) SKILL_SLOTS.get(i).x + 70, (int) SKILL_SLOTS.get(i).y + 45, 1);
                g.setColor(Color.WHITE);
                g.drawString(UNLOCK_SKILL_TEXT1, (int) SKILL_SLOTS.get(i).x + 70, (int) SKILL_SLOTS.get(i).y + 25);
                g.drawString(UNLOCK_SKILL_TEXT2 + this.skillList.get(i).getReqLevel(), (int) SKILL_SLOTS.get(i).x + 70, (int) SKILL_SLOTS.get(i).y + 45);
            }
        }

        for (byte i = 18; i < SKILL_SLOTS.size(); i++) {
            g.drawImage(button, (int) SKILL_SLOTS.get(i).x, (int) SKILL_SLOTS.get(i).y, null);
            if (this.saveData.getTotalStats()[Globals.STAT_LEVEL] >= this.skillList.get(i).getReqLevel()) {
                this.skillList.get(i).draw(g, (int) SKILL_SLOTS.get(i).x, (int) SKILL_SLOTS.get(i).y);
                g.setFont(Globals.ARIAL_15PT);
                drawStringOutline(g, Globals.getStatName(Globals.STAT_LEVEL) + Globals.COLON_SPACE_TEXT + this.skillList.get(i).getLevel(), (int) SKILL_SLOTS.get(i).x, (int) SKILL_SLOTS.get(i).y + 80, 1);
                g.setColor(Color.WHITE);
                g.drawString(Globals.getStatName(Globals.STAT_LEVEL) + Globals.COLON_SPACE_TEXT + this.skillList.get(i).getLevel(), (int) SKILL_SLOTS.get(i).x, (int) SKILL_SLOTS.get(i).y + 80);
                drawSkillAddButton(g, i);
            } else {
                this.skillList.get(i).drawDisabled(g, (int) SKILL_SLOTS.get(i).x, (int) SKILL_SLOTS.get(i).y);
                g.setFont(Globals.ARIAL_15PT);
                int line1X = (int) (SKILL_SLOTS.get(i).x + SKILL_SLOTS.get(i).width / 2 - g.getFontMetrics().stringWidth(UNLOCK_SKILL_TEXT3) / 2);
                int line2X = (int) (SKILL_SLOTS.get(i).x + SKILL_SLOTS.get(i).width / 2 - g.getFontMetrics().stringWidth(Integer.toString(this.skillList.get(i).getReqLevel())) / 2);
                drawStringOutline(g, UNLOCK_SKILL_TEXT3, line1X, (int) SKILL_SLOTS.get(i).y + 25, 1);
                drawStringOutline(g, Integer.toString(this.skillList.get(i).getReqLevel()), line2X, (int) SKILL_SLOTS.get(i).y + 45, 1);
                g.setColor(Color.WHITE);
                g.drawString(UNLOCK_SKILL_TEXT3, line1X, (int) SKILL_SLOTS.get(i).y + 25);
                g.drawString(Integer.toString(this.skillList.get(i).getReqLevel()), line2X, (int) SKILL_SLOTS.get(i).y + 45);
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

    @Override
    public void drawSkillInfo(final Graphics2D g) {
        if (this.drawInfoSkillCode != -1) {
            if (this.saveData.getTotalStats()[Globals.STAT_LEVEL] >= this.skillList.get(this.drawInfoSkillCode).getReqLevel()) {
                drawSkillInfo(g, this.skillList.get(this.drawInfoSkillCode));
            }
        }
    }

}
