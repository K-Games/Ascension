package blockfighter.client.entities.particles.skills.bow;

import blockfighter.client.Core;
import blockfighter.client.entities.particles.Particle;
import blockfighter.shared.Globals;
import java.awt.Graphics2D;

public class ParticleBowArc extends Particle {

    public ParticleBowArc(final int x, final int y, final byte f) {
        super(x, y, f);
        this.frame = 0;
        this.frameDuration = 100;
        this.duration = 400;
        this.particleData = Globals.Particles.BOW_ARC;
    }

    @Override
    public void update() {
        if (Globals.nsToMs(Core.getLogicModule().getTime() - this.lastFrameTime) >= this.frameDuration) {
            super.update();
            this.frameDuration = 25;
        }
    }

    @Override
    public void draw(final Graphics2D g) {
        draw(g, 35, -17);
    }
}
