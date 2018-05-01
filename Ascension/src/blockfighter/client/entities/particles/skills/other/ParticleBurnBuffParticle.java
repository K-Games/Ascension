package blockfighter.client.entities.particles.skills.other;

import blockfighter.client.Core;
import blockfighter.client.entities.particles.Particle;
import blockfighter.shared.Globals;
import java.awt.Graphics2D;

public class ParticleBurnBuffParticle extends Particle {

    public ParticleBurnBuffParticle(final int x, final int y, final byte f) {
        super(x, y, f);
        this.x += Globals.rng(50) - 35;
        this.y -= Globals.rng(80);
        this.frame = 0;
        this.frameDuration = 25;
        this.duration = 500;
        this.particleData = Globals.Particles.BURN_BUFF_PARTICLE;
    }

    @Override
    public void update() {
        if (Globals.nsToMs(Core.getLogicModule().getTime() - this.lastFrameTime) >= this.frameDuration) {
            this.y -= 3;
            super.update();
            this.lastFrameTime = Core.getLogicModule().getTime();
        }
    }

    @Override
    public void draw(final Graphics2D g) {
        draw(g, 0, 0, false);
    }
}
