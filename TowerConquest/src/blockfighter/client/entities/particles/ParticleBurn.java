package blockfighter.client.entities.particles;

import blockfighter.client.Globals;
import blockfighter.client.LogicModule;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class ParticleBurn extends Particle {

    public ParticleBurn(LogicModule l, int k, int x, int y, byte f) {
        super(l, k, x, y, f);
        this.x += rng.nextInt(100) + ((facing == Globals.RIGHT) ? 60 : 0);
        this.y += rng.nextInt(200) + 20;
        frame = 0;
        frameDuration = 25;
        duration = 500;
    }

    @Override
    public void update() {
        super.update();
        frameDuration -= Globals.LOGIC_UPDATE / 1000000;
        if (frameDuration <= 0 && frame < PARTICLE_SPRITE[Globals.PARTICLE_BURN].length) {
            frameDuration = 25;
            frame++;

        }
    }

    @Override
    public void draw(Graphics2D g) {
        if (PARTICLE_SPRITE[Globals.PARTICLE_BURN] == null) {
            loadParticles();
        }
        if (frame >= PARTICLE_SPRITE[Globals.PARTICLE_BURN].length) {
            return;
        }
        BufferedImage sprite = PARTICLE_SPRITE[Globals.PARTICLE_BURN][frame];
        g.drawImage(sprite, x, y, null);
    }
}
