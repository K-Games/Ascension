package blockfighter.client.screen;

import blockfighter.client.Globals;
import blockfighter.client.LogicModule;
import blockfighter.client.SaveData;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.swing.BorderFactory;
import javax.swing.JTextField;

/**
 *
 * @author Ken Kwan
 */
public class ScreenSelectChar extends ScreenMenu {

    private SaveData[] charsData = new SaveData[3];

    private boolean createPrompt = false;
    private final JTextField CREATE_NAMEFIELD = new JTextField();
    private String CREATE_ERR = "";

    private final Rectangle[] promptBox = new Rectangle[2];
    private final Rectangle[] selectBox = new Rectangle[3];

    private byte selectNum = -1;

    public ScreenSelectChar(LogicModule l) {
        super(l);
        CREATE_NAMEFIELD.setBounds(440, 300, 400, 50);
        CREATE_NAMEFIELD.setFont(Globals.ARIAL_30PT);
        CREATE_NAMEFIELD.setForeground(Color.WHITE);
        CREATE_NAMEFIELD.setOpaque(false);
        CREATE_NAMEFIELD.setBorder(BorderFactory.createEmptyBorder());

        loadSaveData();
        for (byte i = 0; i < charsData.length; i++) {
            selectBox[i] = new Rectangle(20 + 420 * i, 60, 400, 500);
        }

        promptBox[0] = new Rectangle(401, 400, 214, 112);
        promptBox[1] = new Rectangle(665, 400, 214, 112);

    }

    private void loadSaveData() {
        for (byte i = 0; i < charsData.length; i++) {
            charsData[i] = SaveData.readData(i);
        }
    }

    @Override
    public void draw(Graphics2D g) {
        BufferedImage bg = Globals.MENU_BG[0];
        g.drawImage(bg, 0, 0, null);

        BufferedImage button = Globals.MENU_BUTTON[Globals.BUTTON_SELECTCHAR];
        g.drawImage(button, 20, 60, null);
        g.drawImage(button, 440, 60, null);
        g.drawImage(button, 860, 60, null);

        g.setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);

        for (int j = 0; j < 3; j++) {
            if (charsData[j] == null) {
                g.setFont(Globals.ARIAL_30PT);
                drawStringOutline(g, "Create", 170 + 420 * j, 260, 2);
                drawStringOutline(g, "New", 185 + 420 * j, 310, 2);
                drawStringOutline(g, "Character", 150 + 420 * j, 360, 2);
                g.setColor(Color.WHITE);
                g.drawString("Create", 170 + 420 * j, 260);
                g.drawString("New", 185 + 420 * j, 310);
                g.drawString("Character", 150 + 420 * j, 360);
            } else {
                double[] stats = charsData[j].getBaseStats(), bonus = charsData[j].getBonusStats();
                g.setFont(Globals.ARIAL_30PT);
                drawStringOutline(g, charsData[j].getPlayerName(), 120 + 420 * j, 380, 2);
                g.setColor(Color.WHITE);
                g.drawString(charsData[j].getPlayerName(), 120 + 420 * j, 380);

                g.setFont(Globals.ARIAL_24PT);
                drawStringOutline(g, "Level: " + (int) stats[Globals.STAT_LEVEL], 120 + 420 * j, 415, 2);
                drawStringOutline(g, "Power: " + (int) stats[Globals.STAT_POWER] + " + " + (int) bonus[Globals.STAT_POWER], 120 + 420 * j, 445, 2);
                drawStringOutline(g, "Defense: " + (int) stats[Globals.STAT_DEFENSE] + " + " + (int) bonus[Globals.STAT_DEFENSE], 120 + 420 * j, 475, 2);
                drawStringOutline(g, "Spirit: " + (int) stats[Globals.STAT_SPIRIT] + " + " + (int) bonus[Globals.STAT_SPIRIT], 120 + 420 * j, 505, 2);

                g.setColor(Color.WHITE);
                g.drawString("Level: " + (int) stats[Globals.STAT_LEVEL], 120 + 420 * j, 415);
                g.drawString("Power: " + (int) stats[Globals.STAT_POWER] + " + " + (int) bonus[Globals.STAT_POWER], 120 + 420 * j, 445);
                g.drawString("Defense: " + (int) stats[Globals.STAT_DEFENSE] + " + " + (int) bonus[Globals.STAT_DEFENSE], 120 + 420 * j, 475);
                g.drawString("Spirit: " + (int) stats[Globals.STAT_SPIRIT] + " + " + (int) bonus[Globals.STAT_SPIRIT], 120 + 420 * j, 505);
                g.drawString("ID: " + charsData[j].getUniqueID(), 120 + 420 * j, 535);
            }
        }

        drawStringOutline(g, "Select a Character", 520, 640, 2);
        g.setColor(Color.WHITE);
        g.drawString("Select a Character", 520, 640);

        if (createPrompt) {
            BufferedImage window = Globals.MENU_WINDOW[Globals.WINDOW_CREATECHAR];
            g.drawImage(window, 265, 135, null);
            drawStringOutline(g, "Enter New Character Name", 460, 250, 2);
            g.setColor(Color.WHITE);
            g.drawString("Enter New Character Name", 460, 250);

            button = Globals.MENU_BUTTON[Globals.BUTTON_BIGRECT];
            g.drawImage(button, 401, 400, null);
            drawStringOutline(g, "Create", 460, 465, 2);
            g.setColor(Color.WHITE);
            g.drawString("Create", 460, 465);

            g.drawImage(button, 665, 400, null);
            drawStringOutline(g, "Cancel", 725, 465, 2);
            g.setColor(Color.WHITE);
            g.drawString("Cancel", 725, 465);

            g.setFont(Globals.ARIAL_24PT);
            drawStringOutline(g, CREATE_ERR, 450, 550, 2);
            g.setColor(Color.WHITE);
            g.drawString(CREATE_ERR, 450, 550);
        }
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

    private void mouseReleased_Create(MouseEvent e) {
        panel.requestFocus();
        for (byte i = 0; i < promptBox.length; i++) {
            if (promptBox[i].contains(e.getPoint())) {
                if (i == 0) {
                    CREATE_NAMEFIELD.setText(CREATE_NAMEFIELD.getText().trim());
                    if (CREATE_NAMEFIELD.getText().length() <= 15 && CREATE_NAMEFIELD.getText().length() > 0) {
                        SaveData.saveData(selectNum, new SaveData(CREATE_NAMEFIELD.getText().trim()));
                        loadSaveData();
                    } else {
                        if (CREATE_NAMEFIELD.getText().length() <= 0) {
                            CREATE_ERR = "Name must have at least 1 character!";
                        } else if (CREATE_NAMEFIELD.getText().length() > 15) {
                            CREATE_ERR = "Name must be less than 15 characters!";
                        }
                        break;
                    }
                }
                createPrompt = false;
                selectNum = -1;
                CREATE_ERR = "";
                CREATE_NAMEFIELD.setText("");
                if (panel != null) {
                    panel.remove(CREATE_NAMEFIELD);
                }
                break;
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            if (createPrompt) {
                mouseReleased_Create(e);
                return;
            }
            for (byte i = 0; i < selectBox.length; i++) {
                if (selectBox[i].contains(e.getPoint())) {
                    if (charsData[i] == null) {
                        createPrompt = true;
                        CREATE_NAMEFIELD.setBounds(440, 300, 400, 50);
                        CREATE_NAMEFIELD.setFont(Globals.ARIAL_30PT);
                        CREATE_NAMEFIELD.setForeground(Color.WHITE);
                        CREATE_NAMEFIELD.setOpaque(false);
                        CREATE_NAMEFIELD.setBorder(BorderFactory.createEmptyBorder());
                        if (panel != null) {
                            panel.add(CREATE_NAMEFIELD);
                        }
                        selectNum = i;
                        break;
                    } else {
                        logic.setSelectedChar(charsData[i]);
                        logic.setScreen(new ScreenStats(logic));
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
