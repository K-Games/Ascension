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

    public ScreenSelectChar(LogicModule l) {
        logic = l;
        particles.put(0, new ParticleMenuSmoke(l, 0, 0, 0, 0));
        particles.put(1, new ParticleMenuSmoke(l, 1, 1280, 0, 0));

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

        if (particles != null) {
            for (Map.Entry<Integer, Particle> pEntry : particles.entrySet()) {
                pEntry.getValue().draw(g);
            }
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
            for (int i = 0; i < 2; i++) {
                g.setColor(Color.BLACK);
                g.drawString("Create", 168 + 420 * j + i * 4, 260);
                g.drawString("New", 183 + 420 * j + i * 4, 310);
                g.drawString("Character", 148 + 420 * j + i * 4, 360);
                g.drawString("Create", 170 + 420 * j, 258 + i * 4);
                g.drawString("New", 185 + 420 * j, 308 + i * 4);
                g.drawString("Character", 150 + 420 * j, 358 + i * 4);
            }
            g.setColor(Color.WHITE);
            g.drawString("Create", 170 + 420 * j, 260);
            g.drawString("New", 185 + 420 * j, 310);
            g.drawString("Character", 150 + 420 * j, 360);
        }
        g.setColor(Color.BLACK);
        g.drawString("Connect", 572, 602);
        g.setColor(Color.WHITE);
        g.drawString("Connect", 570, 600);
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
