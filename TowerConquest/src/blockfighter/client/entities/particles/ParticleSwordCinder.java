package blockfighter.client.entities.particles;

import blockfighter.client.Globals;
import blockfighter.client.screen.ScreenIngame;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class ParticleSwordCinder extends Particle {

    public ParticleSwordCinder(int k, int x, int y, byte f) {
        super(k, x, y, f);
        frame = 0;
        frameDuration = 50;
        duration = 400;
    }

    @Override
    public void update() {
        super.update();
        frameDuration -= Globals.LOGIC_UPDATE / 1000000;
        if (duration > 100) {
            for (int i = 0; i < 2; i++) {
                ParticleBurn b = new ParticleBurn(((ScreenIngame) logic.getScreen()).getNextParticleKey(), x, y, facing);
                ((ScreenIngame) logic.getScreen()).addParticle(b);
            }
        }
        if (frameDuration <= 0) {
            frameDuration = 50;
            if (frame < PARTICLE_SPRITE[Globals.PARTICLE_SWORD_CINDER].length - 1) {
                frame++;
            }
        }
    }

    @Override
    public void draw(Graphics2D g) {
        if (PARTICLE_SPRITE[Globals.PARTICLE_SWORD_CINDER] == null) {
            loadParticles();
        }
        if (frame >= PARTICLE_SPRITE[Globals.PARTICLE_SWORD_CINDER].length) {
            return;
        }
        BufferedImage sprite = PARTICLE_SPRITE[Globals.PARTICLE_SWORD_CINDER][frame];
        int drawSrcX = x + ((facing == Globals.RIGHT) ? -60 : sprite.getWidth() - 45);
        int drawSrcY = y - 30;
        int drawDscY = drawSrcY + sprite.getHeight();
        int drawDscX = x + ((facing == Globals.RIGHT) ? sprite.getWidth() - 60 : -45);
        g.drawImage(sprite, drawSrcX, drawSrcY, drawDscX, drawDscY, 0, 0, sprite.getWidth(), sprite.getHeight(), null);
    }
}
