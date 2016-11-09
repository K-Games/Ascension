package blockfighter.client.entities.particles;

import blockfighter.shared.Globals;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class ParticleShieldChargeParticle extends Particle {

    public ParticleShieldChargeParticle(final int x, final int y, final byte f) {
        super(x, y, f);
        this.frame = 0;
        this.frameDuration = 75;
        this.duration = 400;
        this.y += Globals.rng(11) * 15 - 170;
        if (this.facing == Globals.RIGHT) {
            this.x -= 179;
        }
    }

    @Override
    public void update() {
        super.update();
        if (Globals.nsToMs(logic.getTime() - this.lastFrameTime) >= this.frameDuration) {
            if (PARTICLE_SPRITE != null && this.frame < PARTICLE_SPRITE[Globals.PARTICLE_SHIELD_CHARGEPARTICLE].length) {
                this.frame++;
            }
            this.lastFrameTime = logic.getTime();
        }
    }

    @Override
    public void draw(final Graphics2D g) {
        if (PARTICLE_SPRITE[Globals.PARTICLE_SHIELD_CHARGEPARTICLE] == null) {
            return;
        }
        if (this.frame >= PARTICLE_SPRITE[Globals.PARTICLE_SHIELD_CHARGEPARTICLE].length) {
            return;
        }
        final BufferedImage sprite = PARTICLE_SPRITE[Globals.PARTICLE_SHIELD_CHARGEPARTICLE][this.frame];
        final int drawSrcX = this.x;
        final int drawSrcY = this.y;
        final int drawDscY = drawSrcY + sprite.getHeight();
        final int drawDscX = this.x + sprite.getWidth();
        g.drawImage(sprite, drawSrcX, drawSrcY, drawDscX, drawDscY, 0, 0, sprite.getWidth(), sprite.getHeight(), null);
    }
}
