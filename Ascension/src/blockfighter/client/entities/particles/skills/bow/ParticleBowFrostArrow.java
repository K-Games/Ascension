package blockfighter.client.entities.particles.skills.bow;

import blockfighter.client.entities.particles.Particle;
import blockfighter.shared.Globals;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class ParticleBowFrostArrow extends Particle {

    public ParticleBowFrostArrow(final int x, final int y, final byte f) {
        super(x, y, f);
        this.frame = 0;
        this.frameDuration = 50;
        this.duration = 700;
    }

    @Override
    public void update() {
        super.update();
        if (Globals.nsToMs(logic.getTime() - this.lastFrameTime) >= this.frameDuration) {
            if (Globals.Particles.BOW_FROSTARROW.getSprite() != null && this.frame < Globals.Particles.BOW_FROSTARROW.getSprite().length) {
                this.frame++;
            }
            this.lastFrameTime = logic.getTime();
        }
    }

    @Override
    public void draw(final Graphics2D g) {
        if (Globals.Particles.BOW_FROSTARROW.getSprite() == null) {
            return;
        }
        if (this.frame >= Globals.Particles.BOW_FROSTARROW.getSprite().length) {
            return;
        }
        final BufferedImage sprite = Globals.Particles.BOW_FROSTARROW.getSprite()[this.frame];
        final int drawSrcX = this.x;
        final int drawSrcY = this.y - sprite.getHeight() + 20;
        final int drawDscY = drawSrcY + sprite.getHeight();
        final int drawDscX = drawSrcX + ((this.facing == Globals.RIGHT) ? sprite.getWidth() : -sprite.getWidth());
        g.drawImage(sprite, drawSrcX, drawSrcY, drawDscX, drawDscY, 0, 0, sprite.getWidth(), sprite.getHeight(), null);
    }
}
