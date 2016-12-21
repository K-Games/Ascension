package blockfighter.client.screen;

import blockfighter.client.AscensionClient;
import blockfighter.client.LogicModule;
import blockfighter.client.entities.particles.Particle;
import blockfighter.client.render.RenderPanel;
import blockfighter.shared.Globals;
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
import java.util.concurrent.Future;

public abstract class Screen implements KeyListener, MouseListener, MouseMotionListener, FocusListener {

    protected static final Color SKILL_BOX_BG_COLOR = new Color(0, 0, 0, 115);

    public abstract void update();

    public abstract void draw(Graphics2D g);

    public abstract ConcurrentHashMap<Integer, Particle> getParticles();

    protected static RenderPanel panel;
    protected static LogicModule logic;

    private static final ConcurrentLinkedQueue<Integer> PARTICLE_KEYS = new ConcurrentLinkedQueue<>();
    private static int numParticleKeys = 500;

    public static void init() {
        logic = AscensionClient.getLogicModule();
        for (int key = 0; key < numParticleKeys; key++) {
            PARTICLE_KEYS.add(key);
        }
    }

    public void drawStringOutline(final Graphics2D g, final String s, final int x, final int y, final int width) {
        for (int i = 0; i < 2; i++) {
            g.setColor(Color.BLACK);
            g.drawString(s, x - width + i * 2 * width, y);
            g.drawString(s, x, y - width + i * 2 * width);
        }
    }

    public void updateParticles(final ConcurrentHashMap<Integer, Particle> updateParticles) {
        LinkedList<Future<Particle>> futures = new LinkedList<>();
        for (final Map.Entry<Integer, Particle> pEntry : updateParticles.entrySet()) {
            futures.add(AscensionClient.SHARED_THREADPOOL.submit(pEntry.getValue()));
        }
        for (Future<Particle> task : futures) {
            try {
                Particle particle = task.get();
                if (particle.isExpired()) {
                    updateParticles.remove(particle.getKey());
                }
            } catch (final Exception ex) {
                Globals.logError(ex.toString(), ex, true);
            }
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
