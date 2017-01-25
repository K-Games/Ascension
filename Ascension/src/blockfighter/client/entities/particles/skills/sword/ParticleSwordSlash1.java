package blockfighter.client.entities.particles.skills.sword;

import blockfighter.client.Core;
import blockfighter.client.entities.particles.Particle;
import blockfighter.shared.Globals;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class ParticleSwordSlash1 extends Particle {

    public ParticleSwordSlash1(final int x, final int y, final byte f) {
        super(x, y, f);
        this.frame = 0;
        this.frameDuration = 75;
        this.duration = 250;
    }

    @Override
    public void update() {
        super.update();

        if (Globals.nsToMs(Core.getLogicModule().getTime() - this.lastFrameTime) >= this.frameDuration) {
            if (Globals.Particles.SWORD_SLASH1.getSprite() != null && this.frame < Globals.Particles.SWORD_SLASH1.getSprite().length) {
                this.frame++;
            }
            this.lastFrameTime = Core.getLogicModule().getTime();
        }
    }

    @Override
    public void draw(final Graphics2D g) {
        if (Globals.Particles.SWORD_SLASH1.getSprite() == null) {
            return;
        }
        if (this.frame >= Globals.Particles.SWORD_SLASH1.getSprite().length) {
            return;
        }
        final BufferedImage sprite = Globals.Particles.SWORD_SLASH1.getSprite()[this.frame];
        final int drawSrcX = this.x + ((this.facing == Globals.RIGHT) ? -60 : 60);
        final int drawSrcY = this.y - sprite.getHeight() + 10;
        final int drawDscY = drawSrcY + sprite.getHeight();
        final int drawDscX = drawSrcX + ((this.facing == Globals.RIGHT) ? sprite.getWidth() : -sprite.getWidth());
        g.drawImage(sprite, drawSrcX, drawSrcY, drawDscX, drawDscY, 0, 0, sprite.getWidth(), sprite.getHeight(), null);
    }
}
