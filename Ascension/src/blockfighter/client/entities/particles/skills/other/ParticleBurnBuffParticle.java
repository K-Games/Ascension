package blockfighter.client.entities.particles.skills.other;

import blockfighter.client.Core;
import blockfighter.client.entities.particles.Particle;
import blockfighter.shared.Globals;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class ParticleBurnBuffParticle extends Particle {

    public ParticleBurnBuffParticle(final int x, final int y, final byte f) {
        super(x, y, f);
        this.x += Globals.rng(50) - 35;
        this.y -= Globals.rng(80) + 20;
        this.frame = 0;
        this.frameDuration = 25;
        this.duration = 500;
    }

    @Override
    public void update() {
        super.update();

        this.y -= 3;
        if (Globals.nsToMs(Core.getLogicModule().getTime() - this.lastFrameTime) >= this.frameDuration) {
            if (Globals.Particles.BURN_BUFF_PARTICLE.getSprite() != null && this.frame < Globals.Particles.BURN_BUFF_PARTICLE.getSprite().length - 1) {
                this.frame++;
            }
            this.lastFrameTime = Core.getLogicModule().getTime();
        }
    }

    @Override
    public void draw(final Graphics2D g) {
        if (Globals.Particles.BURN_BUFF_PARTICLE.getSprite() == null) {
            return;
        }
        if (this.frame >= Globals.Particles.BURN_BUFF_PARTICLE.getSprite().length) {
            return;
        }
        final BufferedImage sprite = Globals.Particles.BURN_BUFF_PARTICLE.getSprite()[this.frame];
        g.drawImage(sprite, this.x, this.y, null);
    }
}
