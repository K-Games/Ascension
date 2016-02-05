package blockfighter.client.entities.boss.Lightning;

import blockfighter.client.Globals;
import blockfighter.client.entities.particles.Particle;
import blockfighter.client.screen.ScreenIngame;

public class ParticleAttEmitter extends Particle {

    public ParticleAttEmitter(final int k, final int x, final int y) {
        super(k, x, y, Globals.RIGHT);
        this.frame = 0;
        this.duration = 200;
    }

    @Override
    public void update() {
        super.update();
        if (Globals.nsToMs(logic.getTime() - this.particleStartTime) < this.duration) {
            final ParticleAmbient b = new ParticleAmbient(((ScreenIngame) logic.getScreen()).getNextParticleKey(), this.x,
                    this.y + Globals.rng(200));
            ((ScreenIngame) logic.getScreen()).addParticle(b);
        }
    }
}
