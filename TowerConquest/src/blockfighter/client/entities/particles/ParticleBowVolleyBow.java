package blockfighter.client.entities.particles;

import blockfighter.client.Globals;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class ParticleBowVolleyBow extends Particle {

    public ParticleBowVolleyBow(final int x, final int y, final byte f) {
        super(x, y, f);
        this.frame = 0;
        this.duration = 125;
    }

    @Override
    public void draw(final Graphics2D g) {
        if (PARTICLE_SPRITE[Globals.PARTICLE_BOW_VOLLEYBOW] == null) {
            return;
        }
        if (this.frame >= PARTICLE_SPRITE[Globals.PARTICLE_BOW_VOLLEYBOW].length) {
            return;
        }
        final BufferedImage sprite = PARTICLE_SPRITE[Globals.PARTICLE_BOW_VOLLEYBOW][this.frame];
        final int drawSrcX = this.x + ((this.facing == Globals.RIGHT) ? -15 : 15);
        final int drawSrcY = this.y - 255;
        final int drawDscY = drawSrcY + sprite.getHeight();
        final int drawDscX = this.x + ((this.facing == Globals.RIGHT) ? sprite.getWidth() - 15 : -sprite.getWidth() + 15);
        g.drawImage(sprite, drawSrcX, drawSrcY, drawDscX, drawDscY, 0, 0, sprite.getWidth(), sprite.getHeight(), null);
    }
}
