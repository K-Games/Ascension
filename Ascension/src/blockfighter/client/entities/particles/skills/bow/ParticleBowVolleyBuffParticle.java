package blockfighter.client.entities.particles.skills.bow;

import blockfighter.client.Core;
import blockfighter.client.entities.particles.Particle;
import blockfighter.shared.Globals;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class ParticleBowVolleyBuffParticle extends Particle {

    double xDouble, xSpeed;

    public ParticleBowVolleyBuffParticle(final int x, final int y, final byte f) {
        super(x, y, f);
        this.x += Globals.rng(50) - 35;
        this.y -= Globals.rng(40) + 29;
        this.xDouble = this.x;
        this.xSpeed = Globals.rng(10) * .75 - 3.75;
        this.frame = 0;
        this.frameDuration = 50;
        this.duration = 200;
    }

    @Override
    public void update() {
        super.update();

        this.xDouble += this.xSpeed;
        this.x = (int) this.xDouble;
        this.y -= 9;
        if (Globals.nsToMs(Core.getLogicModule().getTime() - this.lastFrameTime) >= this.frameDuration) {
            if (Globals.Particles.BOW_VOLLEY_BUFF_PARTICLE.getSprite() != null && this.frame < Globals.Particles.BOW_VOLLEY_BUFF_PARTICLE.getSprite().length - 1) {
                this.frame++;
            }
            this.lastFrameTime = Core.getLogicModule().getTime();
        }
    }

    @Override
    public void draw(final Graphics2D g) {
        if (Globals.Particles.BOW_VOLLEY_BUFF_PARTICLE.getSprite() == null) {
            return;
        }
        if (this.frame >= Globals.Particles.BOW_VOLLEY_BUFF_PARTICLE.getSprite().length) {
            return;
        }
        final BufferedImage sprite = Globals.Particles.BOW_VOLLEY_BUFF_PARTICLE.getSprite()[this.frame];
        g.drawImage(sprite, this.x, this.y, null);
    }
}
