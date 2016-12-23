package blockfighter.client.screen;

import blockfighter.client.AscensionClient;
import blockfighter.client.entities.particles.Particle;
import blockfighter.shared.Globals;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

public class ScreenTitle extends Screen {

    private static final String GAME_PORTAL_URL = "https://kenofnz.itch.io/ascension";
    private static final String VERSION_API_URL = "https://itch.io/api/1/x/wharf/latest?target=kenofnz/ascension&channel_name=windows";
    private final static String CLICK_TEXT = "Click to start";

    private final long FADE_IN_START_TIME = System.nanoTime();
    private long fadeOutStart, lastUpdateTime, fontFadeStart;
    private Color fadeInColor, fadeOutColor, fontColor;
    private boolean exitingTitle = false, finishedFadeIn = false, fontFadeIn = false, checkingVersion = false;
    private int bg1y = 0, bg2y = 720;

    private byte checkVersionStatus = CHECK_VERSION_STATUS_CHECKING;
    private boolean updatePrompt = false;
    private float checkVerisionCircleFactor = 0f;
    private byte checkVersionMajor, checkVersionMinor, checkVersionUpdate;
    private String remoteVerison;

    private final Color CHECK_UPDATE_COLOR = new Color(0, 0, 0, 125);
    private final static String[] CHECK_VERSION_STATUS_TEXT = {
        "Checking for updates...",
        "Game is up to date.",
        "A new update is available.",
        "Unable to get version data."
    };

    private final static String[] CHECK_VERSION_BUTTON_TEXT = {
        "Continue", "Get "
    };

    private final static byte CHECK_VERSION_STATUS_CHECKING = 0;
    private final static byte CHECK_VERSION_STATUS_UPTODATE = 1;
    private final static byte CHECK_VERSION_STATUS_OUTDATED = 2;
    private final static byte CHECK_VERSION_STATUS_UNAVAILABLE = 3;
    private final static Rectangle[] UPDATE_PROMPT_BOX = new Rectangle[2];

    static {
        UPDATE_PROMPT_BOX[0] = new Rectangle(Globals.WINDOW_WIDTH / 2 - 175, Globals.WINDOW_HEIGHT / 2 - 75, 350, 150);
        UPDATE_PROMPT_BOX[1] = new Rectangle(UPDATE_PROMPT_BOX[0].x + UPDATE_PROMPT_BOX[0].width / 2 - 75, UPDATE_PROMPT_BOX[0].y + 70, 150, 50);
    }

    @Override
    public void update() {
        final long now = logic.getTime(); // Get time now

        if (Globals.nsToMs(now - FADE_IN_START_TIME) < 5000) {
            int transparency = (int) (255 * (1f - Globals.nsToMs(now - FADE_IN_START_TIME) / 5000f));
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
            if (this.checkingVersion && this.checkVersionStatus == CHECK_VERSION_STATUS_CHECKING) {
                checkVerisionCircleFactor += 0.01;
                checkVerisionCircleFactor = (checkVerisionCircleFactor >= 1) ? 0 : checkVerisionCircleFactor;
            }
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
        g.drawString(CLICK_TEXT, Globals.WINDOW_WIDTH / 2 - g.getFontMetrics().stringWidth(CLICK_TEXT) / 2, 500);

        if (!finishedFadeIn) {
            g.setColor(fadeInColor);
            g.fillRect(0, 0, Globals.WINDOW_WIDTH, Globals.WINDOW_HEIGHT);
        }

        if (checkingVersion) {
            g.setColor(CHECK_UPDATE_COLOR);
            g.fillRect(0, 0, Globals.WINDOW_WIDTH, Globals.WINDOW_HEIGHT);

            g.drawRect(UPDATE_PROMPT_BOX[0].x, UPDATE_PROMPT_BOX[0].y, UPDATE_PROMPT_BOX[0].width, UPDATE_PROMPT_BOX[0].height);
            g.setColor(CHECK_UPDATE_COLOR);
            g.fillRect(UPDATE_PROMPT_BOX[0].x, UPDATE_PROMPT_BOX[0].y, UPDATE_PROMPT_BOX[0].width, UPDATE_PROMPT_BOX[0].height);
            g.setColor(Color.BLACK);
            g.drawRect(UPDATE_PROMPT_BOX[0].x, UPDATE_PROMPT_BOX[0].y, UPDATE_PROMPT_BOX[0].width, UPDATE_PROMPT_BOX[0].height);
            g.drawRect(UPDATE_PROMPT_BOX[0].x + 1, UPDATE_PROMPT_BOX[0].y + 1, UPDATE_PROMPT_BOX[0].width - 2, UPDATE_PROMPT_BOX[0].height - 2);

            g.setFont(Globals.ARIAL_24PT);
            g.setColor(Color.WHITE);
            g.drawString(CHECK_VERSION_STATUS_TEXT[checkVersionStatus], (UPDATE_PROMPT_BOX[0].x + UPDATE_PROMPT_BOX[0].width / 2) - g.getFontMetrics().stringWidth(CHECK_VERSION_STATUS_TEXT[checkVersionStatus]) / 2, UPDATE_PROMPT_BOX[0].y + 50);
            if (this.checkVersionStatus == CHECK_VERSION_STATUS_CHECKING) {
                g.fillArc((UPDATE_PROMPT_BOX[0].x + UPDATE_PROMPT_BOX[0].width / 2) - 15, (UPDATE_PROMPT_BOX[0].y + 20 + UPDATE_PROMPT_BOX[0].height / 2) - 15, 30, 30, 90, (int) (checkVerisionCircleFactor * 360));
                g.drawOval((UPDATE_PROMPT_BOX[0].x + UPDATE_PROMPT_BOX[0].width / 2) - 15, (UPDATE_PROMPT_BOX[0].y + 20 + UPDATE_PROMPT_BOX[0].height / 2) - 15, 30, 30);
            }
            if (updatePrompt) {
                g.drawRect(UPDATE_PROMPT_BOX[1].x, UPDATE_PROMPT_BOX[1].y, UPDATE_PROMPT_BOX[1].width, UPDATE_PROMPT_BOX[1].height);
                g.setColor(CHECK_UPDATE_COLOR);
                g.fillRect(UPDATE_PROMPT_BOX[1].x, UPDATE_PROMPT_BOX[1].y, UPDATE_PROMPT_BOX[1].width, UPDATE_PROMPT_BOX[1].height);
                g.setColor(Color.BLACK);
                g.drawRect(UPDATE_PROMPT_BOX[1].x, UPDATE_PROMPT_BOX[1].y, UPDATE_PROMPT_BOX[1].width, UPDATE_PROMPT_BOX[1].height);
                g.drawRect(UPDATE_PROMPT_BOX[1].x + 1, UPDATE_PROMPT_BOX[1].y + 1, UPDATE_PROMPT_BOX[1].width - 2, UPDATE_PROMPT_BOX[1].height - 2);
                g.setFont(Globals.ARIAL_24PT);
                g.setColor(Color.WHITE);
                if (checkVersionStatus == CHECK_VERSION_STATUS_UPTODATE || checkVersionStatus == CHECK_VERSION_STATUS_UNAVAILABLE) {
                    g.drawString(CHECK_VERSION_BUTTON_TEXT[0], UPDATE_PROMPT_BOX[1].x + UPDATE_PROMPT_BOX[1].width / 2 - g.getFontMetrics().stringWidth(CHECK_VERSION_BUTTON_TEXT[0]) / 2, UPDATE_PROMPT_BOX[1].y + 32);
                } else if (checkVersionStatus == CHECK_VERSION_STATUS_OUTDATED) {
                    g.drawString(CHECK_VERSION_BUTTON_TEXT[1] + this.remoteVerison, UPDATE_PROMPT_BOX[1].x + UPDATE_PROMPT_BOX[1].width / 2 - g.getFontMetrics().stringWidth(CHECK_VERSION_BUTTON_TEXT[1] + this.remoteVerison) / 2, UPDATE_PROMPT_BOX[1].y + 32);
                }
            }
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
        Point2D.Double scaled;
        if (Globals.WINDOW_SCALE_ENABLED) {
            scaled = new Point2D.Double(e.getX() / Globals.WINDOW_SCALE, e.getY() / Globals.WINDOW_SCALE);
        } else {
            scaled = new Point2D.Double(e.getX(), e.getY());
        }
        if (!checkingVersion) {
            this.checkingVersion = true;
            AscensionClient.SHARED_THREADPOOL.execute(() -> {
                BufferedReader in;
                try {
                    URL ipURL = new URL(VERSION_API_URL);
                    JSONObject json = new JSONObject(IOUtils.toString(ipURL, "UTF-8"));
                    this.remoteVerison = json.getString("latest");
                    String gameVersion = Globals.GAME_MAJOR_VERSION + "." + Globals.GAME_MINOR_VERSION + "." + Globals.GAME_UPDATE_NUMBER;
                    if (gameVersion.equals(this.remoteVerison)) {
                        this.updatePrompt = true;
                        this.checkVersionStatus = CHECK_VERSION_STATUS_UPTODATE;
                    } else {
                        this.updatePrompt = true;
                        this.checkVersionStatus = CHECK_VERSION_STATUS_OUTDATED;
                    }
                } catch (Exception ex) {
                    this.updatePrompt = true;
                    this.checkVersionStatus = CHECK_VERSION_STATUS_UNAVAILABLE;
                }
            });
        }
        if (!exitingTitle && updatePrompt && UPDATE_PROMPT_BOX[1].contains(scaled)) {
            if (checkVersionStatus == CHECK_VERSION_STATUS_UPTODATE || checkVersionStatus == CHECK_VERSION_STATUS_UNAVAILABLE) {
                exitingTitle = true;
                fadeOutStart = System.nanoTime();
            } else if (checkVersionStatus == CHECK_VERSION_STATUS_OUTDATED) {
                Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
                if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
                    try {
                        desktop.browse(URI.create(GAME_PORTAL_URL));
                    } catch (IOException ex) {
                        Globals.logError(ex.getMessage(), ex, true);
                    } finally {
                        System.exit(0);
                    }
                }
            }
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
