package blockfighter.client.entities.particles.skills.sword;

import blockfighter.client.Core;
import blockfighter.client.entities.particles.Particle;
import blockfighter.shared.Globals;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class ParticleSwordVorpal extends Particle {

    public ParticleSwordVorpal(final int x, final int y, final byte f) {
        super(x, y, f);
        this.y -= 100;
        this.frame = 0;
        this.frameDuration = 50;
        this.duration = 200;
    }

    @Override
    public void update() {
        super.update();

        if (Globals.nsToMs(Core.getLogicModule().getTime() - this.lastFrameTime) >= this.frameDuration) {
            this.frameDuration = 50;
            if (Globals.Particles.SWORD_VORPAL.getSprite() != null && this.frame < Globals.Particles.SWORD_VORPAL.getSprite().length - 1) {
                this.frame++;
            }
            this.lastFrameTime = Core.getLogicModule().getTime();
        }
    }

    @Override
    public void draw(final Graphics2D g) {
        if (Globals.Particles.SWORD_VORPAL.getSprite() == null) {
            return;
        }
        if (this.frame >= Globals.Particles.SWORD_VORPAL.getSprite().length) {
            return;
        }
        final BufferedImage sprite = Globals.Particles.SWORD_VORPAL.getSprite()[this.frame];
        final int drawSrcX = this.x + ((this.facing == Globals.RIGHT) ? -60 : 60);
        final int drawSrcY = this.y;
        final int drawDscY = drawSrcY + sprite.getHeight();
        final int drawDscX = drawSrcX + ((this.facing == Globals.RIGHT) ? sprite.getWidth() : -sprite.getWidth());
        g.drawImage(sprite, drawSrcX, drawSrcY, drawDscX, drawDscY, 0, 0, sprite.getWidth(), sprite.getHeight(), null);
    }
}
