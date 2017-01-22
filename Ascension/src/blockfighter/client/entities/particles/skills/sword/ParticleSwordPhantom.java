package blockfighter.client.entities.particles.skills.sword;

import blockfighter.client.entities.particles.Particle;
import blockfighter.shared.Globals;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class ParticleSwordPhantom extends Particle {

    public ParticleSwordPhantom(final int x, final int y, final byte f) {
        super(x, y, f);
        this.frame = 0;
        this.frameDuration = 25;
        this.duration = 200;
    }

    @Override
    public void update() {
        super.update();

        if (Globals.nsToMs(logic.getTime() - this.lastFrameTime) >= this.frameDuration) {
            this.frameDuration = 25;
            if (Globals.Particles.SWORD_PHANTOM.getSprite() != null && this.frame < Globals.Particles.SWORD_PHANTOM.getSprite().length - 1) {
                this.frame++;
            }
            this.lastFrameTime = logic.getTime();
        }
    }

    @Override
    public void draw(final Graphics2D g) {

        if (Globals.Particles.SWORD_PHANTOM.getSprite() == null) {
            return;
        }
        if (this.frame >= Globals.Particles.SWORD_PHANTOM.getSprite().length) {
            return;
        }
        final BufferedImage sprite = Globals.Particles.SWORD_PHANTOM.getSprite()[this.frame];
        final int drawSrcX = this.x + ((this.facing == Globals.RIGHT) ? sprite.getWidth() / -2 : sprite.getWidth() / 2);
        final int drawSrcY = this.y - sprite.getHeight();
        final int drawDscY = drawSrcY + sprite.getHeight();
        final int drawDscX = this.x + ((this.facing == Globals.RIGHT) ? sprite.getWidth() / 2 : sprite.getWidth() / -2);
        g.drawImage(sprite, drawSrcX, drawSrcY, drawDscX, drawDscY, 0, 0, sprite.getWidth(), sprite.getHeight(), null);
    }
}
