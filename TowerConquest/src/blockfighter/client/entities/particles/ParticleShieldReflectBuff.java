package blockfighter.client.entities.particles;

import blockfighter.client.Globals;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class ParticleShieldReflectBuff extends Particle {

    double pX, speedX;

    public ParticleShieldReflectBuff(final int k, final int x, final int y, final byte f) {
        super(k, x, y, f);
        this.x += Globals.rng(10) * 10 - 80;
        this.y += -Globals.rng(100) - 60;
        this.pX = this.x;
        this.speedX = (Globals.rng(10) - 5) * 1.5D;

        this.frame = Globals.rng(5);
        this.frameDuration = 25;
        this.duration = 400;
    }

    @Override
    public void update() {
        super.update();
        this.y -= 10;
        this.pX += this.speedX;
        this.x = (int) this.pX;
        if (Globals.nsToMs(logic.getTime() - this.lastFrameTime) >= this.frameDuration) {
            if (this.frame < PARTICLE_SPRITE[Globals.PARTICLE_SHIELD_REFLECTBUFF].length) {
                this.frame++;
            }
            this.lastFrameTime = logic.getTime();
        }
    }

    @Override
    public void draw(final Graphics2D g) {
        if (PARTICLE_SPRITE[Globals.PARTICLE_SHIELD_REFLECTBUFF] == null) {
            return;
        }
        if (this.frame >= PARTICLE_SPRITE[Globals.PARTICLE_SHIELD_REFLECTBUFF].length) {
            return;
        }
        final BufferedImage sprite = PARTICLE_SPRITE[Globals.PARTICLE_SHIELD_REFLECTBUFF][this.frame];
        g.drawImage(sprite, this.x, this.y, null);
    }
}
