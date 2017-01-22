package blockfighter.client.entities.particles.skills.shield;

import blockfighter.client.entities.particles.Particle;
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
            if (Globals.Particles.SHIELD_CHARGE_PARTICLE.getSprite() != null && this.frame < Globals.Particles.SHIELD_CHARGE_PARTICLE.getSprite().length) {
                this.frame++;
            }
            this.lastFrameTime = logic.getTime();
        }
    }

    @Override
    public void draw(final Graphics2D g) {
        if (Globals.Particles.SHIELD_CHARGE_PARTICLE.getSprite() == null) {
            return;
        }
        if (this.frame >= Globals.Particles.SHIELD_CHARGE_PARTICLE.getSprite().length) {
            return;
        }
        final BufferedImage sprite = Globals.Particles.SHIELD_CHARGE_PARTICLE.getSprite()[this.frame];
        final int drawSrcX = this.x;
        final int drawSrcY = this.y;
        final int drawDscY = drawSrcY + sprite.getHeight();
        final int drawDscX = this.x + sprite.getWidth();
        g.drawImage(sprite, drawSrcX, drawSrcY, drawDscX, drawDscY, 0, 0, sprite.getWidth(), sprite.getHeight(), null);
    }
}
