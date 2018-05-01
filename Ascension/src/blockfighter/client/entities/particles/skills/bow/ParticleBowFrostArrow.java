package blockfighter.client.entities.particles.skills.bow;

import blockfighter.client.entities.particles.Particle;
import blockfighter.shared.Globals;
import java.awt.Graphics2D;

public class ParticleBowFrostArrow extends Particle {

    public ParticleBowFrostArrow(final int x, final int y, final byte f) {
        super(x, y, f);
        this.frame = 0;
        this.frameDuration = 50;
        this.duration = 700;
        this.particleData = Globals.Particles.BOW_FROSTARROW;
    }

    @Override
    public void draw(final Graphics2D g) {
        draw(g, 0, 20);
    }
}
