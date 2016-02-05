package blockfighter.client.entities.boss.Lightning;

import blockfighter.client.Globals;
import blockfighter.client.entities.particles.Particle;
import blockfighter.client.screen.ScreenIngame;

public class ParticleBallEmitter extends Particle {

    public ParticleBallEmitter(final int k, final int x, final int y) {
        super(k, x, y, Globals.RIGHT);
        this.frame = 0;
        this.duration = 200;
    }

    @Override
    public void update() {
        super.update();
        if (!isExpired()) {
            for (int i = 0; i < 20; i++) {
                final ParticleAmbient b = new ParticleAmbient(((ScreenIngame) logic.getScreen()).getNextParticleKey(),
                        this.x + Globals.rng(1950), this.y,
                        true);
                ((ScreenIngame) logic.getScreen()).addParticle(b);
            }
        }
    }
}
