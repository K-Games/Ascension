package blockfighter.client.entities.particles.skills.utility;

import blockfighter.client.entities.particles.Particle;
import blockfighter.shared.Globals;
import java.awt.Graphics2D;

public class ParticleUtilityDashBuffParticle extends Particle {

    public ParticleUtilityDashBuffParticle(final int x, final int y, final byte f) {
        super(x, y, f);
        this.x += Globals.rng(50) - 35;
        this.y -= Globals.rng(40);
        this.frame = 0;
        this.frameDuration = 0;
        this.duration = 250;
        this.particleData = Globals.Particles.UTILITY_DASH_BUFF_PARTICLE;
    }

    @Override
    public void update() {
        this.y -= 9;
    }

    @Override
    public void draw(final Graphics2D g) {
        draw(g, 0, 0, false);
    }
}
