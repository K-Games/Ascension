package blockfighter.client.entities.particles.skills.utility;

import blockfighter.client.entities.particles.Particle;
import blockfighter.shared.Globals;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class ParticleUtilityDashBuffParticle extends Particle {

    public ParticleUtilityDashBuffParticle(final int x, final int y, final byte f) {
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
        if (Globals.Particles.UTILITY_DASH_BUFF_PARTICLE.getSprite() == null) {
            return;
        }
        if (this.frame >= Globals.Particles.UTILITY_DASH_BUFF_PARTICLE.getSprite().length) {
            return;
        }
        final BufferedImage sprite = Globals.Particles.UTILITY_DASH_BUFF_PARTICLE.getSprite()[this.frame];
        g.drawImage(sprite, this.x, this.y, null);
    }
}
