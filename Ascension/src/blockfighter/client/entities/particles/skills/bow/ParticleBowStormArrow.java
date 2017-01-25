package blockfighter.client.entities.particles.skills.bow;

import blockfighter.client.Core;
import blockfighter.client.entities.particles.Particle;
import blockfighter.shared.Globals;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class ParticleBowStormArrow extends Particle {

    public ParticleBowStormArrow(final int x, final int y, final byte f) {
        super(x, y, f);
        this.x += Globals.rng(35) * 20 + ((this.facing == Globals.RIGHT) ? -180 : 180);
        this.y -= Globals.rng(2) * 5;
        this.frame = 0;
        this.frameDuration = 50;
        this.duration = 250;
    }

    @Override
    public void update() {
        super.update();
        if (Globals.nsToMs(Core.getLogicModule().getTime() - this.lastFrameTime) >= this.frameDuration) {
            if (Globals.Particles.BOW_STORM_ARROW.getSprite() != null && this.frame < Globals.Particles.BOW_STORM_ARROW.getSprite().length) {
                this.frame++;
            }
            this.lastFrameTime = Core.getLogicModule().getTime();
        }
    }

    @Override
    public void draw(final Graphics2D g) {
        if (Globals.Particles.BOW_STORM_ARROW.getSprite() == null) {
            return;
        }
        if (this.frame >= Globals.Particles.BOW_STORM_ARROW.getSprite().length) {
            return;
        }
        final BufferedImage sprite = Globals.Particles.BOW_STORM_ARROW.getSprite()[this.frame];
        final int drawSrcX = this.x + ((this.facing == Globals.RIGHT) ? -sprite.getWidth() / 2 : sprite.getWidth() / 2);
        final int drawSrcY = this.y - 130;
        final int drawDscY = drawSrcY + sprite.getHeight();
        final int drawDscX = drawSrcX + ((this.facing == Globals.RIGHT) ? sprite.getWidth() : -sprite.getWidth());
        g.drawImage(sprite, drawSrcX, drawSrcY, drawDscX, drawDscY, 0, 0, sprite.getWidth(), sprite.getHeight(), null);
    }
}
