package blockfighter.client.entities.particles.skills.bow;

import blockfighter.client.entities.particles.Particle;
import blockfighter.shared.Globals;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

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
    }

    @Override
    public void update() {
        super.update();
        this.x += (this.facing == Globals.RIGHT) ? 20 : -20;
        if (Globals.nsToMs(logic.getTime() - this.lastFrameTime) >= this.frameDuration) {
            if (Globals.Particles.BOW_POWER_PARTICLE.getSprite() != null && this.frame < Globals.Particles.BOW_POWER_PARTICLE.getSprite().length - 1) {
                this.frame++;
            }
            this.lastFrameTime = logic.getTime();
        }
    }

    @Override
    public void draw(final Graphics2D g) {
        if (Globals.Particles.BOW_POWER_PARTICLE.getSprite() == null) {
            return;
        }
        if (this.frame >= Globals.Particles.BOW_POWER_PARTICLE.getSprite().length) {
            return;
        }
        final BufferedImage sprite = Globals.Particles.BOW_POWER_PARTICLE.getSprite()[this.frame];
        g.drawImage(sprite, this.x, this.y, null);
    }
}
