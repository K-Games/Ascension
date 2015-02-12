package blockfighter.client.entities.particles;

import blockfighter.client.Globals;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class ParticleSwordTauntAuraParticle extends Particle {

    public ParticleSwordTauntAuraParticle(int k, int x, int y, byte f) {
        super(k, x, y, f);
        this.x += rng.nextInt(200) - 100;
        this.y -= rng.nextInt(40);
        frame = 0;
        frameDuration = 50;
        duration = 500;
    }

    @Override
    public void update() {
        super.update();
        frameDuration -= Globals.LOGIC_UPDATE / 1000000;
        y -= 9;
        if (frameDuration <= 0) {
            frameDuration = 50;
            if (frame < PARTICLE_SPRITE[Globals.PARTICLE_SWORD_TAUNTAURA2].length - 1) {
                frame++;
            }
        }
    }

    @Override
    public void draw(Graphics2D g) {
        if (PARTICLE_SPRITE[Globals.PARTICLE_SWORD_TAUNTAURA2] == null) {
            return;
        }
        if (frame >= PARTICLE_SPRITE[Globals.PARTICLE_SWORD_TAUNTAURA2].length) {
            return;
        }
        BufferedImage sprite = PARTICLE_SPRITE[Globals.PARTICLE_SWORD_TAUNTAURA2][frame];
        g.drawImage(sprite, x, y, null);
    }
}
