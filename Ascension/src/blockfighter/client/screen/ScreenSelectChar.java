package blockfighter.client.screen;

import blockfighter.client.Globals;
import blockfighter.client.Main;
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
import javax.swing.SwingUtilities;

public class ScreenSelectChar extends ScreenMenu {

    private static final SaveData[] CHARACTER_DATA = new SaveData[3];

    private boolean createPrompt = false;
    private static final JTextField CREATE_NAMEFIELD = new JTextField();
    private String CREATE_ERR = "";

    private static final Rectangle[] PROMPT_BOX = new Rectangle[2];
    private static final Rectangle[] SELECT_BOX = new Rectangle[3];

    private byte selectNum = -1;

    static {
        CREATE_NAMEFIELD.addFocusListener(Main.FOCUS_HANDLER);
        CREATE_NAMEFIELD.setBounds(440, 300, 400, 50);
        CREATE_NAMEFIELD.setFont(Globals.ARIAL_30PT);
        CREATE_NAMEFIELD.setForeground(Color.WHITE);
        CREATE_NAMEFIELD.setOpaque(false);
        CREATE_NAMEFIELD.setCaretColor(Color.WHITE);
        CREATE_NAMEFIELD.setBorder(BorderFactory.createEmptyBorder());
        for (byte i = 0; i < CHARACTER_DATA.length; i++) {
            SELECT_BOX[i] = new Rectangle(20 + 420 * i, 60, 400, 500);
        }

        PROMPT_BOX[0] = new Rectangle(401, 400, 214, 112);
        PROMPT_BOX[1] = new Rectangle(665, 400, 214, 112);
    }

    public ScreenSelectChar(final boolean fadeIn) {
        super(fadeIn);
        loadSaveData();
    }

    public ScreenSelectChar() {
        this(false);
    }

    private void loadSaveData() {
        for (byte i = 0; i < CHARACTER_DATA.length; i++) {
            try {
                CHARACTER_DATA[i] = SaveData.readData(i);
            } catch (final Exception e) {
                System.err.println("Corrupted savefile: " + i);
                CHARACTER_DATA[i] = null;
            }
        }
    }

    @Override
    public void draw(final Graphics2D g) {
        final BufferedImage bg = Globals.MENU_BG[0];
        g.drawImage(bg, 0, 0, null);

        BufferedImage button = Globals.MENU_BUTTON[Globals.BUTTON_SELECTCHAR];
        g.drawImage(button, 20, 60, null);
        g.drawImage(button, 440, 60, null);
        g.drawImage(button, 860, 60, null);

        g.setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);

        for (int j = 0; j < 3; j++) {
            if (CHARACTER_DATA[j] == null) {
                g.setFont(Globals.ARIAL_30PT);
                drawStringOutline(g, "Create", 170 + 420 * j, 260, 2);
                drawStringOutline(g, "New", 185 + 420 * j, 310, 2);
                drawStringOutline(g, "Character", 150 + 420 * j, 360, 2);
                g.setColor(Color.WHITE);
                g.drawString("Create", 170 + 420 * j, 260);
                g.drawString("New", 185 + 420 * j, 310);
                g.drawString("Character", 150 + 420 * j, 360);
            } else {
                final double[] stats = CHARACTER_DATA[j].getBaseStats(), bonus = CHARACTER_DATA[j].getBonusStats();
                g.setFont(Globals.ARIAL_30PT);
                drawStringOutline(g, CHARACTER_DATA[j].getPlayerName(), 120 + 420 * j, 380, 2);
                g.setColor(Color.WHITE);
                g.drawString(CHARACTER_DATA[j].getPlayerName(), 120 + 420 * j, 380);

                g.setFont(Globals.ARIAL_24PT);
                drawStringOutline(g, "Level: " + (int) stats[Globals.STAT_LEVEL], 120 + 420 * j, 415, 2);
                drawStringOutline(g, "Power: " + (int) stats[Globals.STAT_POWER] + " + " + (int) bonus[Globals.STAT_POWER], 120 + 420 * j,
                        445, 2);
                drawStringOutline(g, "Defense: " + (int) stats[Globals.STAT_DEFENSE] + " + " + (int) bonus[Globals.STAT_DEFENSE],
                        120 + 420 * j, 475, 2);
                drawStringOutline(g, "Spirit: " + (int) stats[Globals.STAT_SPIRIT] + " + " + (int) bonus[Globals.STAT_SPIRIT],
                        120 + 420 * j, 505, 2);

                g.setColor(Color.WHITE);
                g.drawString("Level: " + (int) stats[Globals.STAT_LEVEL], 120 + 420 * j, 415);
                g.drawString("Power: " + (int) stats[Globals.STAT_POWER] + " + " + (int) bonus[Globals.STAT_POWER], 120 + 420 * j, 445);
                g.drawString("Defense: " + (int) stats[Globals.STAT_DEFENSE] + " + " + (int) bonus[Globals.STAT_DEFENSE], 120 + 420 * j,
                        475);
                g.drawString("Spirit: " + (int) stats[Globals.STAT_SPIRIT] + " + " + (int) bonus[Globals.STAT_SPIRIT], 120 + 420 * j, 505);
                if (Globals.DEBUG_MODE) {
                    g.drawString("ID: " + CHARACTER_DATA[j].getUniqueID(), 120 + 420 * j, 535);
                }
            }
        }

        drawStringOutline(g, "Select a Character", 520, 640, 2);
        g.setColor(Color.WHITE);
        g.drawString("Select a Character", 520, 640);

        if (this.createPrompt) {
            final BufferedImage window = Globals.MENU_WINDOW[Globals.WINDOW_CREATECHAR];
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
            drawStringOutline(g, this.CREATE_ERR, 450, 550, 2);
            g.setColor(Color.WHITE);
            g.drawString(this.CREATE_ERR, 450, 550);
        }
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

    private void mouseReleased_Create(final MouseEvent e) {
        panel.requestFocus();
        for (byte i = 0; i < PROMPT_BOX.length; i++) {
            if (PROMPT_BOX[i].contains(e.getPoint())) {
                if (i == 0) {
                    CREATE_NAMEFIELD.setText(CREATE_NAMEFIELD.getText().trim());
                    if (CREATE_NAMEFIELD.getText().length() <= 15 && CREATE_NAMEFIELD.getText().length() > 0) {
                        final SaveData newChar = new SaveData(CREATE_NAMEFIELD.getText().trim(), this.selectNum);
                        newChar.newCharacter(Globals.TEST_MAX_LEVEL);
                        SaveData.saveData(this.selectNum, newChar);
                        loadSaveData();
                    } else {
                        if (CREATE_NAMEFIELD.getText().length() <= 0) {
                            this.CREATE_ERR = "Name must have at least 1 character!";
                        } else if (CREATE_NAMEFIELD.getText().length() > 15) {
                            this.CREATE_ERR = "Name must be less than 15 characters!";
                        }
                        break;
                    }
                }
                this.createPrompt = false;
                this.selectNum = -1;
                this.CREATE_ERR = "";
                CREATE_NAMEFIELD.setText("");
                if (panel != null) {
                    panel.remove(CREATE_NAMEFIELD);
                    panel.revalidate();
                }
                break;
            }
        }
    }

    @Override
    public void mouseClicked(final MouseEvent e) {

    }

    @Override
    public void mousePressed(final MouseEvent e) {

    }

    @Override
    public void mouseReleased(final MouseEvent e) {
        if (this.fadeIn && !this.finishedFadeIn) {
            return;
        }
        if (SwingUtilities.isLeftMouseButton(e)) {
            if (this.createPrompt) {
                mouseReleased_Create(e);
                return;
            }
            for (byte i = 0; i < SELECT_BOX.length; i++) {
                if (SELECT_BOX[i].contains(e.getPoint())) {
                    if (CHARACTER_DATA[i] == null) {
                        this.createPrompt = true;
                        CREATE_NAMEFIELD.setBounds(440, 300, 400, 50);
                        CREATE_NAMEFIELD.setFont(Globals.ARIAL_30PT);
                        CREATE_NAMEFIELD.setForeground(Color.WHITE);
                        CREATE_NAMEFIELD.setOpaque(false);
                        CREATE_NAMEFIELD.setBorder(BorderFactory.createEmptyBorder());
                        if (panel != null) {
                            panel.add(CREATE_NAMEFIELD);
                            panel.revalidate();
                        }
                        this.selectNum = i;
                        break;
                    } else {
                        logic.setSelectedChar(CHARACTER_DATA[i]);
                        logic.setScreen(new ScreenStats());
                    }
                }
            }
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
        if (panel != null) {
            panel.remove(CREATE_NAMEFIELD);
        }
    }

}
