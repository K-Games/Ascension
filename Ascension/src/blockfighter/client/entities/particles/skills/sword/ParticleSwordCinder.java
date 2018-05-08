package blockfighter.client.entities.particles.skills.sword;

import blockfighter.client.entities.particles.Particle;
import blockfighter.shared.Globals;
import java.awt.Graphics2D;

public class ParticleSwordCinder extends Particle {

    public ParticleSwordCinder(final int x, final int y, final byte f) {
        super(x, y, f);
        this.frame = 0;
        this.frameDuration = 50;
        this.duration = 400;
        this.particleData = Globals.Particles.SWORD_CINDER;
    }

    @Override
    public void draw(final Graphics2D g) {
        draw(g, -130, 40);
    }
}
