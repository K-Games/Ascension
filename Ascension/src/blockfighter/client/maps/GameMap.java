package blockfighter.client.maps;

import blockfighter.client.Globals;
import blockfighter.client.entities.particles.Particle;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

public abstract class GameMap {

    protected ConcurrentHashMap<Integer, Particle> particles = new ConcurrentHashMap<>(20);
    protected long lastUpdateTime = 0;
    protected static ExecutorService threadPool;
    protected int mapHeight, mapWidth, mapXOrigin = 0, mapYOrigin = 0;
    private final static double PARALLAX_FACTOR = 0.35;
    private int mapID = -1;
    BufferedImage bg;

    public ConcurrentHashMap<Integer, Particle> getParticles() {
        return this.particles;
    }

    public static void setThreadPool(final ExecutorService tp) {
        threadPool = tp;
    }

    public byte getBGM() {
        return -1;
    }

    public void setMapID(final int i) {
        this.mapID = i;
    }

    public int getMapID() {
        return this.mapID;
    }

    public abstract void loadAssets() throws Exception;

    public abstract void prerender(final Graphics2D g);

    public void update() {
        final long now = System.nanoTime(); // Get time now
        if (now - this.lastUpdateTime >= Globals.LOGIC_UPDATE) {
            updateParticles();
            this.lastUpdateTime = now;
        }
    }

    public void updateParticles() {
        for (final Map.Entry<Integer, Particle> pEntry : this.particles.entrySet()) {
            threadPool.execute(pEntry.getValue());
        }
        Iterator<Map.Entry<Integer, Particle>> particlesIter = this.particles.entrySet().iterator();
        while (particlesIter.hasNext()) {
            Map.Entry<Integer, Particle> particle = particlesIter.next();
            try {
                particle.getValue().join();
                if (particle.getValue().isExpired()) {
                    particlesIter.remove();
                }
            } catch (final InterruptedException ex) {
            }
        }
    }

    public void draw(final Graphics2D g) {
        for (final Map.Entry<Integer, Particle> pEntry : particles.entrySet()) {
            pEntry.getValue().draw(g);
        }
    }

    public void drawBg(final Graphics2D g, final int x, final int y) {
        double relativeX = 1D * (x - this.mapXOrigin) / this.mapWidth,
                relativeY = 1D * (y - this.mapYOrigin) / this.mapHeight;
        final AffineTransform resetForm = g.getTransform();
        double scale = 1 + PARALLAX_FACTOR;

        g.translate(-relativeX * (PARALLAX_FACTOR * 1280), -relativeY * (PARALLAX_FACTOR * 720));
        g.scale(scale, scale);
        g.drawImage(this.bg, 0, 0, 1280, 720, null);
        g.setTransform(resetForm);
    }
}
