package blockfighter.client.screen;

import blockfighter.client.LogicModule;
import blockfighter.client.Main;
import blockfighter.client.entities.particles.Particle;
import blockfighter.client.render.RenderPanel;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;

/**
 *
 * @author Ken Kwan
 */
public abstract class Screen implements KeyListener, MouseListener, MouseMotionListener, FocusListener {

    protected static final Color SKILL_BOX_BG_COLOR = new Color(0, 0, 0, 115);

    public abstract void update();

    public abstract void draw(Graphics2D g);

    public abstract ConcurrentHashMap<Integer, Particle> getParticles();

    protected static ExecutorService threadPool;
    protected static RenderPanel panel;
    protected static LogicModule logic;

    private static final ConcurrentLinkedQueue<Integer> PARTICLE_KEYS = new ConcurrentLinkedQueue<>();
    private static int numParticleKeys = 500;

    public static void init() {
        logic = Main.getLogicModule();
        for (int key = 0; key < numParticleKeys; key++) {
            PARTICLE_KEYS.add(key);
        }
    }

    public static void setThreadPool(final ExecutorService tp) {
        threadPool = tp;
    }

    public void drawStringOutline(final Graphics2D g, final String s, final int x, final int y, final int width) {
        for (int i = 0; i < 2; i++) {
            g.setColor(Color.BLACK);
            g.drawString(s, x - width + i * 2 * width, y);
            g.drawString(s, x, y - width + i * 2 * width);
        }
    }

    public void updateParticles(final ConcurrentHashMap<Integer, Particle> particles) {
        for (final Map.Entry<Integer, Particle> pEntry : particles.entrySet()) {
            threadPool.execute(pEntry.getValue());
        }
        final LinkedList<Integer> remove = new LinkedList<>();
        for (final Map.Entry<Integer, Particle> pEntry : particles.entrySet()) {
            try {
                pEntry.getValue().join();
                if (pEntry.getValue().isExpired()) {
                    remove.add(pEntry.getKey());
                }
            } catch (final InterruptedException ex) {
            }
        }
        removeParticles(particles, remove);
    }

    private void removeParticles(final ConcurrentHashMap<Integer, Particle> particles, final LinkedList<Integer> remove) {
        while (!remove.isEmpty()) {
            particles.remove(remove.pop());
        }
    }

    public static void setRenderPanel(final RenderPanel r) {
        panel = r;
    }

    public abstract void unload();

    @Override
    public void focusGained(FocusEvent e) {
        logic.enableSound();
    }

    @Override
    public void focusLost(FocusEvent e) {
        logic.disableSound();
    }

    public byte getBGM() {
        return -1;
    }

    public int getNextParticleKey() {
        Integer nextKey = PARTICLE_KEYS.poll();
        while (nextKey == null) {
            PARTICLE_KEYS.add(numParticleKeys);
            numParticleKeys++;
            nextKey = PARTICLE_KEYS.poll();
        }
        return nextKey;
    }

    public void returnParticleKey(final int key) {
        PARTICLE_KEYS.add(key);
    }

    public void addParticle(final Particle newParticle) {
    }

}
