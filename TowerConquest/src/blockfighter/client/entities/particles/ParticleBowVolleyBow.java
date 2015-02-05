package blockfighter.client.entities.particles;

import blockfighter.client.Globals;
import blockfighter.client.LogicModule;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class ParticleBowVolleyBow extends Particle {

    public ParticleBowVolleyBow(LogicModule l, int k, int x, int y, byte f) {
        super(l, k, x, y, f);
        frame = 0;
        frameDuration = 25;
        duration = 125;
    }

    @Override
    public void update() {
        super.update();
        frameDuration -= Globals.LOGIC_UPDATE / 1000000;
    }

    @Override
    public void draw(Graphics2D g) {
        if (PARTICLE_SPRITE[Globals.PARTICLE_BOW_VOLLEYBOW] == null) {
            loadParticles();
        }
        if (frame >= PARTICLE_SPRITE[Globals.PARTICLE_BOW_VOLLEYBOW].length) {
            return;
        }
        BufferedImage sprite = PARTICLE_SPRITE[Globals.PARTICLE_BOW_VOLLEYBOW][frame];
        int drawSrcX = x + ((facing == Globals.RIGHT) ? -15 : 15);
        int drawSrcY = y - 255;
        int drawDscY = drawSrcY + sprite.getHeight();
        int drawDscX = x + ((facing == Globals.RIGHT) ? sprite.getWidth() - 15 : -sprite.getWidth() + 15);
        g.drawImage(sprite, drawSrcX, drawSrcY, drawDscX, drawDscY, 0, 0, sprite.getWidth(), sprite.getHeight(), null);
    }
}
