package blockfighter.client.screen;

import blockfighter.client.Globals;
import blockfighter.client.entities.particles.Particle;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.concurrent.ConcurrentHashMap;

public class ScreenTitle extends Screen {

    private long fadeInStart = System.currentTimeMillis();
    private long fadeOutStart;
    private Color fadeInColor, fadeOutColor;
    private boolean exitingTitle = false, finishedFadeIn = false;

    @Override
    public void update() {
        long curTime = System.currentTimeMillis();
        if (curTime - fadeInStart < 5000) {
            int transparency = (int) (255 * (1f - (curTime - fadeInStart) / 3000f));
            fadeInColor = new Color(255, 255, 255, (transparency < 0) ? 0 : transparency);
        } else {
            fadeInColor = new Color(255, 255, 255, 0);
            finishedFadeIn = true;
        }
        if (exitingTitle && curTime - fadeOutStart < 2000) {
            int transparency = (int) (255 * (curTime - fadeOutStart) / 2000f);
            fadeOutColor = new Color(0, 0, 0, (transparency < 0) ? 0 : transparency);
        } else {
            fadeOutColor = new Color(0, 0, 0, 255);
        }
        if (exitingTitle && curTime - fadeOutStart >= 3000) {
            logic.setScreen(new ScreenSelectChar(true));
        }
    }

    @Override
    public void draw(Graphics2D g) {
        final BufferedImage bg = Globals.MENU_BG[2];
        g.drawImage(bg, 0, 0, 1280, 720, null);
        if (!finishedFadeIn) {
            g.setColor(fadeInColor);
            g.fillRect(0, 0, Globals.WINDOW_WIDTH, Globals.WINDOW_HEIGHT);
        }
        if (exitingTitle) {
            g.setColor(fadeOutColor);
            g.fillRect(0, 0, Globals.WINDOW_WIDTH, Globals.WINDOW_HEIGHT);
        }
    }

    @Override
    public byte getBGM() {
        return Globals.BGM_TITLE;
    }

    @Override
    public ConcurrentHashMap<Integer, Particle> getParticles() {
        return null;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        exitingTitle = true;
        fadeOutStart = System.currentTimeMillis();
    }

    @Override
    public void unload() {
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
