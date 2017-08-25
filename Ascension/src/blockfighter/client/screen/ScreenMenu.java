package blockfighter.client.screen;

import blockfighter.client.Core;
import blockfighter.client.entities.particles.Particle;
import blockfighter.client.entities.particles.menu.ParticleMenuSmoke;
import blockfighter.shared.Globals;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import javax.swing.SwingUtilities;

public abstract class ScreenMenu extends Screen {

    private static final String CHARACTERS_TEXT = "Characters";
    private static final String KEY_BINDINGS_TEXT = "Key Bindings";
    private static final String LOGIN_TEXT = "Play";
    private static final String SKILLS_TEXT = "Skills";
    private static final String UPGRADES_TEXT = "Infusion";
    private static final String INVENTORY_TEXT = "Inventory";
    private static final String STATS_TEXT = "Stats";

    private static final String[] BUTTON_TEXT = {STATS_TEXT,
        INVENTORY_TEXT,
        UPGRADES_TEXT,
        SKILLS_TEXT,
        LOGIN_TEXT,
        KEY_BINDINGS_TEXT,
        CHARACTERS_TEXT
    };

    private static final ArrayList<Class<? extends Screen>> SCREEN_CLASS = new ArrayList<>(Arrays.asList(
            ScreenStats.class,
            ScreenInventory.class,
            ScreenUpgrade.class,
            ScreenSkills.class,
            ScreenServerList.class,
            ScreenKeyBind.class,
            ScreenSelectChar.class
    ));

    protected long lastParticleUpdateTime = 0, lastUpdateTime = 0;
    private long lastSaveValidateTime = 0;

    protected final static ConcurrentHashMap<Integer, Particle> PARTICLES = new ConcurrentHashMap<>(3);
    private static final Rectangle2D.Double[] MENU_BOX = new Rectangle2D.Double[7];
    protected boolean fadeIn = false;

    protected Point2D.Double mousePos;

    private long fadeInStart = System.nanoTime();
    private Color fadeInColor;
    protected boolean finishedFadeIn = false;

    static {
        for (int i = 0; i < MENU_BOX.length; i++) {
            MENU_BOX[i] = new Rectangle2D.Double(20, 27 + 50 * i, 180, 50);
        }
        Particle[] smoke = {new ParticleMenuSmoke(0, 0), new ParticleMenuSmoke(1280, 0)};
        PARTICLES.put(smoke[0].getKey(), smoke[0]);
        PARTICLES.put(smoke[1].getKey(), smoke[1]);
    }

    public ScreenMenu() {
        this(false);
    }

    public ScreenMenu(final boolean fadeIn) {
        this.fadeIn = fadeIn;
    }

    @Override
    public ConcurrentHashMap<Integer, Particle> getParticles() {
        return PARTICLES;
    }

    @Override
    public byte getBgmCode() {
        return Globals.BGMs.MENU.getBgmCode();
    }

    @Override
    public void update() {
        final long now = Core.getLogicModule().getTime(); // Get time now
        if (now - this.lastParticleUpdateTime >= Globals.CLIENT_LOGIC_UPDATE) {
            updateParticles(PARTICLES);
            this.lastParticleUpdateTime = now;
            if (fadeIn) {
                if (!finishedFadeIn && Globals.nsToMs(now - fadeInStart) < 2000) {
                    int transparency = (int) (255 * (1f - Globals.nsToMs(now - fadeInStart) / 2000f));
                    fadeInColor = new Color(0, 0, 0, (transparency < 0) ? 0 : transparency);
                } else {
                    fadeInColor = new Color(0, 0, 0, 0);
                    finishedFadeIn = true;
                }
            }
        }

        if (Core.getLogicModule().getSelectedChar() != null && now - lastSaveValidateTime >= Globals.msToNs(3000)) {
            Core.getLogicModule().getSelectedChar().validate();
            lastSaveValidateTime = now;
        }
    }

    @Override
    public void draw(final Graphics2D g) {
        PARTICLES.entrySet().forEach((pEntry) -> {
            pEntry.getValue().draw(g);
        });
        if (this.fadeIn && !this.finishedFadeIn) {
            g.setColor(fadeInColor);
            g.fillRect(0, 0, Globals.WINDOW_WIDTH, Globals.WINDOW_HEIGHT);
        }
    }

    public void drawMenuButton(final Graphics2D g) {
        final BufferedImage button = Globals.MENU_BUTTON[Globals.BUTTON_MENUS];
        for (final Rectangle2D.Double menuBox1 : MENU_BOX) {
            g.drawImage(button, (int) menuBox1.x, (int) menuBox1.y, null);
        }
        g.setFont(Globals.ARIAL_24PT);

        for (int i = 0; i < BUTTON_TEXT.length; i++) {
            drawStringOutline(g, BUTTON_TEXT[i], (int) (MENU_BOX[i].x + 20), (int) (MENU_BOX[i].y + 35), 2);
            g.setColor(Color.WHITE);
            g.drawString(BUTTON_TEXT[i], (int) (MENU_BOX[i].x + 20), (int) (MENU_BOX[i].y + 35));
        }

    }

    @Override
    public void mouseReleased(final MouseEvent e) {
        panel.requestFocus();
        Point2D.Double scaled;
        if (Globals.WINDOW_SCALE_ENABLED) {
            scaled = new Point2D.Double(e.getX() / Globals.WINDOW_SCALE, e.getY() / Globals.WINDOW_SCALE);
        } else {
            scaled = new Point2D.Double(e.getX(), e.getY());
        }
        if (SwingUtilities.isLeftMouseButton(e)) {
            for (byte i = 0; i < MENU_BOX.length; i++) {
                if (MENU_BOX[i].contains(scaled)) {
                    if (!(Core.getLogicModule().getScreen().getClass() == SCREEN_CLASS.get(i))) {
                        try {
                            Core.getLogicModule().setScreen(SCREEN_CLASS.get(i).newInstance());
                        } catch (InstantiationException | IllegalAccessException ex) {
                            Globals.logError(ex.toString(), ex);
                        }
                    }
                    break;
                }
            }
        }
    }
}
