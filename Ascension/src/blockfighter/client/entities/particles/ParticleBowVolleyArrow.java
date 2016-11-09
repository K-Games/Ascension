package blockfighter.client.entities.particles;

import blockfighter.shared.Globals;
import static blockfighter.client.entities.particles.Particle.logic;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class ParticleBowVolleyArrow extends Particle {

    public ParticleBowVolleyArrow(final int x, final int y, final byte f) {
        super(x, y, f);
        this.frame = 0;
        this.frameDuration = 50;
        this.duration = 350;
    }

    @Override
    public void update() {
        super.update();
        if (Globals.nsToMs(logic.getTime() - this.lastFrameTime) >= this.frameDuration) {
            this.frameDuration = 50;
            if (PARTICLE_SPRITE != null && this.frame < PARTICLE_SPRITE[Globals.PARTICLE_BOW_VOLLEYARROW].length) {
                this.frame++;
            }
            this.lastFrameTime = logic.getTime();
        }
    }

    @Override
    public void draw(final Graphics2D g) {
        if (PARTICLE_SPRITE[Globals.PARTICLE_BOW_VOLLEYARROW] == null) {
            return;
        }
        if (this.frame >= PARTICLE_SPRITE[Globals.PARTICLE_BOW_VOLLEYARROW].length) {
            return;
        }
        final BufferedImage sprite = PARTICLE_SPRITE[Globals.PARTICLE_BOW_VOLLEYARROW][this.frame];
        final int drawSrcX = this.x + ((this.facing == Globals.RIGHT) ? 0 : -0);
        final int drawSrcY = this.y - sprite.getHeight() / 2;
        final int drawDscY = drawSrcY + sprite.getHeight();
        final int drawDscX = drawSrcX + ((this.facing == Globals.RIGHT) ? sprite.getWidth() : -sprite.getWidth());
        g.drawImage(sprite, drawSrcX, drawSrcY, drawDscX, drawDscY, 0, 0, sprite.getWidth(), sprite.getHeight(), null);
    }
}
