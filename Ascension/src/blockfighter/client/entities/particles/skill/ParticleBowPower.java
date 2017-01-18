package blockfighter.client.entities.particles.skill;

import blockfighter.client.entities.particles.Particle;
import blockfighter.shared.Globals;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class ParticleBowPower extends Particle {

    public ParticleBowPower(final int x, final int y, final byte f) {
        super(x, y, f);
        this.frame = 0;
        this.frameDuration = 75;
        this.duration = 250;
    }

    @Override
    public void update() {
        super.update();
        if (Globals.nsToMs(logic.getTime() - this.particleStartTime) >= 50) {
            for (int i = 0; i < 2; i++) {
                final ParticleBowPowerParticle b = new ParticleBowPowerParticle(this.x, this.y - 150, this.facing);
                logic.getScreen().addParticle(b);
            }
        }
        if (Globals.nsToMs(logic.getTime() - this.lastFrameTime) >= this.frameDuration) {
            if (Globals.Particles.BOW_POWER.getSprite() != null && this.frame < Globals.Particles.BOW_POWER.getSprite().length - 1) {
                this.frame++;
            }
            this.lastFrameTime = logic.getTime();
        }
    }

    @Override
    public void draw(final Graphics2D g) {
        if (Globals.Particles.BOW_POWER.getSprite() == null) {
            return;
        }
        if (this.frame >= Globals.Particles.BOW_POWER.getSprite().length) {
            return;
        }
        final BufferedImage sprite = Globals.Particles.BOW_POWER.getSprite()[this.frame];
        final int drawSrcX = this.x + ((this.facing == Globals.RIGHT) ? -50 : 50);
        final int drawSrcY = this.y - sprite.getHeight() + 110;
        final int drawDscY = drawSrcY + sprite.getHeight();
        final int drawDscX = drawSrcX + ((this.facing == Globals.RIGHT) ? sprite.getWidth() + 250 : -sprite.getWidth() - 250);
        g.drawImage(sprite, drawSrcX, drawSrcY, drawDscX, drawDscY, 0, 0, sprite.getWidth(), sprite.getHeight(), null);
    }
}
