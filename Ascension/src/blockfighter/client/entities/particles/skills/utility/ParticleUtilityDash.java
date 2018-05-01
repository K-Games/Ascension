package blockfighter.client.entities.particles.skills.utility;

import blockfighter.client.entities.particles.Particle;
import blockfighter.shared.Globals;
import java.awt.Graphics2D;

public class ParticleUtilityDash extends Particle {

    public ParticleUtilityDash(final int x, final int y, final byte f) {
        super(x, y, f);
        this.frame = 0;
        this.frameDuration = 25;
        this.duration = 400;
        this.particleData = Globals.Particles.UTILITY_DASH;
    }

    @Override
    public void draw(final Graphics2D g) {
        draw(g, -220, 80);
    }
}
