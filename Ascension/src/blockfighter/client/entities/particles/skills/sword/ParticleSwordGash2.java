package blockfighter.client.entities.particles.skills.sword;

import blockfighter.client.entities.particles.Particle;
import blockfighter.shared.Globals;
import java.awt.Graphics2D;

public class ParticleSwordGash2 extends Particle {

    public ParticleSwordGash2(final int x, final int y, final byte f) {
        super(x, y, f);
        this.frame = 0;
        this.frameDuration = 50;
        this.duration = 200;
        this.particleData = Globals.Particles.SWORD_GASH2;
    }

    @Override
    public void draw(final Graphics2D g) {
        draw(g, -20, 20);
    }
}
