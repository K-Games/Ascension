package blockfighter.client.entities.particles.skills.other;

import blockfighter.client.entities.particles.Particle;
import blockfighter.shared.Globals;
import java.awt.Graphics2D;

public class ParticleHit extends Particle {

    public ParticleHit(final int x, final int y, final byte f) {
        super(x, y, f);
        this.frame = 0;
        this.frameDuration = 50;
        this.duration = Globals.Particles.HIT.getNumFrames() * this.frameDuration;
        this.particleData = Globals.Particles.HIT;
    }

    @Override
    public void draw(final Graphics2D g) {
        draw(g, -97, 52);
    }
}
