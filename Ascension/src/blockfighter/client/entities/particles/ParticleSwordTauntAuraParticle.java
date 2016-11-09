package blockfighter.client.entities.particles;

import blockfighter.shared.Globals;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class ParticleSwordTauntAuraParticle extends Particle {

    public ParticleSwordTauntAuraParticle(final int x, final int y, final byte f) {
        super(x, y, f);
        this.x += Globals.rng(50) - 35;
        this.y -= Globals.rng(40) + 15;
        this.frame = 0;
        this.frameDuration = 50;
        this.duration = 500;
    }

    @Override
    public void update() {
        super.update();

        this.y -= 6;
        if (Globals.nsToMs(logic.getTime() - this.lastFrameTime) >= this.frameDuration) {
            this.frameDuration = 50;
            if (PARTICLE_SPRITE != null && this.frame < PARTICLE_SPRITE[Globals.PARTICLE_SWORD_TAUNTAURA2].length - 1) {
                this.frame++;
            }
            this.lastFrameTime = logic.getTime();
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
