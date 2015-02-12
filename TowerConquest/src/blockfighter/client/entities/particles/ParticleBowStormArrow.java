package blockfighter.client.entities.particles;

import blockfighter.client.Globals;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class ParticleBowStormArrow extends Particle {

    public ParticleBowStormArrow(int k, int x, int y, byte f) {
        super(k, x, y, f);
        this.x += rng.nextInt(30) * 20 - ((facing == Globals.RIGHT) ? 90 : 150);
        this.y += rng.nextInt(25) * 15 - 100;
        frame = 0;
        frameDuration = 25;
        duration = 500;
    }

    @Override
    public void update() {
        super.update();
        frameDuration -= Globals.LOGIC_UPDATE / 1000000;
        if (frameDuration <= 0) {
            frameDuration = 25;
            if (frame < PARTICLE_SPRITE[Globals.PARTICLE_BOW_STORM].length - 1) {
                frame++;
            }
        }
    }

    @Override
    public void draw(Graphics2D g) {
        if (PARTICLE_SPRITE[Globals.PARTICLE_BOW_STORM] == null) {
            return;
        }
        if (frame >= PARTICLE_SPRITE[Globals.PARTICLE_BOW_STORM].length) {
            return;
        }
        BufferedImage sprite = PARTICLE_SPRITE[Globals.PARTICLE_BOW_STORM][frame];
        int drawSrcX = x + ((facing == Globals.RIGHT) ? 0 : sprite.getWidth());
        int drawSrcY = y;
        int drawDscY = drawSrcY + sprite.getHeight();
        int drawDscX = x + ((facing == Globals.RIGHT) ? sprite.getWidth() : 0);
        g.drawImage(sprite, drawSrcX, drawSrcY, drawDscX, drawDscY, 0, 0, sprite.getWidth(), sprite.getHeight(), null);
    }
}
