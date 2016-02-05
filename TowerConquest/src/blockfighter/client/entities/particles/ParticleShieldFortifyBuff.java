package blockfighter.client.entities.particles;

import blockfighter.client.Globals;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class ParticleShieldFortifyBuff extends Particle {

    double pX, speedX;

    public ParticleShieldFortifyBuff(final int k, final int x, final int y, final byte f) {
        super(k, x, y, f);
        this.x += Globals.rng(10) * 10 - 60;
        this.y += -30 - Globals.rng(100);
        this.frameDuration = 25;
        this.duration = 200;
    }

    @Override
    public void update() {
        super.update();

        this.y -= 7;
        if (Globals.nsToMs(logic.getTime() - this.lastFrameTime) >= this.frameDuration) {
            if (this.frame < PARTICLE_SPRITE[Globals.PARTICLE_SHIELD_FORTIFYBUFF].length) {
                this.frame++;
            }
            this.lastFrameTime = logic.getTime();
        }
    }

    @Override
    public void draw(final Graphics2D g) {
        if (PARTICLE_SPRITE[Globals.PARTICLE_SHIELD_FORTIFYBUFF] == null) {
            return;
        }
        if (this.frame >= PARTICLE_SPRITE[Globals.PARTICLE_SHIELD_FORTIFYBUFF].length) {
            return;
        }
        final BufferedImage sprite = PARTICLE_SPRITE[Globals.PARTICLE_SHIELD_FORTIFYBUFF][this.frame];
        g.drawImage(sprite, this.x, this.y, null);
    }
}
