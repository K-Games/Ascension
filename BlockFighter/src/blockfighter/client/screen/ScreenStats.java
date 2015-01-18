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
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;

/**
 *
 * @author Ken
 */
public class ScreenStats extends ScreenMenu {

    private SaveData sd;
    private double[] stats, bs;
    DecimalFormat df = new DecimalFormat("0.00");

    public ScreenStats(LogicModule l) {
        super(l);
        sd = l.getSelectedChar();
        stats = sd.getStats();
        bs = sd.getBaseStats();
    }

    @Override
    public void draw(Graphics g) {
        BufferedImage bg = Globals.MENU_BG[1];
        g.drawImage(bg, 0, 0, null);

        super.draw(g);
        drawMenuButton(g);
        
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);

        g.setFont(Globals.ARIAL_30PT);
        drawStringOutline(g, sd.getPlayerName(), 255, 76, 2);
        g.setColor(Color.WHITE);
        g.drawString(sd.getPlayerName(), 255, 76);

        int mainStat = 165, secStat = 295;;
        BufferedImage button = Globals.MENU_BUTTON[Globals.BUTTON_ADDSTAT];
        g.drawImage(button, 418, mainStat - 17, null);
        g.drawImage(button, 418, mainStat + 25 - 17, null);
        g.drawImage(button, 418, mainStat + 50 - 17, null);
        g.drawImage(button, 453, mainStat - 17, null);
        g.drawImage(button, 453, mainStat + 25 - 17, null);
        g.drawImage(button, 453, mainStat + 50 - 17, null);

        g.setFont(Globals.ARIAL_15PT);
        drawStringOutline(g, "+1", 425, mainStat, 2);
        drawStringOutline(g, "+1", 425, mainStat + 25, 2);
        drawStringOutline(g, "+1", 425, mainStat + 50, 2);
        drawStringOutline(g, "+5", 460, mainStat, 2);
        drawStringOutline(g, "+5", 460, mainStat + 25, 2);
        drawStringOutline(g, "+5", 460, mainStat + 50, 2);
        g.setColor(Color.WHITE);
        g.drawString("+1", 425, mainStat);
        g.drawString("+1", 425, mainStat + 25);
        g.drawString("+1", 425, mainStat + 50);
        g.drawString("+5", 460, mainStat);
        g.drawString("+5", 460, mainStat + 25);
        g.drawString("+5", 460, mainStat + 50);

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
