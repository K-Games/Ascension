package blockfighter.client.entities.particles.skills.passive;

import blockfighter.client.entities.particles.Particle;
import blockfighter.shared.Globals;
import java.awt.Graphics2D;

public class ParticlePassiveBarrier extends Particle {

    public ParticlePassiveBarrier(final int x, final int y) {
        super(x, y, Globals.RIGHT);
        this.frame = 0;
        this.frameDuration = 25;
        this.duration = 300;
        this.particleData = Globals.Particles.PASSIVE_BARRIER;
    }

    @Override
    public void draw(final Graphics2D g) {
        draw(g, 62, 66, false);
    }
}
