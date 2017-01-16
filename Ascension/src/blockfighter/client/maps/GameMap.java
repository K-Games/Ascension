package blockfighter.client.maps;

import blockfighter.client.AscensionClient;
import blockfighter.client.entities.particles.Particle;
import blockfighter.shared.Globals;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

public abstract class GameMap {

    protected ConcurrentHashMap<Integer, Particle> particles = new ConcurrentHashMap<>(20);
    protected long lastUpdateTime = 0;
    protected int mapHeight, mapWidth, mapXOrigin = 0, mapYOrigin = 0;
    private final static double PARALLAX_FACTOR = 0.35;
    private int mapID = -1;
    BufferedImage bg;

    public ConcurrentHashMap<Integer, Particle> getParticles() {
        return this.particles;
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
        if (now - this.lastUpdateTime >= Globals.CLIENT_LOGIC_UPDATE) {
            updateParticles();
            this.lastUpdateTime = now;
        }
    }

    public void updateParticles() {
        LinkedList<Future<Particle>> futures = new LinkedList<>();
        for (final Map.Entry<Integer, Particle> pEntry : this.particles.entrySet()) {
            futures.add(AscensionClient.SHARED_THREADPOOL.submit(pEntry.getValue()));
        }
        for (Future<Particle> task : futures) {

            try {
                Particle particle = task.get();
                if (particle.isExpired()) {
                    this.particles.remove(particle.getKey());
                }
            } catch (final Exception ex) {
                Globals.logError(ex.toString(), ex);
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
