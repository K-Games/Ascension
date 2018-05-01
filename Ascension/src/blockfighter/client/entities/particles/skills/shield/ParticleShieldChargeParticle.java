package blockfighter.client.entities.particles.skills.shield;

import blockfighter.client.entities.particles.Particle;
import blockfighter.shared.Globals;
import java.awt.Graphics2D;

public class ParticleShieldChargeParticle extends Particle {

    public ParticleShieldChargeParticle(final int x, final int y, final byte f) {
        super(x, y, f);
        this.frame = 0;
        this.frameDuration = 75;
        this.duration = 400;
        this.y += Globals.rng(11) * 12;
        this.particleData = Globals.Particles.SHIELD_CHARGE_PARTICLE;
    }

    @Override
    public void draw(final Graphics2D g) {
        draw(g, -100, -115);
    }
}
