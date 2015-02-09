package blockfighter.client.entities.particles;

import blockfighter.client.Globals;
import blockfighter.client.LogicModule;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class ParticleBowPowerParticle extends Particle {

    public ParticleBowPowerParticle( int k, int x, int y, byte f) {
        super( k, x, y, f);
        if (facing == Globals.RIGHT) {
            this.x += rng.nextInt(450);
        } else {
            this.x -= rng.nextInt(450)-25;
        }
        this.y += rng.nextInt(80) + 25;
        frame = 0;
        frameDuration = 25;
        duration = 250;
    }

    @Override
    public void update() {
        super.update();
        frameDuration -= Globals.LOGIC_UPDATE / 1000000;
        x += (facing == Globals.RIGHT) ? 20 : -20;
        if (frameDuration <= 0) {
            frameDuration = 25;
            if (frame < PARTICLE_SPRITE[Globals.PARTICLE_BOW_POWERPARTICLE].length-1) {
                frame++;
            }
        }
    }

    @Override
    public void draw(Graphics2D g) {
        if (PARTICLE_SPRITE[Globals.PARTICLE_BOW_POWERPARTICLE] == null) {
            loadParticles();
        }
        if (frame >= PARTICLE_SPRITE[Globals.PARTICLE_BOW_POWERPARTICLE].length) {
            return;
        }
        BufferedImage sprite = PARTICLE_SPRITE[Globals.PARTICLE_BOW_POWERPARTICLE][frame];
        g.drawImage(sprite, x, y, null);
    }
}
