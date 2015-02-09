package blockfighter.client.entities.particles;

import blockfighter.client.Globals;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class ParticleBowPowerCharge extends Particle {

    public ParticleBowPowerCharge(int k, int x, int y, byte f) {
        super(k, x, y, f);
        frame = 0;
        frameDuration = 25;
        duration = 600;
    }

    @Override
    public void update() {
        super.update();
        frameDuration -= Globals.LOGIC_UPDATE / 1000000;
        if (frameDuration <= 0) {
            frameDuration = 25;
            if (frame < PARTICLE_SPRITE[Globals.PARTICLE_BOW_POWERCHARGE].length - 1) {
                frame++;
            }
        }
    }

    @Override
    public void draw(Graphics2D g) {
        if (PARTICLE_SPRITE[Globals.PARTICLE_BOW_POWERCHARGE] == null) {
            loadParticles();
        }
        if (frame >= PARTICLE_SPRITE[Globals.PARTICLE_BOW_POWERCHARGE].length) {
            return;
        }
        BufferedImage sprite = PARTICLE_SPRITE[Globals.PARTICLE_BOW_POWERCHARGE][frame];
        int drawSrcX = x - sprite.getWidth() / 2;
        int drawSrcY = y;
        int drawDscY = drawSrcY + sprite.getHeight();
        int drawDscX = x + sprite.getWidth() / 2;
        g.drawImage(sprite, drawSrcX, drawSrcY, drawDscX, drawDscY, 0, 0, sprite.getWidth(), sprite.getHeight(), null);
        g.setColor(Color.WHITE);
    }
}
