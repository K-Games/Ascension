package blockfighter.client.screen;

import blockfighter.client.Globals;
import blockfighter.client.entities.particles.Particle;
import blockfighter.client.entities.particles.ParticleMenuSmoke;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author Ken
 */
public abstract class ScreenMenu extends Screen {

    protected double lastUpdateTime = System.nanoTime();
    protected ConcurrentHashMap<Integer, Particle> particles = new ConcurrentHashMap<>(20);
    
    public ScreenMenu(){
        particles.put(0, new ParticleMenuSmoke(null, 0, 0, 0, 0));
        particles.put(1, new ParticleMenuSmoke(null, 1, 1280, 0, 0));
    }
    
    @Override
    public ConcurrentHashMap<Integer, Particle> getParticles() {
        return particles;
    }

    @Override
    public void update() {
        double now = System.nanoTime(); //Get time now
        if (now - lastUpdateTime >= Globals.LOGIC_UPDATE) {
            updateParticles(particles);
            lastUpdateTime = now;
        }
        while (now - lastUpdateTime < Globals.LOGIC_UPDATE) {
            Thread.yield();
            now = System.nanoTime();
        }
    }
}
