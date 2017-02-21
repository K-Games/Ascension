package blockfighter.client.maps;

import blockfighter.client.Core;
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

    protected Globals.GameMaps gameMap;
    protected ConcurrentHashMap<Integer, Particle> particles = new ConcurrentHashMap<>(20);
    protected long lastUpdateTime = 0;
    protected int mapHeight, mapWidth, mapXOrigin = 0, mapYOrigin = 0;
    private final static double PARALLAX_FACTOR = 0.35;
    BufferedImage[] bg;

    public GameMap(final Globals.GameMaps gameMap) {
        this.gameMap = gameMap;
    }

    public ConcurrentHashMap<Integer, Particle> getParticles() {
        return this.particles;
    }

    public byte getBgmCode() {
        return -1;
    }

    public abstract void loadAssets() throws Exception;

    public abstract void prerender(final Graphics2D g);

    public abstract void unloadAssets();

    public Globals.GameMaps getGameMap() {
        return this.gameMap;
    }

    public void update() {
        final long now = Core.getLogicModule().getTime(); // Get time now
        if (now - this.lastUpdateTime >= Globals.CLIENT_LOGIC_UPDATE) {
            updateParticles();
            this.lastUpdateTime = now;
        }
    }

    public void updateParticles() {
        LinkedList<Future<Particle>> futures = new LinkedList<>();
        for (final Map.Entry<Integer, Particle> pEntry : this.particles.entrySet()) {
            futures.add(Core.SHARED_THREADPOOL.submit(pEntry.getValue()));
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
        for (int i = 0; i < this.bg.length; i++) {
            final AffineTransform resetForm = g.getTransform();
            double scale = 1 + PARALLAX_FACTOR * Math.pow(1.35, i);

            g.translate(-relativeX * (PARALLAX_FACTOR * Math.pow(1.35, i) * 1280), -relativeY * (PARALLAX_FACTOR * Math.pow(1.35, i) * 720));
            g.scale(scale, scale);
            g.drawImage(this.bg[i], 0, 0, 1280, 720, null);
            g.setTransform(resetForm);
        }
    }
}
