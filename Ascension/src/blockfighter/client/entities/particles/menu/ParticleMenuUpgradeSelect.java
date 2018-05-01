package blockfighter.client.entities.particles.menu;

import blockfighter.client.entities.particles.Particle;
import blockfighter.shared.Globals;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class ParticleMenuUpgradeSelect extends Particle {

    private final int color;
    private final double deltaX, deltaY;
    private double realX, realY;

    public ParticleMenuUpgradeSelect(final int x, final int y, int destX, int destY) {
        super(x, y);
        this.x += Globals.rng(31) - 15;
        this.y += Globals.rng(31) - 15;
        destX += Globals.rng(31) - 15;
        destY += Globals.rng(31) - 15;
        this.realX = this.x;
        this.realY = this.y;
        this.duration = Globals.rng(701) + 300;
        this.color = Globals.rng(4);
        this.deltaX = (destX - this.realX) / (this.duration / 1000D * Globals.CLIENT_LOGIC_TICKS_PER_SEC);
        this.deltaY = (destY - this.realY) / (this.duration / 1000D * Globals.CLIENT_LOGIC_TICKS_PER_SEC);
    }

    @Override
    public void update() {
        this.realX += this.deltaX;
        this.realY += this.deltaY;
        this.x = (int) this.realX;
        this.y = (int) this.realY;
    }

    @Override
    public void draw(final Graphics2D g) {
        final BufferedImage sprite = Globals.MENU_UPGRADEPARTICLE[this.color];
        g.drawImage(sprite, this.x, this.y, null);
    }
}
