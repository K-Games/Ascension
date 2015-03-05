package blockfighter.client.entities.boss.Lightning;

import blockfighter.client.Globals;
import blockfighter.client.entities.particles.Particle;
import blockfighter.client.screen.ScreenIngame;

public class ParticleAttEmitter extends Particle {

    public ParticleAttEmitter(int k, int x, int y) {
        super(k, x, y, Globals.RIGHT);
        frame = 0;
        duration = 200;
    }

    @Override
    public void update() {
        super.update();
        if (duration > 0) {
            ParticleAmbient b = new ParticleAmbient(((ScreenIngame) logic.getScreen()).getNextParticleKey(), x, y + Globals.rng(200));
            ((ScreenIngame) logic.getScreen()).addParticle(b);
        }
    }
}
