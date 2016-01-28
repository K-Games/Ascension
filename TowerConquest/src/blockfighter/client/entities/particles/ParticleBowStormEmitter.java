package blockfighter.client.entities.particles;

import blockfighter.client.Globals;
import blockfighter.client.screen.ScreenIngame;

public class ParticleBowStormEmitter extends Particle {

    public ParticleBowStormEmitter(final int k, final int x, final int y, final byte f) {
        super(k, x, y, f);
        this.frame = 0;
        this.frameDuration = 25;
        this.duration = 5000;
    }

    @Override
    public void update() {
        super.update();
        this.frameDuration -= Globals.LOGIC_UPDATE / 1000000;
        if (this.duration > 200) {
            final ParticleBowStormArrow b = new ParticleBowStormArrow(((ScreenIngame) logic.getScreen()).getNextParticleKey(), this.x,
                    this.y, this.facing);
            ((ScreenIngame) logic.getScreen()).addParticle(b);
        }

    }
}
