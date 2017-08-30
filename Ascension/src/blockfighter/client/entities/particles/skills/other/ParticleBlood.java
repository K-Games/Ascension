package blockfighter.client.entities.particles.skills.other;

import blockfighter.client.entities.particles.Particle;
import blockfighter.shared.Globals;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class ParticleBlood extends Particle {

    double xDouble, xSpeed, yDouble, ySpeed, drawSize = 6;

    public ParticleBlood(final int x, final int y, final byte f) {
        super(x, y, f);
        this.x = x - 5 + Globals.rng(10);
        this.y = y - 75 + Globals.rng(30);
        this.xDouble = this.x;
        this.yDouble = this.y;
        this.xSpeed = ((f != Globals.RIGHT) ? 1 : -1) * (.075 * Globals.rng(70) + 4.5);
        this.ySpeed = (.1 * Globals.rng(100) - 14);
        this.frame = 0;
        this.duration = 300 + Globals.rng(30) * 10;
    }

    @Override
    public void update() {
        super.update();
        this.drawSize -= .03;
        this.ySpeed += Globals.GRAVITY * 1.5;
        this.xDouble += this.xSpeed;
        this.yDouble += this.ySpeed;
        this.x = (int) this.xDouble;
        this.y = (int) this.yDouble;
    }

    @Override
    public void draw(final Graphics2D g) {
        if (Globals.Particles.BLOOD.getSprite() == null) {
            return;
        }
        if (this.frame >= Globals.Particles.BLOOD.getSprite().length) {
            return;
        }

        final BufferedImage sprite = Globals.Particles.BLOOD.getSprite()[this.frame];
        g.drawImage(sprite, this.x, this.y, (int) this.drawSize, (int) this.drawSize, null);
    }
}
