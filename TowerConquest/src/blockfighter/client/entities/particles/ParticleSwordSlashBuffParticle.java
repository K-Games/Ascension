package blockfighter.client.entities.particles;

import blockfighter.client.Globals;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class ParticleSwordSlashBuffParticle extends Particle {

    public ParticleSwordSlashBuffParticle(int k, int x, int y, byte f) {
        super(k, x, y, f);
        this.x += Globals.rng(100) - 60;
        this.y -= Globals.rng(40);
        frame = 0;
        frameDuration = 50;
        duration = 300;
    }

    @Override
    public void update() {
        super.update();
        frameDuration -= Globals.LOGIC_UPDATE / 1000000;
        y -= 9;
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
