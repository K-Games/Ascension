package blockfighter.client.entities.particles.skills.passive;

import blockfighter.client.entities.particles.Particle;
import blockfighter.shared.Globals;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class ParticlePassiveShadowAttack extends Particle {

    public ParticlePassiveShadowAttack(final int x, final int y) {
        super(x, y, Globals.RIGHT);
        this.frame = 0;
        this.y -= Globals.rng(15);
        this.frameDuration = 50;
        this.duration = 350;
        this.particleData = Globals.Particles.PASSIVE_SHADOWATTACK;
    }

    @Override
    public void draw(final Graphics2D g) {
        if (!this.spriteFrameExists()) {
            return;
        }
        final BufferedImage sprite = this.particleData.getSprites()[this.frame];
        draw(g, -sprite.getWidth() / 2, 0, false);
    }
}
