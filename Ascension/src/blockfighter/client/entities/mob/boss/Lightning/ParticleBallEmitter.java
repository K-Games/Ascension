package blockfighter.client.entities.mob.boss.Lightning;

import blockfighter.client.Globals;
import blockfighter.client.entities.particles.Particle;

public class ParticleBallEmitter extends Particle {

    public ParticleBallEmitter(final int x, final int y) {
        super(x, y, Globals.RIGHT);
        this.frame = 0;
        this.duration = 200;
    }

    @Override
    public void update() {
        super.update();
        if (!isExpired()) {
            for (int i = 0; i < 20; i++) {
                final ParticleAmbient b = new ParticleAmbient(this.x + Globals.rng(1950), this.y,
                        true);
                logic.getScreen().addParticle(b);
            }
        }
    }
}