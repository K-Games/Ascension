package blockfighter.client.entities.particles.skills.shield;

import blockfighter.client.Core;
import blockfighter.client.entities.particles.Particle;
import blockfighter.shared.Globals;
import java.awt.Graphics2D;

public class ParticleShieldReflectHit extends Particle {

    private double speedX, speedY;

    public ParticleShieldReflectHit(final int x, final int y) {
        this.duration = 0;
        for (int i = 0; i < 20; i++) {
            Core.getLogicModule().getScreen().addParticle(new ParticleShieldReflectHit(x, y, i));
        }
    }

    public ParticleShieldReflectHit(final int x, final int y, final int index) {
        super(x, y);
        this.frame = 0;
        this.frameDuration = 25;
        this.duration = 300;
        this.particleData = Globals.Particles.SHIELD_REFLECT_HIT;
        double targetX = this.x + 300f * Math.cos(2 * Math.PI * index / 20f);
        double targetY = this.y + 300f * Math.sin(2 * Math.PI * index / 20f);
        double numberOfTicks = this.duration / this.frameDuration * 1f;
        this.speedX = (targetX - this.x) / numberOfTicks;
        this.speedY = (targetY - this.y) / numberOfTicks;
    }

    @Override
    public void update() {
        if (Globals.nsToMs(Core.getLogicModule().getTime() - this.lastFrameTime) >= this.frameDuration) {
            this.x += this.speedX;
            this.y += this.speedY;
            super.update();
        }
    }

    @Override
    public void draw(final Graphics2D g) {
        draw(g, -25, -25, false);
    }
}
