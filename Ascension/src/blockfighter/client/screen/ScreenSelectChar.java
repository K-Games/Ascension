package blockfighter.client.screen;

import blockfighter.client.Core;
import blockfighter.client.savedata.SaveData;
import blockfighter.shared.Globals;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import javax.swing.BorderFactory;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class ScreenSelectChar extends ScreenMenu {

    private static final String LESS_THAN_FIFTEEN_CHAR_ERR_TEXT = "Name must be less than 15 characters!";
    private static final String MIN_ONE_CHARACTER_ERR_TEXT = "Name must have at least 1 character!";
    private static final String CANCEL_BUTTON_TEXT = "Cancel";
    private static final String CREATE_BUTTON_TEXT = "Create";
    private static final String ENTER_NEW_NAME_TEXT = "Enter New Character Name";
    private static final String SELECT_A_CHARACTER_TEXT = "Select a Character";
    private static final String NEW_CHARACTER_TEXT = "New Character";

    private static final SaveData[] CHARACTER_DATA = new SaveData[3];

    private boolean createPrompt = false;
    private static final JTextField CREATE_NAMEFIELD = new JTextField();
    private String CREATE_ERR = "";

    private static final Rectangle[] PROMPT_BOX = new Rectangle[2];
    private static final Rectangle[] SELECT_BOX = new Rectangle[3];

    private byte selectNum = -1;
    private boolean savesLoaded = false;

    static {
        CREATE_NAMEFIELD.addFocusListener(Core.FOCUS_HANDLER);
        if (Globals.WINDOW_SCALE_ENABLED) {
            CREATE_NAMEFIELD.setBounds((int) (440 * Globals.WINDOW_SCALE), (int) (300 * Globals.WINDOW_SCALE), (int) (400 * Globals.WINDOW_SCALE), (int) (50 * Globals.WINDOW_SCALE));
            CREATE_NAMEFIELD.setFont(new Font(Globals.ARIAL_30PT.getFontName(), Globals.ARIAL_30PT.getStyle(), (int) (Globals.ARIAL_30PT.getSize() * Globals.WINDOW_SCALE)));
        } else {
            CREATE_NAMEFIELD.setBounds(440, 300, 400, 50);
            CREATE_NAMEFIELD.setFont(Globals.ARIAL_30PT);
        }
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
    }

    public ScreenSelectChar() {
        this(false);
    }

    private void loadSaveData() {
        Globals.log(ScreenSelectChar.class, "Loading Save Data...", Globals.LOG_TYPE_DATA);
        for (byte i = 0; i < CHARACTER_DATA.length; i++) {
            try {
                CHARACTER_DATA[i] = SaveData.readSaveData(i);
                CHARACTER_DATA[i].validate();
            } catch (final Exception e) {
                System.err.println("Corrupted savefile: " + i);
                CHARACTER_DATA[i] = null;
            }
        }
        Globals.log(ScreenSelectChar.class, "Finished loading Save Data.", Globals.LOG_TYPE_DATA);
        this.savesLoaded = true;
    }

    @Override
    public void update() {
        super.update();
        if (!savesLoaded) {
            loadSaveData();
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

        for (int j = 0; j < 3; j++) {
            if (CHARACTER_DATA[j] == null) {
                g.setFont(Globals.ARIAL_30PT);
                drawStringOutline(g, NEW_CHARACTER_TEXT, 20 + 420 * j + 200 - g.getFontMetrics().stringWidth(NEW_CHARACTER_TEXT) / 2, 310, 2);

                g.setColor(Color.WHITE);
                g.drawString(NEW_CHARACTER_TEXT, 20 + 420 * j + 200 - g.getFontMetrics().stringWidth(NEW_CHARACTER_TEXT) / 2, 310);

            } else {
                final double[] stats = CHARACTER_DATA[j].getBaseStats(), bonus = CHARACTER_DATA[j].getBonusStats();
                g.setFont(Globals.ARIAL_30PT);
                drawStringOutline(g, CHARACTER_DATA[j].getPlayerName(), 120 + 420 * j, 380, 2);
                g.setColor(Color.WHITE);
                g.drawString(CHARACTER_DATA[j].getPlayerName(), 120 + 420 * j, 380);

                g.setFont(Globals.ARIAL_24PT);
                String[] statString = {
                    Globals.getStatName(Globals.STAT_LEVEL) + Globals.COLON_SPACE_TEXT + (int) stats[Globals.STAT_LEVEL],
                    Globals.getStatName(Globals.STAT_POWER) + Globals.COLON_SPACE_TEXT + (int) stats[Globals.STAT_POWER] + " + " + (int) bonus[Globals.STAT_POWER],
                    Globals.getStatName(Globals.STAT_DEFENSE) + Globals.COLON_SPACE_TEXT + (int) stats[Globals.STAT_DEFENSE] + " + " + (int) bonus[Globals.STAT_DEFENSE],
                    Globals.getStatName(Globals.STAT_SPIRIT) + Globals.COLON_SPACE_TEXT + (int) stats[Globals.STAT_SPIRIT] + " + " + (int) bonus[Globals.STAT_SPIRIT]
                };

                for (byte i = 0; i < statString.length; i++) {
                    drawStringOutline(g, statString[i], 120 + 420 * j, 415 + 30 * i, 2);
                    g.setColor(Color.WHITE);
                    g.drawString(statString[i], 120 + 420 * j, 415 + 30 * i);
                }
                if (Globals.DEBUG_MODE) {
                    g.drawString("ID: " + CHARACTER_DATA[j].getUniqueID(), 120 + 420 * j, 535);
                }
            }
        }

        drawStringOutline(g, SELECT_A_CHARACTER_TEXT, 520, 640, 2);
        g.setColor(Color.WHITE);
        g.drawString(SELECT_A_CHARACTER_TEXT, 520, 640);

        if (this.createPrompt) {
            final BufferedImage window = Globals.MENU_WINDOW[Globals.WINDOW_CREATECHAR];
            g.drawImage(window, 265, 135, null);
            drawStringOutline(g, ENTER_NEW_NAME_TEXT, 460, 250, 2);
            g.setColor(Color.WHITE);
            g.drawString(ENTER_NEW_NAME_TEXT, 460, 250);

            button = Globals.MENU_BUTTON[Globals.BUTTON_BIGRECT];
            g.drawImage(button, 401, 400, null);
            drawStringOutline(g, CREATE_BUTTON_TEXT, 460, 465, 2);
            g.setColor(Color.WHITE);
            g.drawString(CREATE_BUTTON_TEXT, 460, 465);

            g.drawImage(button, 665, 400, null);
            drawStringOutline(g, CANCEL_BUTTON_TEXT, 725, 465, 2);
            g.setColor(Color.WHITE);
            g.drawString(CANCEL_BUTTON_TEXT, 725, 465);

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

    private void mouseReleased_Create(final MouseEvent e) throws InstantiationException, IllegalAccessException {
        Point2D.Double scaled;
        if (Globals.WINDOW_SCALE_ENABLED) {
            scaled = new Point2D.Double(e.getX() / Globals.WINDOW_SCALE, e.getY() / Globals.WINDOW_SCALE);
        } else {
            scaled = new Point2D.Double(e.getX(), e.getY());
        }
        panel.requestFocus();
        for (byte i = 0; i < PROMPT_BOX.length; i++) {
            if (PROMPT_BOX[i].contains(scaled)) {
                if (i == 0) {
                    CREATE_NAMEFIELD.setText(CREATE_NAMEFIELD.getText().trim());
                    if (CREATE_NAMEFIELD.getText().length() <= 15 && CREATE_NAMEFIELD.getText().length() > 0) {
                        final SaveData newChar = new SaveData(CREATE_NAMEFIELD.getText().trim(), this.selectNum);
                        newChar.newCharacter(Globals.TEST_MAX_LEVEL);
                        SaveData.writeSaveData(this.selectNum, newChar);
                        loadSaveData();
                    } else {
                        if (CREATE_NAMEFIELD.getText().length() <= 0) {
                            this.CREATE_ERR = MIN_ONE_CHARACTER_ERR_TEXT;
                        } else if (CREATE_NAMEFIELD.getText().length() > 15) {
                            this.CREATE_ERR = LESS_THAN_FIFTEEN_CHAR_ERR_TEXT;
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
        Point2D.Double scaled;
        if (Globals.WINDOW_SCALE_ENABLED) {
            scaled = new Point2D.Double(e.getX() / Globals.WINDOW_SCALE, e.getY() / Globals.WINDOW_SCALE);
        } else {
            scaled = new Point2D.Double(e.getX(), e.getY());
        }
        if (this.fadeIn && !this.finishedFadeIn) {
            return;
        }
        if (SwingUtilities.isLeftMouseButton(e)) {
            if (this.createPrompt) {
                try {
                    mouseReleased_Create(e);
                } catch (InstantiationException | IllegalAccessException ex) {
                    Globals.logError(ex.toString(), ex);
                }
                return;
            }
            for (byte i = 0; i < SELECT_BOX.length; i++) {
                if (SELECT_BOX[i].contains(scaled)) {
                    if (CHARACTER_DATA[i] == null) {
                        this.createPrompt = true;
                        if (panel != null) {
                            panel.add(CREATE_NAMEFIELD);
                            panel.revalidate();
                        }
                        this.selectNum = i;
                        break;
                    } else {
                        Core.getLogicModule().setSelectedSaveData(CHARACTER_DATA[i]);
                        Core.getLogicModule().setScreen(new ScreenStats());
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
