package blockfighter.client.entities.particles.skills.passive;

import blockfighter.client.Core;
import blockfighter.client.entities.particles.Particle;
import blockfighter.shared.Globals;
import java.awt.Graphics2D;

public class ParticlePassiveResist extends Particle {

    public ParticlePassiveResist(final int x, final int y) {
        super(x, y, Globals.RIGHT);
        this.frame = 0;
        this.frameDuration = 25;
        this.duration = 300;
        this.particleData = Globals.Particles.PASSIVE_RESIST;
    }

    @Override
    public void update() {
        super.update();

        if (Globals.nsToMs(Core.getLogicModule().getTime() - this.lastFrameTime) >= this.frameDuration) {
            if (Globals.Particles.PASSIVE_RESIST.getSprites() != null && this.frame < Globals.Particles.PASSIVE_RESIST.getSprites().length - 1) {
                this.frame++;
            }
            this.lastFrameTime = Core.getLogicModule().getTime();
        }
    }

    @Override
    public void draw(final Graphics2D g) {
        draw(g, 47, 50, false);
    }
}
