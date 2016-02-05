package blockfighter.client.entities.particles;

import blockfighter.client.Globals;
import static blockfighter.client.entities.particles.Particle.logic;
import blockfighter.client.screen.ScreenIngame;

public class ParticleBowStormEmitter extends Particle {

    public ParticleBowStormEmitter(final int k, final int x, final int y, final byte f) {
        super(k, x, y, f);
        this.frame = 0;
        this.duration = 5000;
    }

    @Override
    public void update() {
        super.update();
        if (Globals.nsToMs(logic.getTime() - this.particleStartTime) <= 4800) {
            final ParticleBowStormArrow b = new ParticleBowStormArrow(((ScreenIngame) logic.getScreen()).getNextParticleKey(), this.x,
                    this.y, this.facing);
            ((ScreenIngame) logic.getScreen()).addParticle(b);
        }

    }
}
