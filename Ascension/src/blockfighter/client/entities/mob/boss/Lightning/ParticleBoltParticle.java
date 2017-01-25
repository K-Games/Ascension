package blockfighter.client.entities.mob.boss.Lightning;

import blockfighter.client.Core;
import blockfighter.client.entities.particles.Particle;
import blockfighter.shared.Globals;
import java.awt.Graphics2D;

public class ParticleBoltParticle extends Particle {

    double dX, dY, speedX, speedY;

    public ParticleBoltParticle(final int x, final int y) {
        super(x, y, Globals.RIGHT);
        this.dX = x;
        this.dY = y;
        this.speedX = (Globals.rng(20) - 10) * 1.5;
        this.speedY = (Globals.rng(10) + 10) * 2;
        this.frame = 0;
        this.frameDuration = 50;
        this.duration = 300;
    }

    @Override
    public void update() {
        super.update();
        this.dX += this.speedX;
        this.dY -= this.speedY;
        this.x = (int) this.dX;
        this.y = (int) this.dY;
        if (Globals.nsToMs(Core.getLogicModule().getTime() - this.lastFrameTime) >= this.frameDuration) {

            this.lastFrameTime = Core.getLogicModule().getTime();
        }
    }

    @Override
    public void draw(final Graphics2D g) {

    }
}
