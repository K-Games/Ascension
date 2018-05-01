package blockfighter.client.entities.particles.skills.bow;

import blockfighter.client.entities.particles.Particle;
import blockfighter.shared.Globals;
import java.awt.Graphics2D;

public class ParticleBowPowerParticle extends Particle {

    public ParticleBowPowerParticle(final int x, final int y, final byte f) {
        super(x, y, f);
        if (this.facing == Globals.RIGHT) {
            this.x += Globals.rng(450);
        } else {
            this.x -= Globals.rng(450) - 25;
        }
        this.y += Globals.rng(80) + 25;
        this.frame = 0;
        this.frameDuration = 25;
        this.duration = 250;
        this.particleData = Globals.Particles.BOW_POWER_PARTICLE;
    }

    @Override
    public void update() {
        super.update();
        this.x += (this.facing == Globals.RIGHT) ? 20 : -20;
    }

    @Override
    public void draw(final Graphics2D g) {
        draw(g, 0, 0, false);
    }
}
