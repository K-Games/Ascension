package blockfighter.client.entities.particles;

import blockfighter.client.Globals;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class ParticlePassiveResist extends Particle {

    public ParticlePassiveResist(final int k, final int x, final int y) {
        super(k, x, y, Globals.RIGHT);
        this.frame = 0;
        this.frameDuration = 25;
        this.duration = 300;
    }

    @Override
    public void update() {
        super.update();

        if (Globals.nsToMs(logic.getTime() - this.lastFrameTime) >= this.frameDuration) {
            if (this.frame < PARTICLE_SPRITE[Globals.PARTICLE_PASSIVE_RESIST].length - 1) {
                this.frame++;
            }
            this.lastFrameTime = logic.getTime();
        }
    }

    @Override
    public void draw(final Graphics2D g) {
        if (PARTICLE_SPRITE[Globals.PARTICLE_PASSIVE_RESIST] == null) {
            return;
        }
        if (this.frame >= PARTICLE_SPRITE[Globals.PARTICLE_PASSIVE_RESIST].length) {
            return;
        }
        final BufferedImage sprite = PARTICLE_SPRITE[Globals.PARTICLE_PASSIVE_RESIST][this.frame];
        final int drawSrcX = this.x - sprite.getWidth() / 2;
        final int drawSrcY = this.y - sprite.getHeight() + 50;
        final int drawDscY = drawSrcY + sprite.getHeight();
        final int drawDscX = drawSrcX + sprite.getWidth();
        g.drawImage(sprite, drawSrcX, drawSrcY, drawDscX, drawDscY, 0, 0, sprite.getWidth(), sprite.getHeight(), null);
        g.setColor(Color.WHITE);
    }
}
