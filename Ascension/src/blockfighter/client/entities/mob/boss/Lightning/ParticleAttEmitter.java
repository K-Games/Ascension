package blockfighter.client.entities.mob.boss.Lightning;

import blockfighter.client.Globals;
import blockfighter.client.entities.particles.Particle;

public class ParticleAttEmitter extends Particle {

    public ParticleAttEmitter(final int x, final int y) {
        super(x, y, Globals.RIGHT);
        this.frame = 0;
        this.duration = 200;
    }

    @Override
    public void update() {
        super.update();
        if (!isExpired()) {
            final ParticleAmbient b = new ParticleAmbient(this.x, this.y + Globals.rng(200));
            logic.getScreen().addParticle(b);
        }
    }
}
