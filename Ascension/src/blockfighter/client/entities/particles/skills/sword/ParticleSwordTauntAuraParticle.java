package blockfighter.client.entities.particles.skills.sword;

import blockfighter.client.entities.particles.Particle;
import blockfighter.shared.Globals;
import java.awt.Graphics2D;

public class ParticleSwordTauntAuraParticle extends Particle {

    public ParticleSwordTauntAuraParticle(final int x, final int y, final byte f) {
        super(x, y, f);
        this.x += Globals.rng(50) - 35;
        this.y -= Globals.rng(40);
        this.frame = 0;
        this.frameDuration = 50;
        this.duration = 500;
        this.particleData = Globals.Particles.SWORD_TAUNT_AURA_PARTICLE;
    }

    @Override
    public void update() {
        super.update();
        this.y -= 6;
    }

    @Override
    public void draw(final Graphics2D g) {
        draw(g, 0, 0, false);
    }
}
