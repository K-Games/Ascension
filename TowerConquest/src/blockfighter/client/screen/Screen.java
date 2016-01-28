package blockfighter.client.screen;

import blockfighter.client.LogicModule;
import blockfighter.client.entities.particles.Particle;
import blockfighter.client.render.RenderPanel;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

/**
 *
 * @author Ken Kwan
 */
public abstract class Screen implements KeyListener, MouseListener, MouseMotionListener {

    public abstract void update();

    public abstract void draw(Graphics2D g);

    public abstract ConcurrentHashMap<Integer, Particle> getParticles();

    protected static ExecutorService threadPool;
    protected static RenderPanel panel;
    protected static LogicModule logic;

    public static void setLogic(final LogicModule l) {
        logic = l;
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
}
