package blockfighter.client.entities.boss.Lightning;

import blockfighter.client.Globals;
import blockfighter.client.entities.particles.Particle;
import blockfighter.client.screen.ScreenIngame;

public class ParticleBallEmitter extends Particle {

    public ParticleBallEmitter(int k, int x, int y) {
        super(k, x, y, Globals.RIGHT);
        frame = 0;
        duration = 200;
    }

    @Override
    public void update() {
        super.update();
        if (duration > 0) {
            for (int i = 0; i < 20; i++) {
                ParticleAmbient b = new ParticleAmbient(((ScreenIngame) logic.getScreen()).getNextParticleKey(), x + Globals.rng(1950), y, true);
                ((ScreenIngame) logic.getScreen()).addParticle(b);
            }
        }
    }
}
