package blockfighter.client.entities.particles.skills.bow;

import blockfighter.client.Core;
import blockfighter.client.entities.particles.Particle;
import blockfighter.shared.Globals;
import java.awt.Graphics2D;

public class ParticleBowPower extends Particle {

    public ParticleBowPower(final int x, final int y, final byte f) {
        super(x, y, f);
        this.frame = 0;
        this.frameDuration = 75;
        this.duration = 250;
        this.particleData = Globals.Particles.BOW_POWER;
    }

    @Override
    public void update() {
        super.update();
        for (int i = 0; i < 2; i++) {
            final ParticleBowPowerParticle b = new ParticleBowPowerParticle(this.x, this.y - 150, this.facing);
            Core.getLogicModule().getScreen().addParticle(b);
        }
    }

    @Override
    public void draw(final Graphics2D g) {
        draw(g, -50, 110, 250, 0, true);
    }
}
