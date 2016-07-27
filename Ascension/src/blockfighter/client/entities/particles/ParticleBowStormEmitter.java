package blockfighter.client.entities.particles;

import blockfighter.client.Globals;
import static blockfighter.client.entities.particles.Particle.logic;

public class ParticleBowStormEmitter extends Particle {

    public ParticleBowStormEmitter(final int x, final int y, final byte f) {
        super(x, y, f);
        this.frame = 0;
        this.duration = 5000;
    }

    @Override
    public void update() {
        super.update();
        if (Globals.nsToMs(logic.getTime() - this.particleStartTime) <= 4800) {
            final ParticleBowStormArrow b = new ParticleBowStormArrow(this.x, this.y, this.facing);
            logic.getScreen().addParticle(b);
        }

    }
}
