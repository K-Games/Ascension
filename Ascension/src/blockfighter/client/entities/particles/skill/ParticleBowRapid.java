package blockfighter.client.entities.particles.skill;

import blockfighter.client.entities.particles.Particle;
import blockfighter.shared.Globals;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class ParticleBowRapid extends Particle {

    public ParticleBowRapid(final int x, final int y, final byte f) {
        super(x, y, f);
        this.frame = 0;
        this.frameDuration = 50;
        this.duration = 300;
    }

    @Override
    public void update() {
        super.update();
        if (Globals.nsToMs(logic.getTime() - this.lastFrameTime) >= this.frameDuration) {
            if (Globals.Particles.BOW_RAPID.getSprite() != null && this.frame < Globals.Particles.BOW_RAPID.getSprite().length - 1) {
                this.frame++;
            }
            this.lastFrameTime = logic.getTime();
        }
    }

    @Override
    public void draw(final Graphics2D g) {
        if (Globals.Particles.BOW_RAPID.getSprite() == null) {
            return;
        }
        if (this.frame >= Globals.Particles.BOW_RAPID.getSprite().length) {
            return;
        }
        final BufferedImage sprite = Globals.Particles.BOW_RAPID.getSprite()[this.frame];
        final int drawSrcX = this.x + ((this.facing == Globals.RIGHT) ? 0 : sprite.getWidth());
        final int drawSrcY = this.y;
        final int drawDscY = drawSrcY + sprite.getHeight();
        final int drawDscX = this.x + ((this.facing == Globals.RIGHT) ? sprite.getWidth() : 0);
        g.drawImage(sprite, drawSrcX, drawSrcY, drawDscX, drawDscY, 0, 0, sprite.getWidth(), sprite.getHeight(), null);
    }
}