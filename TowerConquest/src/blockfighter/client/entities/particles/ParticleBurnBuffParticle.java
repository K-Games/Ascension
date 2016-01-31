package blockfighter.client.entities.particles;

import blockfighter.client.Globals;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class ParticleBurnBuffParticle extends Particle {

    public ParticleBurnBuffParticle(final int k, final int x, final int y, final byte f) {
        super(k, x, y, f);
        this.x += Globals.rng(50) - 35;
        this.y -= Globals.rng(100) + 20;
        this.frame = 0;
        this.frameDuration = 25;
        this.duration = 500;
    }

    @Override
    public void update() {
        super.update();
        this.frameDuration -= Globals.LOGIC_UPDATE / 1000000;
        this.y -= 3;
        if (this.frameDuration <= 0) {
            this.frameDuration = 25;
            if (this.frame < PARTICLE_SPRITE[Globals.PARTICLE_BURN].length - 1) {
                this.frame++;
            }
        }
    }

    @Override
    public void draw(final Graphics2D g) {
        if (PARTICLE_SPRITE[Globals.PARTICLE_BURN] == null) {
            return;
        }
        if (this.frame >= PARTICLE_SPRITE[Globals.PARTICLE_BURN].length) {
            return;
        }
        final BufferedImage sprite = PARTICLE_SPRITE[Globals.PARTICLE_BURN][this.frame];
        g.drawImage(sprite, this.x, this.y, null);
    }
}
