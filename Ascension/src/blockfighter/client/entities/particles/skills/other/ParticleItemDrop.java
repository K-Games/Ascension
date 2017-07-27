package blockfighter.client.entities.particles.skills.other;

import blockfighter.client.entities.items.Item;
import blockfighter.client.entities.particles.Particle;
import blockfighter.client.entities.player.Player;
import blockfighter.shared.Globals;
import java.awt.Graphics2D;

public class ParticleItemDrop extends Particle {

    private Item item;
    private double xSpeed, ySpeed, deltaY = -30;
    private static int STATIC_X_VARIATION = Globals.rng(5);
    private int xVariation;

    public ParticleItemDrop(final Player p) {
        super(p);
        this.frame = 0;
        this.duration = 2000;
    }

    public ParticleItemDrop(final Player source, final Player dest) {
        super(dest);
        xVariation = STATIC_X_VARIATION;
        this.x = source.getX() + (xVariation - 2) * 20;

        STATIC_X_VARIATION++;
        STATIC_X_VARIATION = (STATIC_X_VARIATION >= 5) ? 0 : STATIC_X_VARIATION;
        this.y = source.getY();
        this.ySpeed = -1 * (Globals.rng(4) / 10D + 0.5);
        this.frame = 0;
        this.duration = 2000;
    }

    public void setItem(Item i) {
        this.item = i;
    }

    @Override
    public void update() {
        super.update();
        this.x += this.xSpeed;
        this.deltaY += this.ySpeed;
    }

    @Override
    public void draw(final Graphics2D g) {
        this.x = owner.getX() + (xVariation - 2) * 20;
        this.y = (int) (owner.getY() + this.deltaY);
        item.drawIcon(g, this.x - 30, this.y - 30);
    }
}
