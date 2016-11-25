package blockfighter.client.entities.particles;

import blockfighter.shared.Globals;
import java.awt.Color;
import java.awt.Graphics2D;

public class ParticleBlood extends Particle {

    double xDouble, xSpeed, yDouble, ySpeed, drawSize = 6;

    public ParticleBlood(final int x, final int y, final byte f) {
        super(x, y, f);
        this.x = x - 5 + Globals.rng(10);
        this.y = y - 75 + Globals.rng(30);
        this.xDouble = this.x;
        this.yDouble = this.y;
        this.xSpeed = ((f != Globals.RIGHT) ? 1 : -1) * (.075 * Globals.rng(80) + 5.5);
        this.ySpeed = (.1 * Globals.rng(100) - 11);
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
        g.setColor(Color.RED);
        g.fillOval(this.x, this.y, (int) this.drawSize, (int) this.drawSize);
    }
}
