package blockfighter.client.screen;

import blockfighter.client.entities.particles.Particle;
import blockfighter.client.render.RenderPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author Ken
 */
public abstract class Screen implements KeyListener, MouseListener, MouseMotionListener {

    public abstract void update();

    public abstract void draw(Graphics g);

    public abstract ConcurrentHashMap<Integer, Particle> getParticles();

    protected static ExecutorService threadPool = Executors.newFixedThreadPool(10);
    protected RenderPanel panel;
    
    public void drawStringOutline(Graphics g, String s, int x, int y, int width) {
        for (int i = 0; i < 2; i++) {
            g.setColor(Color.BLACK);
            g.drawString(s, x - width + i * 2 * width, y);
            g.drawString(s, x, y - width + i * 2 * width);
        }
    }

    public void updateParticles(ConcurrentHashMap<Integer, Particle> particles) {
        for (Map.Entry<Integer, Particle> pEntry : particles.entrySet()) {
            threadPool.execute(pEntry.getValue());
        }
        for (Map.Entry<Integer, Particle> pEntry : particles.entrySet()) {
            try {
                pEntry.getValue().join();
            } catch (InterruptedException ex) {
            }
        }
        removeParticles(particles);
    }

    private void removeParticles(ConcurrentHashMap<Integer, Particle> particles) {
        LinkedList<Integer> remove = new LinkedList<>();
        for (Map.Entry<Integer, Particle> pEntry : particles.entrySet()) {
            Particle p = pEntry.getValue();
            if (p.isExpired()) {
                remove.add(pEntry.getKey());
            }
        }
        while (!remove.isEmpty()) {
            particles.remove(remove.pop());
        }
    }
    
    public void setRenderPanel(RenderPanel r) {
        panel = r;
    }
}
