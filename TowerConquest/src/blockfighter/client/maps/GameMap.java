package blockfighter.client.maps;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

import blockfighter.client.Globals;
import blockfighter.client.entities.particles.Particle;

/**
 *
 * @author Ken Kwan
 */
public abstract class GameMap {

	protected static ConcurrentHashMap<Integer, Particle> particles = new ConcurrentHashMap<>(20);
	protected double lastUpdateTime = System.nanoTime();
	protected static ExecutorService threadPool;
	private int mapID = -1;
	BufferedImage bg;

	public ConcurrentHashMap<Integer, Particle> getParticles() {
		return particles;
	}

	public static void setThreadPool(final ExecutorService tp) {
		threadPool = tp;
	}

	public void setMapID(final int i) {
		this.mapID = i;
	}

	public int getMapID() {
		return this.mapID;
	}

	public abstract void loadAssets() throws Exception;

	public void update() {
		final double now = System.nanoTime(); // Get time now
		if (now - this.lastUpdateTime >= Globals.LOGIC_UPDATE) {
			updateParticles();
			this.lastUpdateTime = now;
		}
	}

	public void updateParticles() {
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
		removeParticles(remove);
	}

	private void removeParticles(final LinkedList<Integer> remove) {
		while (!remove.isEmpty()) {
			particles.remove(remove.pop());
		}
	}

	public void draw(final Graphics2D g) {
		for (final Map.Entry<Integer, Particle> pEntry : particles.entrySet()) {
			pEntry.getValue().draw(g);
		}
	}

	public void drawBg(final Graphics2D g) {
		g.drawImage(this.bg, 0, 0, 1280, 720, null);
	}
}
