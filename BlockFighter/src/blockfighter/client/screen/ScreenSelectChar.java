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
    private Rectangle[] selectBox = new Rectangle[3];

    public ScreenSelectChar(LogicModule l) {
        logic = l;
        particles.put(0, new ParticleMenuSmoke(l, 0, 0, 0, 0));
        particles.put(1, new ParticleMenuSmoke(l, 1, 1280, 0, 0));

        for (byte i = 0; i < charsData.length; i++) {
            charsData[i] = SaveData.readData(i);
            selectBox[i] = new Rectangle(20 + 420 * i, 60, 400, 500);
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
        g.setFont(Globals.ARIAL_30PT);

        for (int j = 0; j < 3; j++) {
            if (charsData[j] == null) {

                drawStringOutline(g, "Create", 170 + 420 * j, 260, 2);
                drawStringOutline(g, "New", 185 + 420 * j, 310, 2);
                drawStringOutline(g, "Character", 150 + 420 * j, 360, 2);

                g.setColor(Color.WHITE);
                g.drawString("Create", 170 + 420 * j, 260);
                g.drawString("New", 185 + 420 * j, 310);
                g.drawString("Character", 150 + 420 * j, 360);
            }
        }

        drawStringOutline(g, "Select a Character", 520, 640, 2);
        g.setColor(Color.WHITE);
        g.drawString("Select a Character", 520, 640);

        if (createPrompt) {
            BufferedImage window = Globals.MENU_WINDOW[Globals.WINDOW_CREATECHAR];
            g.drawImage(window, 265, 135, null);
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
        for (byte i = 0; i < selectBox.length; i++) {
            if (selectBox[i].contains(e.getPoint())) {
                if (charsData[i] == null) {
                    createPrompt = true;
                }
            }
        }

        if (new Rectangle(550, 550, 214, 112).contains(e.getPoint())) {
            
            for (byte i = 0; i < 30; i++) {
                logic.sendLogin();
            }
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
