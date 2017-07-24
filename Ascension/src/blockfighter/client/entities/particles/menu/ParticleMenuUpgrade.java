package blockfighter.client.entities.particles.menu;

import blockfighter.client.entities.particles.Particle;
import blockfighter.shared.Globals;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class ParticleMenuUpgrade extends Particle {

    private final int color;
    private final double deltaX;
    private double deltaY, realX, realY;

    public ParticleMenuUpgrade(final int x, final int y, final int c, final double dX, final double dY) {
        super(x, y);
        this.duration = 1000;
        this.color = c;
        this.realX = x;
        this.realY = y;
        this.deltaX = dX;
        this.deltaY = dY;
    }

    @Override
    public void update() {
        super.update();
        this.realX += this.deltaX;
        this.realY += this.deltaY;
        if (Globals.rng(2) == 0) {
            this.deltaY += 0.75;
        }
    }

    @Override
    public void draw(final Graphics2D g) {
        final BufferedImage sprite = Globals.MENU_UPGRADEPARTICLE[this.color];
        g.drawImage(sprite, (int) this.realX, (int) this.realY, null);
    }
}
