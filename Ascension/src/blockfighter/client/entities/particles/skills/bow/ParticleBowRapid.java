package blockfighter.client.entities.particles.skills.bow;

import blockfighter.client.Core;
import blockfighter.client.entities.particles.Particle;
import blockfighter.shared.Globals;
import java.awt.Graphics2D;

public class ParticleBowRapid extends Particle {

    public ParticleBowRapid(final int x, final int y, final byte f) {
        super(x, y, f);
        this.frame = 0;
        this.frameDuration = 50;
        this.duration = 300;
        this.particleData = Globals.Particles.BOW_RAPID;
        Core.getLogicModule().getScreen().addParticle(new ParticleBowRapid2(x, y, f));
    }

    @Override
    public void draw(final Graphics2D g) {
        draw(g, 40, 20);
    }
}
