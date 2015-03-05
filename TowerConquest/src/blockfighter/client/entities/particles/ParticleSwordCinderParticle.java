package blockfighter.client.entities.particles;

import blockfighter.client.Globals;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class ParticleSwordCinderParticle extends Particle {

    public ParticleSwordCinderParticle(int k, int x, int y, byte f) {
        super(k, x, y, f);
        this.x += Globals.rng(100) + ((facing == Globals.RIGHT) ? 60 : 0);
        this.y += Globals.rng(200) + 20;
        frame = 0;
        frameDuration = 25;
        duration = 500;
    }

    @Override
    public void update() {
        super.update();
        frameDuration -= Globals.LOGIC_UPDATE / 1000000;
        y -= 3;
        if (frameDuration <= 0) {
            frameDuration = 25;
            if (frame < PARTICLE_SPRITE[Globals.PARTICLE_BURN].length - 1) {
                frame++;
            }
        }
    }

    @Override
    public void draw(Graphics2D g) {
        if (PARTICLE_SPRITE[Globals.PARTICLE_BURN] == null) {
            return;
        }
        if (frame >= PARTICLE_SPRITE[Globals.PARTICLE_BURN].length) {
            return;
        }
        BufferedImage sprite = PARTICLE_SPRITE[Globals.PARTICLE_BURN][frame];
        g.drawImage(sprite, x, y, null);
    }
}
