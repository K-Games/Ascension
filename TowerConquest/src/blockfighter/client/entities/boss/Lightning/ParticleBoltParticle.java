package blockfighter.client.entities.boss.Lightning;

import blockfighter.client.Globals;
import blockfighter.client.entities.particles.Particle;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class ParticleBoltParticle extends Particle {

    double dX, dY, speedX, speedY;

    public ParticleBoltParticle(int k, int x, int y) {
        super(k, x, y, Globals.RIGHT);
        dX = x;
        dY = y;
        speedX = (rng.nextInt(20) - 10) * 1.5;
        speedY = (rng.nextInt(10) + 10) * 2;
        frame = 0;
        frameDuration = 50;
        duration = 300;
    }

    @Override
    public void update() {
        super.update();
        frameDuration -= Globals.LOGIC_UPDATE / 1000000;
        dX += speedX;
        dY -= speedY;
        x = (int) dX;
        y = (int) dY;
        if (frameDuration <= 0) {
            frameDuration = 50;
            if (frame < PARTICLE_SPRITE[Globals.PARTICLE_SWORD_SLASHBUFF].length - 1) {
                frame++;
            }
        }
    }

    @Override
    public void draw(Graphics2D g) {
        if (PARTICLE_SPRITE[Globals.PARTICLE_SWORD_SLASHBUFF] == null) {
            return;
        }
        if (frame >= PARTICLE_SPRITE[Globals.PARTICLE_SWORD_SLASHBUFF].length) {
            return;
        }
        BufferedImage sprite = PARTICLE_SPRITE[Globals.PARTICLE_SWORD_SLASHBUFF][frame];
        g.drawImage(sprite, x, y, null);
    }
}
