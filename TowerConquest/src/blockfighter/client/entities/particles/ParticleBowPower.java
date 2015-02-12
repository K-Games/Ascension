package blockfighter.client.entities.particles;

import blockfighter.client.Globals;
import blockfighter.client.screen.ScreenIngame;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class ParticleBowPower extends Particle {

    public ParticleBowPower(int k, int x, int y, byte f) {
        super(k, x, y, f);
        frame = 0;
        frameDuration = 25;
        duration = 600;
    }

    @Override
    public void update() {
        super.update();
        frameDuration -= Globals.LOGIC_UPDATE / 1000000;
        if (duration > 50) {
            for (int i = 0; i < 2; i++) {
                ParticleBowPowerParticle b = new ParticleBowPowerParticle(((ScreenIngame) logic.getScreen()).getNextParticleKey(), x + ((facing == Globals.RIGHT) ? 0 : 700), y, facing);
                ((ScreenIngame) logic.getScreen()).addParticle(b);
            }
        }
        if (frameDuration <= 0) {
            frameDuration = 25;
            if (frame < PARTICLE_SPRITE[Globals.PARTICLE_BOW_POWER].length - 1) {
                frame++;
            }
        }
    }

    @Override
    public void draw(Graphics2D g) {
        if (PARTICLE_SPRITE[Globals.PARTICLE_BOW_POWER] == null) {
            return;
        }
        if (frame >= PARTICLE_SPRITE[Globals.PARTICLE_BOW_POWER].length) {
            return;
        }
        BufferedImage sprite = PARTICLE_SPRITE[Globals.PARTICLE_BOW_POWER][frame];
        int drawSrcX = x + ((facing == Globals.RIGHT) ? 0 : 700);
        int drawSrcY = y;
        int drawDscY = drawSrcY + sprite.getHeight();
        int drawDscX = x + ((facing == Globals.RIGHT) ? 700 : 0);
        g.drawImage(sprite, drawSrcX, drawSrcY, drawDscX, drawDscY, 0, 0, sprite.getWidth(), sprite.getHeight(), null);
    }
}
