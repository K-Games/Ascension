package blockfighter.client.entities.particles.skills.sword;

import blockfighter.client.entities.particles.Particle;
import blockfighter.shared.Globals;
import java.awt.Graphics2D;

public class ParticleSwordSlash3 extends Particle {

    public ParticleSwordSlash3(final int x, final int y, final byte f) {
        super(x, y, f);
        this.frame = 0;
        this.frameDuration = 50;
        this.duration = 250;
        this.particleData = Globals.Particles.SWORD_SLASH3;
    }

    @Override
    public void draw(final Graphics2D g) {
        draw(g, -60, 30);
    }
}
