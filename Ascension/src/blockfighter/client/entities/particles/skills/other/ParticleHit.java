package blockfighter.client.entities.particles.skills.other;

import blockfighter.client.Core;
import blockfighter.client.entities.particles.Particle;
import blockfighter.shared.Globals;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class ParticleHit extends Particle {

    public ParticleHit(final int x, final int y, final byte f) {
        super(x, y, f);
        this.frame = 0;
        this.frameDuration = 50;
        this.duration = Globals.Particles.HIT.getNumFrames() * this.frameDuration;
    }

    @Override
    public void update() {
        super.update();

        if (Globals.nsToMs(Core.getLogicModule().getTime() - this.lastFrameTime) >= this.frameDuration) {
            if (Globals.Particles.HIT.getSprite() != null && this.frame < Globals.Particles.HIT.getSprite().length) {
                this.frame++;
            }
            this.lastFrameTime = Core.getLogicModule().getTime();
        }
    }

    @Override
    public void draw(final Graphics2D g) {
        if (Globals.Particles.HIT.getSprite() == null) {
            return;
        }
        if (this.frame >= Globals.Particles.HIT.getSprite().length) {
            return;
        }

        final BufferedImage sprite = Globals.Particles.HIT.getSprite()[this.frame];
        final int drawSrcX = this.x + ((this.facing == Globals.RIGHT) ? -sprite.getWidth() : sprite.getWidth());
        final int drawSrcY = this.y - sprite.getHeight() / 2;
        final int drawDscY = drawSrcY + sprite.getHeight();
        final int drawDscX = this.x + ((this.facing == Globals.RIGHT) ? 0 : 0);
        g.drawImage(sprite, drawSrcX, drawSrcY, drawDscX, drawDscY, 0, 0, sprite.getWidth(), sprite.getHeight(), null);
    }
}
