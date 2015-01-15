package blockfighter.client.screen;

import blockfighter.client.Globals;
import blockfighter.client.LogicModule;
import blockfighter.client.SaveData;
import blockfighter.client.entities.particles.Particle;
import blockfighter.client.entities.particles.ParticleMenuSmoke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.swing.BorderFactory;
import javax.swing.JTextField;

/**
 *
 * @author Ken
 */
public class ScreenSelectChar extends Screen {

    private double lastUpdateTime = System.nanoTime();
    private ConcurrentHashMap<Integer, Particle> particles = new ConcurrentHashMap<>(20);
    private SaveData[] charsData = new SaveData[3];
    private LogicModule logic;

    private boolean createPrompt = false;
    private final JTextField CREATE_NAMEFIELD = new JTextField();
    private String CREATE_ERR = "";

    private final Rectangle[] promptBox = new Rectangle[2];

    private final Rectangle[] selectBox = new Rectangle[3];

    private byte selectNum = -1;

    public ScreenSelectChar(LogicModule l) {
        CREATE_NAMEFIELD.setBounds(440, 300, 400, 50);
        CREATE_NAMEFIELD.setFont(Globals.ARIAL_30PT);
        CREATE_NAMEFIELD.setForeground(Color.WHITE);
        CREATE_NAMEFIELD.setOpaque(false);
        CREATE_NAMEFIELD.setBorder(BorderFactory.createEmptyBorder());

        logic = l;
        particles.put(0, new ParticleMenuSmoke(l, 0, 0, 0, 0));
        particles.put(1, new ParticleMenuSmoke(l, 1, 1280, 0, 0));

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
    public void update() {
        double now = System.nanoTime(); //Get time now
        if (now - lastUpdateTime >= Globals.LOGIC_UPDATE) {
            updateParticles(particles);

            lastUpdateTime = now;
        }
        while (now - lastUpdateTime < Globals.LOGIC_UPDATE) {
            Thread.yield();
            now = System.nanoTime();
        }
    }

    @Override
    public void draw(Graphics g) {
        BufferedImage bg = Globals.MENU_BG[0];
        g.drawImage(bg, 0, 0, null);

        for (Map.Entry<Integer, Particle> pEntry : particles.entrySet()) {
            pEntry.getValue().draw(g);
        }

        BufferedImage button = Globals.MENU_BUTTON[Globals.BUTTON_OKAY];
        g.drawImage(button, 550, 550, null);

        button = Globals.MENU_BUTTON[Globals.BUTTON_SELECTCHAR];
        g.drawImage(button, 20, 60, null);
        g.drawImage(button, 440, 60, null);
        g.drawImage(button, 860, 60, null);

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(
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
                g.setFont(Globals.ARIAL_30PT);
                drawStringOutline(g, charsData[j].getPlayerName(), 120 + 420 * j, 380, 2);
                g.setColor(Color.WHITE);
                g.drawString(charsData[j].getPlayerName(), 120 + 420 * j, 380);

                g.setFont(Globals.ARIAL_24PT);
                drawStringOutline(g, "Level: " + charsData[j].getStats()[Globals.STAT_LEVEL], 120 + 420 * j, 415, 2);
                drawStringOutline(g, "Power: " + charsData[j].getStats()[Globals.STAT_POWER], 120 + 420 * j, 445, 2);
                drawStringOutline(g, "Defense: " + charsData[j].getStats()[Globals.STAT_DEFENSE], 120 + 420 * j, 475, 2);
                drawStringOutline(g, "Spirit: " + charsData[j].getStats()[Globals.STAT_SPIRIT], 120 + 420 * j, 505, 2);

                g.setColor(Color.WHITE);
                g.drawString("Level: " + charsData[j].getStats()[Globals.STAT_LEVEL], 120 + 420 * j, 415);
                g.drawString("Power: " + charsData[j].getStats()[Globals.STAT_POWER], 120 + 420 * j, 445);
                g.drawString("Defense: " + charsData[j].getStats()[Globals.STAT_DEFENSE], 120 + 420 * j, 475);
                g.drawString("Spirit: " + charsData[j].getStats()[Globals.STAT_SPIRIT], 120 + 420 * j, 505);
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

            button = Globals.MENU_BUTTON[Globals.BUTTON_OKAY];
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
    }

    @Override
    public ConcurrentHashMap<Integer, Particle> getParticles() {
        return particles;
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
        if (!createPrompt) {
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
                    }
                }
            }
        } else {
            for (byte i = 0; i < promptBox.length; i++) {
                if (promptBox[i].contains(e.getPoint())) {
                    if (i == 0) {
                        CREATE_NAMEFIELD.setText(CREATE_NAMEFIELD.getText().trim());
                        if (CREATE_NAMEFIELD.getText().length() <= 20 && CREATE_NAMEFIELD.getText().length() > 0) {
                            SaveData.saveData(selectNum, new SaveData(CREATE_NAMEFIELD.getText().trim()));
                            loadSaveData();
                        } else {
                            if (CREATE_NAMEFIELD.getText().length() <= 0) {
                                CREATE_ERR = "Name must have at least 1 character!";
                            } else if (CREATE_NAMEFIELD.getText().length() > 20) {
                                CREATE_ERR = "Name must be less than 20 characters!";
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

        if (new Rectangle(550, 550, 214, 112).contains(e.getPoint())) {
            logic.sendLogin();
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

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
