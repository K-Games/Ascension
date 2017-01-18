package blockfighter.client.entities.particles.skill;

import blockfighter.client.entities.particles.Particle;
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
            if (Globals.Particles.SWORD_TAUNT_AURA_PARTICLE.getSprite() != null && this.frame < Globals.Particles.SWORD_TAUNT_AURA_PARTICLE.getSprite().length - 1) {
                this.frame++;
            }
            this.lastFrameTime = logic.getTime();
        }
    }

    @Override
    public void draw(final Graphics2D g) {
        if (Globals.Particles.SWORD_TAUNT_AURA_PARTICLE.getSprite() == null) {
            return;
        }
        if (this.frame >= Globals.Particles.SWORD_TAUNT_AURA_PARTICLE.getSprite().length) {
            return;
        }
        final BufferedImage sprite = Globals.Particles.SWORD_TAUNT_AURA_PARTICLE.getSprite()[this.frame];
        g.drawImage(sprite, this.x, this.y, null);
    }
}
