package blockfighter.client.entities.particles;

import blockfighter.client.Globals;
import java.awt.Color;
import java.awt.Graphics2D;

public class ParticleBlood extends Particle {

    double xDouble, xSpeed, yDouble, ySpeed, size = 7;

    public ParticleBlood(int k, int x, int y, byte f) {
        super(k, x, y, f);
        this.x = x;
        this.y = y - 48;
        xDouble = (double) this.x;
        yDouble = (double) this.y;
        xSpeed = ((f == Globals.RIGHT) ? -1 : 1) * (.15 * Globals.rng(38) + 3.5);
        ySpeed = (.15 * Globals.rng(48) - 4);
        frame = 0;
        duration = 400;
    }

    @Override
    public void update() {
        super.update();
        size -= .1;
        xDouble += xSpeed;
        yDouble += ySpeed;
        x = (int) xDouble;
        y = (int) yDouble;
    }

    @Override
    public void draw(Graphics2D g) {
        g.setColor(Color.RED);
        g.fillRect(x, y, (int) size, (int) size);
    }
}
