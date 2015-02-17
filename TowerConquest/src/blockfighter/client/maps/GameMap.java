package blockfighter.client.maps;

import blockfighter.client.Globals;
import blockfighter.client.entities.particles.Particle;
import blockfighter.client.screen.ScreenInventory;
import java.awt.Graphics2D;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ken Kwan
 */
public abstract class GameMap {

    protected static ConcurrentHashMap<Integer, Particle> particles = new ConcurrentHashMap<>(20);
    protected double lastUpdateTime = System.nanoTime();
    protected static ExecutorService threadPool;
    private int mapID = -1;

    public ConcurrentHashMap<Integer, Particle> getParticles() {
        return particles;
    }

    public static void setThreadPool(ExecutorService tp) {
        threadPool = tp;
    }

    public void setMapID(int i) {
        mapID = i;
    }

    public int getMapID() {
        return mapID;
    }

    public abstract void loadAssets() throws Exception;

    public void update() {
        double now = System.nanoTime(); //Get time now
        if (now - lastUpdateTime >= Globals.LOGIC_UPDATE) {
            updateParticles(particles);
            lastUpdateTime = now;
        }
        try {
            Thread.sleep(0, 1);
        } catch (InterruptedException ex) {
            Logger.getLogger(ScreenInventory.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void updateParticles(ConcurrentHashMap<Integer, Particle> particles) {
        for (Map.Entry<Integer, Particle> pEntry : particles.entrySet()) {
            threadPool.execute(pEntry.getValue());
        }
        LinkedList<Integer> remove = new LinkedList<>();
        for (Map.Entry<Integer, Particle> pEntry : particles.entrySet()) {
            try {
                pEntry.getValue().join();
                if (pEntry.getValue().isExpired()) {
                    remove.add(pEntry.getKey());
                }
            } catch (InterruptedException ex) {
            }
        }
        removeParticles(particles, remove);
    }

    private void removeParticles(ConcurrentHashMap<Integer, Particle> particles, LinkedList<Integer> remove) {
        while (!remove.isEmpty()) {
            particles.remove(remove.pop());
        }
    }

    public void draw(Graphics2D g) {
        for (Map.Entry<Integer, Particle> pEntry : particles.entrySet()) {
            pEntry.getValue().draw(g);
        }
    }
}
