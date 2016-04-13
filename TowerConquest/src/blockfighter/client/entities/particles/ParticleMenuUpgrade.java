package blockfighter.client.entities.particles;

import blockfighter.client.Globals;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 *
 * @author Ken Kwan
 */
public class ParticleMenuUpgrade extends Particle {

    private final int color, deltaX;
    private int deltaY;

    public ParticleMenuUpgrade(final int k, final int x, final int y, final int c, final int dX, final int dY) {
        super(x, y);
        this.duration = 1000;
        this.color = c;
        this.deltaX = dX;
        this.deltaY = dY;
    }

    @Override
    public void update() {
        super.update();
        this.x += this.deltaX;
        this.y += this.deltaY;
        if (Globals.rng(2) == 0) {
            this.deltaY++;
        }

    }

    @Override
    public void draw(final Graphics2D g) {
        final BufferedImage sprite = Globals.MENU_UPGRADEPARTICLE[this.color];
        g.drawImage(sprite, this.x, this.y, null);
    }
}
