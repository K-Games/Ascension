package blockfighter.client.screen;

import blockfighter.client.entities.particles.Particle;
import blockfighter.shared.Globals;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.concurrent.ConcurrentHashMap;

public class ScreenTitle extends Screen {

    private long fadeInStart = System.nanoTime();
    private long fadeOutStart, lastUpdateTime, fontFadeStart;
    private Color fadeInColor, fadeOutColor, fontColor;
    private boolean exitingTitle = false, finishedFadeIn = false, fontFadeIn = false;
    private int bg1y = 0, bg2y = 720;

    @Override
    public void update() {
        final long now = logic.getTime(); // Get time now

        if (Globals.nsToMs(now - fadeInStart) < 5000) {
            int transparency = (int) (255 * (1f - Globals.nsToMs(now - fadeInStart) / 5000f));
            fadeInColor = new Color(255, 255, 255, (transparency < 0) ? 0 : transparency);
        } else {
            fadeInColor = new Color(255, 255, 255, 0);
            finishedFadeIn = true;
        }
        if (exitingTitle && Globals.nsToMs(now - fadeOutStart) < 2000) {
            int transparency = (int) (255 * Globals.nsToMs(now - fadeOutStart) / 2000f);
            fadeOutColor = new Color(0, 0, 0, (transparency < 0) ? 0 : transparency);
        }
        if (now - this.lastUpdateTime >= Globals.CLIENT_LOGIC_UPDATE) {
            bg1y--;
            bg2y--;
            if (bg1y <= -720) {
                bg1y = 720;
            }
            this.lastUpdateTime = now;
        }
        if (bg2y <= -720) {
            bg2y = 720;
        }
        if (Globals.nsToMs(now - fontFadeStart) < 1000) {
            int transparency = (fontFadeIn) ? (int) (255 * Globals.nsToMs(now - fontFadeStart) / 1000f)
                    : (int) (255 * (1f - Globals.nsToMs(now - fontFadeStart) / 1000f));
            fontColor = new Color(160, 0, 0, (transparency < 0) ? 0 : (transparency > 255) ? 255 : transparency);
        }

        if (Globals.nsToMs(now - fontFadeStart) >= 1000) {
            fontFadeIn = !fontFadeIn;
            fontFadeStart = now;
        }

        if (exitingTitle && Globals.nsToMs(now - fadeOutStart) >= 2000) {
            logic.setScreen(new ScreenSelectChar(true));
        }

    }

    @Override
    public void draw(Graphics2D g) {
        final BufferedImage bg = Globals.MENU_BG[2];
        g.drawImage(bg, 0, bg1y, 1280, 720, null);
        g.drawImage(bg, 0, bg2y + 720, 1280, bg2y, 0, 0, bg.getWidth(), bg.getHeight(), null);
        g.drawImage(Globals.TITLE, Globals.WINDOW_WIDTH / 2 - Globals.TITLE.getWidth() / 2, 100, null);

        g.setFont(Globals.ARIAL_24PT);
        g.setColor(fontColor);
        String click = "Click to continue";
        g.drawString(click, Globals.WINDOW_WIDTH / 2 - g.getFontMetrics().stringWidth(click) / 2, 500);

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
        if (!exitingTitle) {
            exitingTitle = true;
            fadeOutStart = System.nanoTime();
        }
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
