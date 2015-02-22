package blockfighter.client.entities.particles;

import blockfighter.client.Globals;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class ParticleBowVolleyBuffParticle extends Particle {

    double xDouble, xSpeed;

    public ParticleBowVolleyBuffParticle(int k, int x, int y, byte f) {
        super(k, x, y, f);
        this.x += rng.nextInt(100) - 60;
        this.y -= rng.nextInt(40) + 58;
        xDouble = this.x;
        xSpeed = rng.nextInt(10) * .75 -3.75;
        frame = 0;
        frameDuration = 50;
        duration = 300;
    }

    @Override
    public void update() {
        super.update();
        frameDuration -= Globals.LOGIC_UPDATE / 1000000;
        xDouble += xSpeed;
        x = (int) xDouble;
        y -= 9;
        if (frameDuration <= 0) {
            frameDuration = 50;
            if (frame < PARTICLE_SPRITE[Globals.PARTICLE_SHIELD_DASHBUFF].length - 1) {
                frame++;
            }
        }
    }

    @Override
    public void draw(Graphics2D g) {
        if (PARTICLE_SPRITE[Globals.PARTICLE_BOW_VOLLEYBUFF] == null) {
            return;
        }
        if (frame >= PARTICLE_SPRITE[Globals.PARTICLE_BOW_VOLLEYBUFF].length) {
            return;
        }
        BufferedImage sprite = PARTICLE_SPRITE[Globals.PARTICLE_BOW_VOLLEYBUFF][frame];
        g.drawImage(sprite, x, y, null);
    }
}
