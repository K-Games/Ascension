package blockfighter.client.render;

import blockfighter.client.Globals;
import blockfighter.client.entities.Player;
import blockfighter.client.entities.particles.Particle;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 *
 * @author ckwa290
 */
public class RenderPanel extends JPanel {

    private Font menuButtonFont = new Font("Arial", Font.PLAIN, 30);
    private Font readFont = new Font("Arial", Font.PLAIN, 12);

    private int FPSCount = 0;
    private int ping = 0;
    private Player[] players;
    private byte myIndex = -1;
    private byte screen = Globals.SCREEN_CHAR_SELECT;

    private ConcurrentHashMap<Integer, Particle> particles;

    public RenderPanel() {
        super();
        setBackground(Color.WHITE);
        setLayout(null);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        switch (screen) {
            case Globals.SCREEN_CHAR_SELECT:
                paintSelectMenu(g);
                break;
            case Globals.SCREEN_INGAME:
                paintIngame(g);
                break;
        }
        
        g.setFont(readFont);
        for (int i = 0; i < 2; i++) {
            g.setColor(Color.BLACK);
            g.drawString("FPS: " + FPSCount, 1199 + i * 2, 20);
            g.drawString("FPS: " + FPSCount, 1200, 19 + i * 2);
        }
        g.setColor(Color.WHITE);
        g.drawString("FPS: " + FPSCount, 1200, 20);
    }

    private void paintSelectMenu(Graphics g) {
        BufferedImage bg = Globals.MENU_BG[0];
        g.drawImage(bg, 0, 0, null);

        if (particles != null) {
            Iterator<Integer> partItr = particles.keySet().iterator();
            while (partItr.hasNext()) {
                Integer key = partItr.next();
                if (particles.get(key) != null) {
                    particles.get(key).draw(g);
                }
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
        g.setFont(menuButtonFont);

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

    private void paintIngame(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        AffineTransform resetForm = g2d.getTransform();

        if (players != null && myIndex != -1 && players[myIndex] != null) {
            ((Graphics2D) g).translate(640.0 - players[myIndex].getX(), 600.0 - players[myIndex].getY());
        }
        
        if (players != null) {
            for (Player player : players) {
                if (player != null) {
                    player.draw(g);
                }
            }
        }
        
        if (particles != null) {
            Iterator<Integer> partItr = particles.keySet().iterator();
            while (partItr.hasNext()) {
                Integer key = partItr.next();
                if (particles.get(key) != null) {
                    particles.get(key).draw(g);
                }
            }
        }

        g.drawRect(0, 620, 5000, 30);
        g.drawRect(200, 400, 300, 30);
        g.drawRect(600, 180, 300, 30);

        ((Graphics2D) g).setTransform(resetForm);

        //BufferedImage hud = Globals.HUD[0];
        //g.drawImage(hud, Globals.WINDOW_WIDTH / 2 - hud.getWidth() / 2, Globals.WINDOW_HEIGHT - hud.getHeight(), null);
        g.drawString("Ping: " + ping, 1200, 40);
    }

    public void setFPSCount(int f) {
        FPSCount = f;
    }

    public void setPing(int p) {
        ping = p;
    }

    public void setPlayers(Player[] p) {
        if (p != null) {
            if (players == null) {
                players = new Player[p.length];
            }
            System.arraycopy(p, 0, players, 0, p.length);
        }
    }

    public void setParticles(ConcurrentHashMap<Integer, Particle> p) {
        particles = p;
    }

    public void setMyIndex(byte i) {
        myIndex = i;
    }

    public void setScreen(byte s) {
        screen = s;
    }
}
