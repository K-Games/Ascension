package blockfighter.client.entities.particles.skills.bow;

import blockfighter.client.entities.particles.Particle;
import blockfighter.shared.Globals;
import java.awt.Graphics2D;

public class ParticleBowVolleyBuffParticle extends Particle {

    double xDouble, xSpeed;

    public ParticleBowVolleyBuffParticle(final int x, final int y, final byte f) {
        super(x, y, f);
        this.x += Globals.rng(50) - 25;
        this.y -= Globals.rng(40);
        this.xDouble = this.x;
        this.xSpeed = Globals.rng(10) * .75 - 3.75;
        this.frame = 0;
        this.frameDuration = 50;
        this.duration = 200;
        this.particleData = Globals.Particles.BOW_VOLLEY_BUFF_PARTICLE;
    }

    @Override
    public void update() {
        super.update();
        this.xDouble += this.xSpeed;
        this.x = (int) this.xDouble;
        this.y -= 9;
    }

    @Override
    public void draw(final Graphics2D g) {
        draw(g, 0, 0, false);
    }
}
