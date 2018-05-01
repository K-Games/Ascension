package blockfighter.client.entities.particles.skills.bow;

import blockfighter.client.entities.particles.Particle;
import blockfighter.shared.Globals;
import java.awt.Graphics2D;

public class ParticleBowRapid2 extends Particle {

    public ParticleBowRapid2(final int x, final int y, final byte f) {
        super(x, y, f);
        this.frame = 0;
        this.frameDuration = 50;
        this.duration = 150;
        this.particleData = Globals.Particles.BOW_RAPID2;
    }

    @Override
    public void draw(final Graphics2D g) {
        draw(g, -60, 45);
    }
}
