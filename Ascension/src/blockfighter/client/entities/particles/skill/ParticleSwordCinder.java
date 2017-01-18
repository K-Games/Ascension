package blockfighter.client.entities.particles.skill;

import blockfighter.client.entities.particles.Particle;
import blockfighter.shared.Globals;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class ParticleSwordCinder extends Particle {

    public ParticleSwordCinder(final int x, final int y, final byte f) {
        super(x, y, f);
        this.frame = 0;
        this.frameDuration = 50;
        this.duration = 400;
    }

    @Override
    public void update() {
        super.update();
        if (Globals.nsToMs(logic.getTime() - this.lastFrameTime) >= this.frameDuration) {
            if (Globals.Particles.SWORD_CINDER.getSprite() != null && this.frame < Globals.Particles.SWORD_CINDER.getSprite().length) {
                this.frame++;
            }
            this.lastFrameTime = logic.getTime();
        }
    }

    @Override
    public void draw(final Graphics2D g) {
        if (Globals.Particles.SWORD_CINDER.getSprite() == null) {
            return;
        }
        if (this.frame >= Globals.Particles.SWORD_CINDER.getSprite().length) {
            return;
        }
        final BufferedImage sprite = Globals.Particles.SWORD_CINDER.getSprite()[this.frame];
        final int drawSrcX = this.x + ((this.facing == Globals.RIGHT) ? -130 : 130);
        final int drawSrcY = this.y - sprite.getHeight() + 40;
        final int drawDscY = drawSrcY + sprite.getHeight();
        final int drawDscX = drawSrcX + ((this.facing == Globals.RIGHT) ? sprite.getWidth() : -sprite.getWidth());
        g.drawImage(sprite, drawSrcX, drawSrcY, drawDscX, drawDscY, 0, 0, sprite.getWidth(), sprite.getHeight(), null);
    }
}