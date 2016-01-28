package blockfighter.client.entities.particles;

import blockfighter.client.Globals;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class ParticleShieldToss extends Particle {

    public ParticleShieldToss(final int k, final int x, final int y, final byte f) {
        super(k, x, y, f);
        this.frame = 0;
        this.frameDuration = 25;
        this.duration = 500;
    }

    @Override
    public void update() {
        super.update();
        this.frameDuration -= Globals.LOGIC_UPDATE / 1000000;
        if (this.facing == Globals.RIGHT) {
            this.x += 20;
        } else {
            this.x -= 20;
        }
        if (this.frameDuration <= 0) {
            this.frameDuration = 25;
            if (this.frame < PARTICLE_SPRITE[Globals.PARTICLE_SHIELD_TOSS].length - 1) {
                this.frame++;
            } else {
                this.frame = 0;
            }
        }
    }

    @Override
    public void draw(final Graphics2D g) {
        if (PARTICLE_SPRITE[Globals.PARTICLE_SHIELD_TOSS] == null) {
            return;
        }
        if (this.frame >= PARTICLE_SPRITE[Globals.PARTICLE_SHIELD_TOSS].length) {
            return;
        }
        final BufferedImage sprite = PARTICLE_SPRITE[Globals.PARTICLE_SHIELD_TOSS][this.frame];
        final int drawSrcX = this.x - 30 + ((this.facing == Globals.RIGHT) ? 0 : sprite.getWidth());
        final int drawSrcY = this.y - 30;
        final int drawDscY = drawSrcY + sprite.getHeight();
        final int drawDscX = drawSrcX + ((this.facing == Globals.RIGHT) ? sprite.getWidth() : -sprite.getWidth());
        g.drawImage(sprite, drawSrcX, drawSrcY, drawDscX, drawDscY, 0, 0, sprite.getWidth(), sprite.getHeight(), null);
    }
}
