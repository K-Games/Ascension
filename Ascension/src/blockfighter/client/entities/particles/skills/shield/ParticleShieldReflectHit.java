package blockfighter.client.entities.particles.skills.shield;

import blockfighter.client.Core;
import blockfighter.client.entities.particles.Particle;
import blockfighter.shared.Globals;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class ParticleShieldReflectHit extends Particle {

    private double speedX, speedY;

    public ParticleShieldReflectHit(final int x, final int y) {
        this.duration = 0;
        for (int i = 0; i < 20; i++) {
            Core.getLogicModule().getScreen().addParticle(new ParticleShieldReflectHit(x, y, i));
        }
    }

    public ParticleShieldReflectHit(final int x, final int y, final int index) {
        super(x, y);
        this.frame = 0;
        this.frameDuration = 25;
        this.duration = 300;
        this.x = x;
        this.y = y - 75;
        double targetX = this.x + 300f * Math.cos(2 * Math.PI * index / 20f);

        double targetY = this.y + 300f * Math.sin(2 * Math.PI * index / 20f);
        double numberOfTicks = this.duration / 25f;
        this.speedX = (targetX - this.x) / numberOfTicks;
        this.speedY = (targetY - this.y) / numberOfTicks;
    }

    @Override
    public void update() {
        super.update();
        if (Globals.nsToMs(Core.getLogicModule().getTime() - this.lastFrameTime) >= this.frameDuration) {
            this.x += this.speedX;
            this.y += this.speedY;
            if (Globals.Particles.SHIELD_REFLECT_HIT.getSprite() != null && this.frame < Globals.Particles.SHIELD_REFLECT_HIT.getSprite().length - 1) {
                this.frame++;
            }
            this.lastFrameTime = Core.getLogicModule().getTime();
        }
    }

    @Override
    public void draw(final Graphics2D g) {
        if (Globals.Particles.SHIELD_REFLECT_HIT.getSprite() == null) {
            return;
        }
        if (this.frame >= Globals.Particles.SHIELD_REFLECT_HIT.getSprite().length) {
            return;
        }
        final BufferedImage sprite = Globals.Particles.SHIELD_REFLECT_HIT.getSprite()[this.frame];
        final int drawSrcX = this.x - sprite.getWidth() / 2;
        final int drawSrcY = this.y - sprite.getHeight() / 2;
        final int drawDscY = drawSrcY + sprite.getHeight();
        final int drawDscX = drawSrcX + sprite.getWidth();
        g.drawImage(sprite, drawSrcX, drawSrcY, drawDscX, drawDscY, 0, 0, sprite.getWidth(), sprite.getHeight(), null);
    }
}
