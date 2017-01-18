package blockfighter.client.entities.particles;

import blockfighter.shared.Globals;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class ParticleShieldReflectBuff extends Particle {

    double pX, speedX;

    public ParticleShieldReflectBuff(final int x, final int y, final byte f) {
        super(x, y, f);
        this.x += Globals.rng(10) * 5 - 60;
        this.y += -Globals.rng(80) - 60;
        this.pX = this.x;
        this.speedX = (Globals.rng(10) - 5) * 1.5D;

        this.frame = Globals.rng(5);
        this.frameDuration = 25;
        this.duration = 400;
    }

    @Override
    public void update() {
        super.update();
        this.y -= 8;
        this.pX += this.speedX;
        this.x = (int) this.pX;
        if (Globals.nsToMs(logic.getTime() - this.lastFrameTime) >= this.frameDuration) {
            if (Globals.Particles.SHIELD_REFLECT_BUFF.getSprite() != null && this.frame < Globals.Particles.SHIELD_REFLECT_BUFF.getSprite().length) {
                this.frame++;
            }
            this.lastFrameTime = logic.getTime();
        }
    }

    @Override
    public void draw(final Graphics2D g) {
        if (Globals.Particles.SHIELD_REFLECT_BUFF.getSprite() == null) {
            return;
        }
        if (this.frame >= Globals.Particles.SHIELD_REFLECT_BUFF.getSprite().length) {
            return;
        }
        final BufferedImage sprite = Globals.Particles.SHIELD_REFLECT_BUFF.getSprite()[this.frame];
        g.drawImage(sprite, this.x, this.y, null);
    }
}
