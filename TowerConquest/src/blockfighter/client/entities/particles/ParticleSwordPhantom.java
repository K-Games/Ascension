package blockfighter.client.entities.particles;

import blockfighter.client.Globals;
import static blockfighter.client.entities.particles.Particle.PARTICLE_SPRITE;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class ParticleSwordPhantom extends Particle {

    public ParticleSwordPhantom(final int k, final int x, final int y, final byte f) {
        super(k, x, y, f);
        this.frame = 0;
        this.frameDuration = 50;
        this.duration = 400;
    }

    @Override
    public void update() {
        super.update();
        this.frameDuration -= Globals.LOGIC_UPDATE / 1000000;
        if (this.frameDuration <= 0) {
            this.frameDuration = 50;
            if (this.frame < PARTICLE_SPRITE[Globals.PARTICLE_SWORD_PHANTOM].length) {
                this.frame++;
            }
        }
    }

    @Override
    public void draw(final Graphics2D g) {

        if (PARTICLE_SPRITE[Globals.PARTICLE_SWORD_PHANTOM] == null) {
            return;
        }
        if (this.frame >= PARTICLE_SPRITE[Globals.PARTICLE_SWORD_PHANTOM].length) {
            return;
        }
        final BufferedImage sprite = PARTICLE_SPRITE[Globals.PARTICLE_SWORD_PHANTOM][this.frame];
        final int drawSrcX = this.x + ((this.facing == Globals.RIGHT) ? sprite.getWidth() / -2 : sprite.getWidth() / 2);
        final int drawSrcY = this.y - sprite.getHeight();
        final int drawDscY = drawSrcY + sprite.getHeight();
        final int drawDscX = this.x + ((this.facing == Globals.RIGHT) ? sprite.getWidth() / 2 : sprite.getWidth() / -2);
        g.drawImage(sprite, drawSrcX, drawSrcY, drawDscX, drawDscY, 0, 0, sprite.getWidth(), sprite.getHeight(), null);
    }
}
