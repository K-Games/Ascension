package blockfighter.client.render;

import blockfighter.client.Globals;
import blockfighter.client.entities.particles.Particle;
import blockfighter.client.entities.Player;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import javax.swing.*;

/**
 *
 * @author ckwa290
 */
public class RenderPanel extends JPanel {

    private int FPSCount = 0;
    private int ping = 0;
    private Player[] players;
    private byte myIndex = -1;
    private byte screen = Globals.SCREEN_CHAR_SELECT;

    private ConcurrentHashMap<Integer, Particle> particles;

    public RenderPanel() {
        super();
        setBackground(Color.WHITE);
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
    }

    private void paintSelectMenu(Graphics g) {
        BufferedImage bg = Globals.MENU_BG[0];
        BufferedImage button = Globals.MENU_BUTTON[0];
        g.drawImage(bg, 0, 0, null);
        g.drawImage(button, 550, 550, null);
    }

    private void paintIngame(Graphics g) {
        AffineTransform resetForm = ((Graphics2D) g).getTransform();

        if (players != null && myIndex != -1 && players[myIndex] != null) {
            ((Graphics2D) g).translate(640.0 - players[myIndex].getX(), 600.0 - players[myIndex].getY());
        }
        g.setColor(Color.BLACK);

        if (players != null) {
            for (Player player : players) {
                if (player != null) {
                    try {
                        player.draw(g);
                    } catch (Exception e) {
                    }
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
        g.drawString("FPS: " + FPSCount, 1200, 20);
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
