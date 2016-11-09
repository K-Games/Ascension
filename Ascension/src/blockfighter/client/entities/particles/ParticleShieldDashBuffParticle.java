package blockfighter.client.entities.particles;

import blockfighter.shared.Globals;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class ParticleShieldDashBuffParticle extends Particle {

    public ParticleShieldDashBuffParticle(final int x, final int y, final byte f) {
        super(x, y, f);
        this.x += Globals.rng(50) - 35;
        this.y -= Globals.rng(40) + 30;
        this.frame = 0;
        this.frameDuration = 0;
        this.duration = 250;
    }

    @Override
    public void update() {
        super.update();
        this.y -= 9;
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
