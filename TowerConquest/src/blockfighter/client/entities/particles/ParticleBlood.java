package blockfighter.client.entities.particles;

import blockfighter.client.Globals;
import java.awt.Color;
import java.awt.Graphics2D;

public class ParticleBlood extends Particle {

    double xDouble, xSpeed, yDouble, ySpeed, drawSize = 7;

    public ParticleBlood(final int x, final int y, final byte f) {
        super(x, y, f);
        this.x = x - 5 + Globals.rng(10);
        this.y = y - 75 + Globals.rng(60);
        this.xDouble = this.x;
        this.yDouble = this.y;
        this.xSpeed = ((f != Globals.RIGHT) ? 1 : -1) * (.15 * Globals.rng(38) + 5.5);
        this.ySpeed = (.15 * Globals.rng(48) - 4);
        this.frame = 0;
        this.duration = 400;
    }

    @Override
    public void update() {
        super.update();
        this.drawSize -= .1;
        this.xDouble += this.xSpeed;
        this.yDouble += this.ySpeed;
        this.x = (int) this.xDouble;
        this.y = (int) this.yDouble;
    }

    @Override
    public void draw(final Graphics2D g) {
        g.setColor(Color.RED);
        g.fillRect(this.x, this.y, (int) this.drawSize, (int) this.drawSize);
    }
}
