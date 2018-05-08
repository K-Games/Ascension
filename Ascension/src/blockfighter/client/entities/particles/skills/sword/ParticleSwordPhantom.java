package blockfighter.client.entities.particles.skills.sword;

import blockfighter.client.entities.particles.Particle;
import blockfighter.shared.Globals;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class ParticleSwordPhantom extends Particle {

    public ParticleSwordPhantom(final int x, final int y, final byte f) {
        super(x, y, f);
        this.frame = 0;
        this.frameDuration = 25;
        this.duration = 200;
        this.particleData = Globals.Particles.SWORD_PHANTOM;
    }

    @Override
    public void draw(final Graphics2D g) {
        if (this.particleData.getSprites() == null || this.frame >= this.particleData.getSprites().length) {
            return;
        }
        final BufferedImage sprite = this.particleData.getSprites()[this.frame];
        draw(g, sprite.getWidth() / -2, 0);
    }
}
