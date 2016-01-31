package blockfighter.client.entities.particles;

import blockfighter.client.Globals;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class ParticleSwordTauntAuraParticle extends Particle {

    public ParticleSwordTauntAuraParticle(final int k, final int x, final int y, final byte f) {
        super(k, x, y, f);
        this.x += Globals.rng(100) - 60;
        this.y -= Globals.rng(40);
        this.frame = 0;
        this.frameDuration = 50;
        this.duration = 500;
    }

    @Override
    public void update() {
        super.update();
        this.frameDuration -= Globals.LOGIC_UPDATE / 1000000;
        this.y -= 9;
        if (this.frameDuration <= 0) {
            this.frameDuration = 50;
            if (this.frame < PARTICLE_SPRITE[Globals.PARTICLE_SWORD_TAUNTAURA2].length - 1) {
                this.frame++;
            }
        }
    }

    @Override
    public void draw(final Graphics2D g) {
        if (PARTICLE_SPRITE[Globals.PARTICLE_SWORD_TAUNTAURA2] == null) {
            return;
        }
        if (this.frame >= PARTICLE_SPRITE[Globals.PARTICLE_SWORD_TAUNTAURA2].length) {
            return;
        }
        final BufferedImage sprite = PARTICLE_SPRITE[Globals.PARTICLE_SWORD_TAUNTAURA2][this.frame];
        g.drawImage(sprite, this.x, this.y, null);
    }
}
