package blockfighter.client.screen;

import blockfighter.client.Globals;
import blockfighter.client.SaveData;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public class ScreenStats extends ScreenMenu {

    private final SaveData c;
    private static final Rectangle2D.Double[] ADD_STAT_BOX = new Rectangle2D.Double[9];
    private static final Rectangle2D.Double RESET_BOX;
    private static final int MAINSTAT_BOX_X = 255, MAINSTAT_BOX_Y = 130;
    private static final int SECSTAT_BOX_X = 255, SECSTAT_BOX_Y = 430;
    private static final int EXPBAR_BOX_X = MAINSTAT_BOX_X, EXPBAR_BOX_Y = MAINSTAT_BOX_Y + 210;

    static {
        for (int i = 0; i < ADD_STAT_BOX.length; i++) {
            ADD_STAT_BOX[i] = new Rectangle2D.Double(MAINSTAT_BOX_X + 235 + 35 * (i / 3), MAINSTAT_BOX_Y + 15 + 25 * (i % 3), 30, 23);
        }
        RESET_BOX = new Rectangle2D.Double(MAINSTAT_BOX_X, MAINSTAT_BOX_Y + 130, 180, 40);

    }

    public ScreenStats() {
        this.c = logic.getSelectedChar();
    }

    private void drawAddStatButton(final Graphics2D g) {
        double[] baseStats = this.c.getBaseStats();
        g.setFont(Globals.ARIAL_15PT);
        BufferedImage button = Globals.MENU_BUTTON[Globals.BUTTON_ADDSTAT];
        if (baseStats[Globals.STAT_POINTS] >= 1) {
            for (byte i = 0; i < 3; i++) {
                g.drawImage(button, (int) ADD_STAT_BOX[i].x, (int) ADD_STAT_BOX[i].y, null);
                drawStringOutline(g, "+1", (int) ADD_STAT_BOX[i].x + 7, (int) ADD_STAT_BOX[i].y + 17, 1);
                g.setColor(Color.WHITE);
                g.drawString("+1", (int) ADD_STAT_BOX[i].x + 7, (int) ADD_STAT_BOX[i].y + 17);
            }
        }

        if (baseStats[Globals.STAT_POINTS] >= 5) {
            for (byte i = 3; i < 6; i++) {
                g.drawImage(button, (int) ADD_STAT_BOX[i].x, (int) ADD_STAT_BOX[i].y, null);
                drawStringOutline(g, "+5", (int) ADD_STAT_BOX[i].x + 7, (int) ADD_STAT_BOX[i].y + 17, 1);
                g.setColor(Color.WHITE);
                g.drawString("+5", (int) ADD_STAT_BOX[i].x + 7, (int) ADD_STAT_BOX[i].y + 17);
            }
        }

        if (baseStats[Globals.STAT_POINTS] >= 50) {
            for (byte i = 6; i < 9; i++) {
                g.drawImage(button, (int) ADD_STAT_BOX[i].x, (int) ADD_STAT_BOX[i].y, null);
                drawStringOutline(g, "+50", (int) ADD_STAT_BOX[i].x + 3, (int) ADD_STAT_BOX[i].y + 17, 1);
                g.setColor(Color.WHITE);
                g.drawString("+50", (int) ADD_STAT_BOX[i].x + 3, (int) ADD_STAT_BOX[i].y + 17);
            }
        }
    }

    private void drawMainStats(final Graphics2D g) {
        g.setColor(SKILL_BOX_BG_COLOR);
        g.fillRoundRect(MAINSTAT_BOX_X - 10, MAINSTAT_BOX_Y - 25, 355, 205, 15, 15);

        double[] baseStats = this.c.getBaseStats();
        double[] totalStats = this.c.getTotalStats();
        double[] bonusStats = this.c.getBonusStats();
        String[] statString = {
            "Level: " + (int) totalStats[Globals.STAT_LEVEL],
            "Power: " + (int) baseStats[Globals.STAT_POWER] + " + " + (int) bonusStats[Globals.STAT_POWER] + " (" + (int) totalStats[Globals.STAT_POWER] + ")",
            "Defense: " + (int) baseStats[Globals.STAT_DEFENSE] + " + " + (int) bonusStats[Globals.STAT_DEFENSE] + " (" + (int) totalStats[Globals.STAT_DEFENSE] + ")",
            "Spirit: " + (int) baseStats[Globals.STAT_SPIRIT] + " + " + (int) bonusStats[Globals.STAT_SPIRIT] + " (" + (int) totalStats[Globals.STAT_SPIRIT] + ")",
            "Points: " + (int) baseStats[Globals.STAT_POINTS]};

        for (byte i = 1; i < 4; i++) {
            g.setFont(Globals.ARIAL_18PT);
            drawStringOutline(g, statString[i], MAINSTAT_BOX_X, MAINSTAT_BOX_Y + 5 + i * 25, 1);
            g.setColor(Color.WHITE);
            g.drawString(statString[i], MAINSTAT_BOX_X, MAINSTAT_BOX_Y + 5 + i * 25);
        }

        g.setFont(Globals.ARIAL_18PT);
        drawStringOutline(g, statString[0], MAINSTAT_BOX_X, MAINSTAT_BOX_Y, 1);
        g.setColor(Color.WHITE);
        g.drawString(statString[0], MAINSTAT_BOX_X, MAINSTAT_BOX_Y);

        g.setFont(Globals.ARIAL_18PT);
        drawStringOutline(g, statString[4], MAINSTAT_BOX_X, MAINSTAT_BOX_Y + 110, 1);
        g.setColor(Color.WHITE);
        g.drawString(statString[4], MAINSTAT_BOX_X, MAINSTAT_BOX_Y + 110);

        final BufferedImage button = Globals.MENU_BUTTON[Globals.BUTTON_SMALLRECT];
        g.drawImage(button, (int) RESET_BOX.x, (int) RESET_BOX.y, null);
        g.setFont(Globals.ARIAL_18PT);
        drawStringOutline(g, "Reset Stats", (int) (RESET_BOX.x + 45), (int) (RESET_BOX.y + 25), 1);
        g.setColor(Color.WHITE);
        g.drawString("Reset Stats", (int) (RESET_BOX.x + 45), (int) (RESET_BOX.y + 25));
    }

    private void drawSecondaryStats(final Graphics2D g) {
        g.setColor(SKILL_BOX_BG_COLOR);
        g.fillRoundRect(SECSTAT_BOX_X - 10, SECSTAT_BOX_Y - 25, 420, 220, 15, 15);
        double[] baseStats = this.c.getBaseStats();
        double[] totalStats = this.c.getTotalStats();
        double[] bonusStats = this.c.getBonusStats();
        String[] statString = {
            "Secondary Stats",
            "HP: " + (int) totalStats[Globals.STAT_MAXHP],
            "Effective HP: " + this.df.format((int) Globals.calcEHP(totalStats[Globals.STAT_DAMAGEREDUCT], totalStats[Globals.STAT_MAXHP])),
            "Damage: " + (int) totalStats[Globals.STAT_MINDMG] + " - " + (int) totalStats[Globals.STAT_MAXDMG],
            "Armor: " + (int) baseStats[Globals.STAT_ARMOR] + " + " + (int) bonusStats[Globals.STAT_ARMOR] + " (" + (int) totalStats[Globals.STAT_ARMOR] + ")",
            "Regen: " + this.df.format(baseStats[Globals.STAT_REGEN]) + " + " + this.df.format(bonusStats[Globals.STAT_REGEN]) + " (" + this.df.format(totalStats[Globals.STAT_REGEN]) + ") HP/Sec",
            "Critical Hit Chance: " + this.df.format(baseStats[Globals.STAT_CRITCHANCE] * 100)
            + " + " + this.df.format(bonusStats[Globals.STAT_CRITCHANCE] * 100) + "% ("
            + this.df.format(totalStats[Globals.STAT_CRITCHANCE] * 100) + "%)",
            "Critical Hit Damage: " + this.df.format((1 + baseStats[Globals.STAT_CRITDMG]) * 100)
            + " + " + this.df.format(bonusStats[Globals.STAT_CRITDMG] * 100) + "% ("
            + this.df.format((1 + totalStats[Globals.STAT_CRITDMG]) * 100) + "%)"
        };

        g.setFont(Globals.ARIAL_18PT);
        drawStringOutline(g, statString[0], SECSTAT_BOX_X, SECSTAT_BOX_Y, 1);
        g.setColor(Color.WHITE);
        g.drawString(statString[0], SECSTAT_BOX_X, SECSTAT_BOX_Y);

        for (byte i = 1; i < statString.length; i++) {
            g.setFont(Globals.ARIAL_18PT);
            drawStringOutline(g, statString[i], SECSTAT_BOX_X, SECSTAT_BOX_Y + 5 + i * 25, 1);
            g.setColor(Color.WHITE);
            g.drawString(statString[i], SECSTAT_BOX_X, SECSTAT_BOX_Y + 5 + i * 25);
        }
    }

    private void drawEXPBar(final Graphics2D g) {
        g.setColor(SKILL_BOX_BG_COLOR);
        g.fillRoundRect(EXPBAR_BOX_X - 10, EXPBAR_BOX_Y - 20, 470, 75, 15, 15);

        double[] baseStats = this.c.getBaseStats();
        String exp = "Exp: " + this.df.format((baseStats[Globals.STAT_EXP])) + "/"
                + this.df.format(baseStats[Globals.STAT_MAXEXP])
                + "(" + this.df.format((baseStats[Globals.STAT_EXP] / baseStats[Globals.STAT_MAXEXP]) * 100) + "%)";

        g.setFont(Globals.ARIAL_18PT);
        drawStringOutline(g, exp, EXPBAR_BOX_X, EXPBAR_BOX_Y, 1);
        g.setColor(Color.WHITE);
        g.drawString(exp, EXPBAR_BOX_X, EXPBAR_BOX_Y);

        g.setColor(Color.BLACK);
        g.fillRect(EXPBAR_BOX_X, EXPBAR_BOX_Y + 5, 450, 40);
        g.setColor(Color.WHITE);
        g.fillRect(EXPBAR_BOX_X + 1, EXPBAR_BOX_Y + 6, 448, 38);
        g.setColor(Color.BLACK);
        g.fillRect(EXPBAR_BOX_X + 2, EXPBAR_BOX_Y + 7, 446, 36);
        g.setColor(new Color(255, 175, 0));
        g.fillRect(EXPBAR_BOX_X + 3, EXPBAR_BOX_Y + 8, (int) (baseStats[Globals.STAT_EXP] / baseStats[Globals.STAT_MAXEXP] * 444), 34);

    }

    @Override
    public void draw(final Graphics2D g) {
        final BufferedImage bg = Globals.MENU_BG[1];
        g.drawImage(bg, 0, 0, null);

        g.setFont(Globals.ARIAL_30PT);
        drawStringOutline(g, this.c.getPlayerName(), 255, 76, 2);
        g.setColor(Color.WHITE);
        g.drawString(this.c.getPlayerName(), 255, 76);

        drawMainStats(g);
        drawAddStatButton(g);
        drawSecondaryStats(g);
        drawEXPBar(g);

        drawMenuButton(g);

        super.draw(g);
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
        double[] baseStats = this.c.getBaseStats();
        super.mouseReleased(e);
        if (baseStats[Globals.STAT_POINTS] >= 1) {
            for (int i = 0; i < 3; i++) {
                if (ADD_STAT_BOX[i].contains(scaled)) {
                    switch (i) {
                        case 0:
                            this.c.addStat(Globals.STAT_POWER, 1);
                            break;
                        case 1:
                            this.c.addStat(Globals.STAT_DEFENSE, 1);
                            break;
                        case 2:
                            this.c.addStat(Globals.STAT_SPIRIT, 1);
                            break;
                    }
                }
            }
        }
        if (baseStats[Globals.STAT_POINTS] >= 5) {
            for (int i = 3; i < 6; i++) {
                if (ADD_STAT_BOX[i].contains(scaled)) {
                    switch (i) {
                        case 3:
                            this.c.addStat(Globals.STAT_POWER, 5);
                            break;
                        case 4:
                            this.c.addStat(Globals.STAT_DEFENSE, 5);
                            break;
                        case 5:
                            this.c.addStat(Globals.STAT_SPIRIT, 5);
                            break;
                    }
                }
            }
        }
        if (baseStats[Globals.STAT_POINTS] >= 50) {
            for (int i = 6; i < 9; i++) {
                if (ADD_STAT_BOX[i].contains(scaled)) {
                    switch (i) {
                        case 6:
                            this.c.addStat(Globals.STAT_POWER, 50);
                            break;
                        case 7:
                            this.c.addStat(Globals.STAT_DEFENSE, 50);
                            break;
                        case 8:
                            this.c.addStat(Globals.STAT_SPIRIT, 50);
                            break;
                    }
                }
            }
        }
        if (RESET_BOX.contains(scaled)) {
            this.c.resetStat();
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

    }

    @Override
    public void mouseMoved(final MouseEvent e) {

    }

    @Override
    public void unload() {
    }

}
