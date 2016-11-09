package blockfighter.client.entities.mob.boss.Lightning;

import blockfighter.shared.Globals;
import blockfighter.client.entities.particles.Particle;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class ParticleBoltParticle extends Particle {

    double dX, dY, speedX, speedY;

    public ParticleBoltParticle(final int x, final int y) {
        super(x, y, Globals.RIGHT);
        this.dX = x;
        this.dY = y;
        this.speedX = (Globals.rng(20) - 10) * 1.5;
        this.speedY = (Globals.rng(10) + 10) * 2;
        this.frame = 0;
        this.frameDuration = 50;
        this.duration = 300;
    }

    @Override
    public void update() {
        super.update();
        this.dX += this.speedX;
        this.dY -= this.speedY;
        this.x = (int) this.dX;
        this.y = (int) this.dY;
        if (Globals.nsToMs(logic.getTime() - this.lastFrameTime) >= this.frameDuration) {
            if (PARTICLE_SPRITE != null && this.frame < PARTICLE_SPRITE[Globals.PARTICLE_SWORD_SLASHBUFF].length - 1) {
                this.frame++;
            }
            this.lastFrameTime = logic.getTime();
        }
    }

    @Override
    public void draw(final Graphics2D g) {
        if (PARTICLE_SPRITE[Globals.PARTICLE_SWORD_SLASHBUFF] == null) {
            return;
        }
        if (this.frame >= PARTICLE_SPRITE[Globals.PARTICLE_SWORD_SLASHBUFF].length) {
            return;
        }
        final BufferedImage sprite = PARTICLE_SPRITE[Globals.PARTICLE_SWORD_SLASHBUFF][this.frame];
        g.drawImage(sprite, this.x, this.y, null);
    }
}
