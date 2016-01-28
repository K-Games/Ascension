package blockfighter.client.entities.particles;

import blockfighter.client.Globals;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class ParticleShieldDashBuffParticle extends Particle {

    public ParticleShieldDashBuffParticle(final int k, final int x, final int y, final byte f) {
        super(k, x, y, f);
        this.x += Globals.rng(100) - 60;
        this.y -= Globals.rng(40) + 64;
        this.frame = 0;
        this.frameDuration = 50;
        this.duration = 300;
    }

    @Override
    public void update() {
        super.update();
        this.frameDuration -= Globals.LOGIC_UPDATE / 1000000;
        this.y -= 9;
        if (this.frameDuration <= 0) {
            this.frameDuration = 50;
            if (this.frame < PARTICLE_SPRITE[Globals.PARTICLE_SHIELD_DASHBUFF].length - 1) {
                this.frame++;
            }
        }
    }

    @Override
    public void draw(final Graphics2D g) {
        if (PARTICLE_SPRITE[Globals.PARTICLE_SHIELD_DASHBUFF] == null) {
            return;
        }
        if (this.frame >= PARTICLE_SPRITE[Globals.PARTICLE_SHIELD_DASHBUFF].length) {
            return;
        }
        final BufferedImage sprite = PARTICLE_SPRITE[Globals.PARTICLE_SHIELD_DASHBUFF][this.frame];
        g.drawImage(sprite, this.x, this.y, null);
    }
}
