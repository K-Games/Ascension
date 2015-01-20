package blockfighter.client.screen;

import blockfighter.client.Globals;
import blockfighter.client.LogicModule;
import blockfighter.client.SaveData;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

/**
 *
 * @author Ken
 */
public class ScreenStats extends ScreenMenu {

    private SaveData c;
    private double[] stats, bs;
    Rectangle2D.Double[] addBox = new Rectangle2D.Double[6];

    public ScreenStats(LogicModule l) {
        super(l);
        addBox[0] = new Rectangle2D.Double(418, 148, 30, 23);
        addBox[1] = new Rectangle2D.Double(418, 173, 30, 23);
        addBox[2] = new Rectangle2D.Double(418, 198, 30, 23);
        addBox[3] = new Rectangle2D.Double(453, 148, 30, 23);
        addBox[4] = new Rectangle2D.Double(453, 173, 30, 23);
        addBox[5] = new Rectangle2D.Double(453, 198, 30, 23);

        c = l.getSelectedChar();
        stats = c.getStats();
        bs = c.getBaseStats();
    }

    @Override
    public void draw(Graphics g) {
        BufferedImage bg = Globals.MENU_BG[1];
        g.drawImage(bg, 0, 0, null);

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);

        g.setFont(Globals.ARIAL_30PT);
        drawStringOutline(g, c.getPlayerName(), 255, 76, 2);
        g.setColor(Color.WHITE);
        g.drawString(c.getPlayerName(), 255, 76);

        int mainStat = 165, secStat = 295;;
        BufferedImage button = Globals.MENU_BUTTON[Globals.BUTTON_ADDSTAT];
        if (bs[Globals.STAT_POINTS] >= 1) {
            g.drawImage(button, 418, mainStat - 17, null);
            g.drawImage(button, 418, mainStat + 25 - 17, null);
            g.drawImage(button, 418, mainStat + 50 - 17, null);
        }

        if (bs[Globals.STAT_POINTS] >= 5) {
            g.drawImage(button, 453, mainStat - 17, null);
            g.drawImage(button, 453, mainStat + 25 - 17, null);
            g.drawImage(button, 453, mainStat + 50 - 17, null);
        }

        g.setFont(Globals.ARIAL_15PT);
        if (bs[Globals.STAT_POINTS] >= 1) {
            drawStringOutline(g, "+1", 425, mainStat, 2);
            drawStringOutline(g, "+1", 425, mainStat + 25, 2);
            drawStringOutline(g, "+1", 425, mainStat + 50, 2);
        }
        if (bs[Globals.STAT_POINTS] >= 5) {
            drawStringOutline(g, "+5", 460, mainStat, 2);
            drawStringOutline(g, "+5", 460, mainStat + 25, 2);
            drawStringOutline(g, "+5", 460, mainStat + 50, 2);
        }

        g.setColor(Color.WHITE);
        if (bs[Globals.STAT_POINTS] >= 1) {
            g.drawString("+1", 425, mainStat);
            g.drawString("+1", 425, mainStat + 25);
            g.drawString("+1", 425, mainStat + 50);
        }
        if (bs[Globals.STAT_POINTS] >= 5) {
            g.drawString("+5", 460, mainStat);
            g.drawString("+5", 460, mainStat + 25);
            g.drawString("+5", 460, mainStat + 50);
        }

        g.setFont(Globals.ARIAL_18PT);
        drawStringOutline(g, "Level: " + (int) stats[Globals.STAT_LEVEL], 255, 130, 2);
        drawStringOutline(g, "Power: " + (int) stats[Globals.STAT_POWER], 255, mainStat, 2);
        drawStringOutline(g, "Defense: " + (int) stats[Globals.STAT_DEFENSE], 255, mainStat + 25, 2);
        drawStringOutline(g, "Spirit: " + (int) stats[Globals.STAT_SPIRIT], 255, mainStat + 50, 2);
        drawStringOutline(g, "Points: " + (int) stats[Globals.STAT_POINTS], 255, mainStat + 85, 2);

        drawStringOutline(g, "HP: " + (int) stats[Globals.STAT_MAXHP], 255, secStat, 2);
        drawStringOutline(g, "Damage: " + (int) stats[Globals.STAT_MINDMG] + " - " + (int) stats[Globals.STAT_MAXDMG], 255, secStat + 25, 2);
        drawStringOutline(g, "Armor: " + (int) stats[Globals.STAT_ARMOR], 255, secStat + 50, 2);
        drawStringOutline(g, "Regen: " + df.format(stats[Globals.STAT_REGEN]) + " HP/Sec", 255, secStat + 75, 2);
        drawStringOutline(g, "Critical Hit Chance: " + df.format(stats[Globals.STAT_CRITCHANCE] * 100) + "%", 255, secStat + 100, 2);
        drawStringOutline(g, "Critical Hit Damage: " + df.format(stats[Globals.STAT_CRITDMG] * 100) + "%", 255, secStat + 125, 2);
        drawStringOutline(g, "Effective HP: " + df.format((int) Globals.calcEHP(
                Globals.calcReduction(stats[Globals.STAT_ARMOR]),
                stats[Globals.STAT_MAXHP])), 255, secStat + 180, 2);
        drawStringOutline(g, "Exp: " + df.format((int) (bs[Globals.STAT_EXP])) + "/" + df.format((int) Globals.calcEXP(bs[Globals.STAT_LEVEL]))
                + "(" + df.format((bs[Globals.STAT_EXP] / Globals.calcEXP(bs[Globals.STAT_LEVEL])) * 100) + "%)", 255, secStat + 205, 2);

        g.setColor(Color.WHITE);
        g.drawString("Level: " + (int) stats[Globals.STAT_LEVEL], 255, 130);
        g.drawString("Power: " + (int) stats[Globals.STAT_POWER], 255, mainStat);
        g.drawString("Defense: " + (int) stats[Globals.STAT_DEFENSE], 255, mainStat + 25);
        g.drawString("Spirit: " + (int) stats[Globals.STAT_SPIRIT], 255, mainStat + 50);
        g.drawString("Points: " + (int) stats[Globals.STAT_POINTS], 255, mainStat + 85);

        g.drawString("HP: " + (int) stats[Globals.STAT_MAXHP], 255, secStat);
        g.drawString("Damage: " + (int) stats[Globals.STAT_MINDMG] + " - " + (int) stats[Globals.STAT_MAXDMG], 255, secStat + 25);
        g.drawString("Armor: " + (int) stats[Globals.STAT_ARMOR], 255, secStat + 50);
        g.drawString("Regen: " + df.format(stats[Globals.STAT_REGEN]) + " HP/Sec", 255, secStat + 75);
        g.drawString("Critical Hit Chance: " + df.format(stats[Globals.STAT_CRITCHANCE] * 100) + "%", 255, secStat + 100);
        g.drawString("Critical Hit Damage: " + df.format(stats[Globals.STAT_CRITDMG] * 100) + "%", 255, secStat + 125);

        g.drawString("Effective HP: " + df.format((int) Globals.calcEHP(
                Globals.calcReduction(stats[Globals.STAT_ARMOR]),
                stats[Globals.STAT_MAXHP])), 255, secStat + 180);

        g.drawString("Exp: " + df.format((int) (bs[Globals.STAT_EXP])) + "/" + df.format((int) Globals.calcEXP(bs[Globals.STAT_LEVEL]))
                + "(" + df.format((bs[Globals.STAT_EXP] / Globals.calcEXP(bs[Globals.STAT_LEVEL])) * 100) + "%)", 255, secStat + 205);

        g.setColor(Color.BLACK);
        g.fillRect(255, secStat + 215, 450, 40);
        g.setColor(Color.WHITE);
        g.fillRect(256, secStat + 216, 448, 38);
        g.setColor(Color.BLACK);
        g.fillRect(257, secStat + 217, 446, 36);
        g.setColor(new Color(255, 175, 0));
        g.fillRect(258, secStat + 218, (int) (bs[Globals.STAT_EXP] / Globals.calcEXP(bs[Globals.STAT_LEVEL]) * 444), 34);
        
        drawMenuButton(g);
        super.draw(g);
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
        super.mouseReleased(e);
        if (bs[Globals.STAT_POINTS] >= 1) {
            for (int i = 0; i < 3; i++) {
                if (addBox[i].contains(e.getPoint())) {
                    switch (i) {
                        case 0:
                            c.addStat(Globals.STAT_POWER, 1);
                            break;
                        case 1:
                            c.addStat(Globals.STAT_DEFENSE, 1);
                            break;
                        case 2:
                            c.addStat(Globals.STAT_SPIRIT, 1);
                            break;
                    }
                }
            }
        }
        if (bs[Globals.STAT_POINTS] >= 5) {
            for (int i = 3; i < 6; i++) {
                if (addBox[i].contains(e.getPoint())) {
                    switch (i) {
                        case 3:
                            c.addStat(Globals.STAT_POWER, 5);
                            break;
                        case 4:
                            c.addStat(Globals.STAT_DEFENSE, 5);
                            break;
                        case 5:
                            c.addStat(Globals.STAT_SPIRIT, 5);
                            break;
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

    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }

}
