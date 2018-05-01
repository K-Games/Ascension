package blockfighter.client.entities.particles.skills.shield;

import blockfighter.client.entities.particles.Particle;
import blockfighter.shared.Globals;
import java.awt.Graphics2D;

public class ParticleShieldReflectBuff extends Particle {

    double pX, speedX;

    public ParticleShieldReflectBuff(final int x, final int y, final byte f) {
        super(x, y, f);
        this.x += Globals.rng(10) * 5;
        this.y += -Globals.rng(80);
        this.pX = this.x;
        this.speedX = (Globals.rng(10) - 5) * 1.5D;

        this.frame = Globals.rng(5);
        this.frameDuration = 25;
        this.duration = 400;
        this.particleData = Globals.Particles.SHIELD_REFLECT_BUFF;
    }

    @Override
    public void update() {
        super.update();
        this.y -= 8;
        this.pX += this.speedX;
        this.x = (int) this.pX;
    }

    @Override
    public void draw(final Graphics2D g) {
        draw(g, -60, 45, false);
    }
}
