package blockfighter.client.entities.particles;

import blockfighter.client.Globals;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class ParticleBowVolleyArrow extends Particle {

    public ParticleBowVolleyArrow( int k, int x, int y, byte f) {
        super( k, x, y, f);
        frame = 0;
        frameDuration = 50;
        duration = 300;
    }

    @Override
    public void update() {
        super.update();
        frameDuration -= Globals.LOGIC_UPDATE / 1000000;
        if (frameDuration <= 0) {
            frameDuration = 50;
            if (frame < PARTICLE_SPRITE[Globals.PARTICLE_BOW_VOLLEYARROW].length-1) {
                frame++;
            }
        }
    }

    @Override
    public void draw(Graphics2D g) {
        if (PARTICLE_SPRITE[Globals.PARTICLE_BOW_VOLLEYARROW] == null) {
            loadParticles();
        }
        if (frame >= PARTICLE_SPRITE[Globals.PARTICLE_BOW_VOLLEYARROW].length) {
            return;
        }
        BufferedImage sprite = PARTICLE_SPRITE[Globals.PARTICLE_BOW_VOLLEYARROW][frame];
        int drawSrcX = x + ((facing == Globals.RIGHT) ? 0 : sprite.getWidth());
        int drawSrcY = y;
        int drawDscY = drawSrcY + sprite.getHeight();
        int drawDscX = x + ((facing == Globals.RIGHT) ? sprite.getWidth() : 0);
        g.drawImage(sprite, drawSrcX, drawSrcY, drawDscX, drawDscY, 0, 0, sprite.getWidth(), sprite.getHeight(), null);
    }
}
