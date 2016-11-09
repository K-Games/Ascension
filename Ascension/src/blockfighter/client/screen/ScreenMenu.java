package blockfighter.client.screen;

import blockfighter.shared.Globals;
import blockfighter.client.SaveData;
import blockfighter.client.entities.particles.Particle;
import blockfighter.client.entities.particles.ParticleMenuSmoke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.swing.SwingUtilities;

public abstract class ScreenMenu extends Screen {

    protected long lastUpdateTime = 0;
    protected static ConcurrentHashMap<Integer, Particle> particles = new ConcurrentHashMap<>(3);
    private static final Rectangle2D.Double[] MENU_BOX = new Rectangle2D.Double[7];
    protected boolean fadeIn = false;

    private long fadeInStart = System.nanoTime();
    private Color fadeInColor;
    protected boolean finishedFadeIn = false;

    protected DecimalFormat df = new DecimalFormat("###,###,##0.##");

    static {
        for (int i = 0; i < MENU_BOX.length; i++) {
            MENU_BOX[i] = new Rectangle2D.Double(20, 27 + 50 * i, 180, 50);
        }
    }

    public ScreenMenu() {
        this(false);
    }

    public ScreenMenu(final boolean fadeIn) {
        if (!particles.containsKey(0)) {
            particles.put(0, new ParticleMenuSmoke(0, 0, 0));
            particles.put(1, new ParticleMenuSmoke(1, 1280, 0));
        }
        this.fadeIn = fadeIn;
    }

    @Override
    public ConcurrentHashMap<Integer, Particle> getParticles() {
        return particles;
    }

    @Override
    public byte getBGM() {
        return Globals.BGM_MENU;
    }

    @Override
    public void update() {
        final long now = logic.getTime(); // Get time now
        if (now - this.lastUpdateTime >= Globals.CLIENT_LOGIC_UPDATE) {
            updateParticles(particles);
            this.lastUpdateTime = now;
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
    }

    @Override
    public void draw(final Graphics2D g) {
        for (final Map.Entry<Integer, Particle> pEntry : particles.entrySet()) {
            pEntry.getValue().draw(g);
        }
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
        drawStringOutline(g, "Stats", 40, 62, 2);
        drawStringOutline(g, "Inventory", 40, 112, 2);
        drawStringOutline(g, "Upgrades", 40, 162, 2);
        drawStringOutline(g, "Skills", 40, 212, 2);
        drawStringOutline(g, "Login", 40, 262, 2);
        drawStringOutline(g, "Key Bindings", 40, 312, 2);
        drawStringOutline(g, "Characters", 40, 362, 2);

        g.setColor(Color.WHITE);
        g.drawString("Stats", 40, 62);
        g.drawString("Inventory", 40, 112);
        g.drawString("Upgrades", 40, 162);
        g.drawString("Skills", 40, 212);
        g.drawString("Login", 40, 262);
        g.drawString("Key Bindings", 40, 312);
        g.drawString("Characters", 40, 362);
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
                    SaveData.saveData(logic.getSelectedChar().getSaveNum(), logic.getSelectedChar());
                    switch (i) {
                        case 0:
                            if (!(logic.getScreen() instanceof ScreenStats)) {
                                logic.setScreen(new ScreenStats());
                            }
                            break;
                        case 1:
                            if (!(logic.getScreen() instanceof ScreenInventory)) {
                                logic.setScreen(new ScreenInventory());
                            }
                            break;
                        case 2:
                            if (!(logic.getScreen() instanceof ScreenUpgrade)) {
                                logic.setScreen(new ScreenUpgrade());
                            }
                            break;
                        case 3:
                            if (!(logic.getScreen() instanceof ScreenSkills)) {
                                logic.setScreen(new ScreenSkills());
                            }
                            break;
                        case 4:
                            if (!(logic.getScreen() instanceof ScreenServerList)) {
                                logic.setScreen(new ScreenServerList());
                            }
                            break;
                        case 5:
                            if (!(logic.getScreen() instanceof ScreenKeyBind)) {
                                logic.setScreen(new ScreenKeyBind());
                            }
                            break;
                        case 6:
                            if (!(logic.getScreen() instanceof ScreenSelectChar)) {
                                logic.setSelectedChar(null);
                                logic.setScreen(new ScreenSelectChar());
                            }
                            break;
                    }
                }
            }
        }
    }
}
