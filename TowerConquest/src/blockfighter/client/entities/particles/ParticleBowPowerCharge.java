package blockfighter.client.entities.particles;

import blockfighter.client.Globals;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class ParticleBowPowerCharge extends Particle {

    public ParticleBowPowerCharge(final int x, final int y, final byte f) {
        super(x, y, f);
        this.frame = 0;
        this.frameDuration = 25;
        this.duration = 600;
    }

    @Override
    public void update() {
        super.update();
        if (Globals.nsToMs(logic.getTime() - this.lastFrameTime) >= this.frameDuration) {
            this.frameDuration = 25;
            if (PARTICLE_SPRITE != null && this.frame < PARTICLE_SPRITE[Globals.PARTICLE_BOW_POWERCHARGE].length - 1) {
                this.frame++;
            }
            this.lastFrameTime = logic.getTime();
        }
    }

    @Override
    public void draw(final Graphics2D g) {
        if (PARTICLE_SPRITE[Globals.PARTICLE_BOW_POWERCHARGE] == null) {
            return;
        }
        if (this.frame >= PARTICLE_SPRITE[Globals.PARTICLE_BOW_POWERCHARGE].length) {
            return;
        }
        final BufferedImage sprite = PARTICLE_SPRITE[Globals.PARTICLE_BOW_POWERCHARGE][this.frame];
        final int drawSrcX = this.x - sprite.getWidth() / 2;
        final int drawSrcY = this.y;
        final int drawDscY = drawSrcY + sprite.getHeight();
        final int drawDscX = drawSrcX + sprite.getWidth();
        g.drawImage(sprite, drawSrcX, drawSrcY, drawDscX, drawDscY, 0, 0, sprite.getWidth(), sprite.getHeight(), null);
        g.setColor(Color.WHITE);
    }
}
