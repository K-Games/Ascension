package blockfighter.client.entities.particles;

import blockfighter.client.Globals;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class ParticlePassiveBarrier extends Particle {

    public ParticlePassiveBarrier(int k, int x, int y) {
        super(k, x, y, Globals.RIGHT);
        frame = 0;
        frameDuration = 25;
        duration = 300;
    }

    @Override
    public void update() {
        super.update();
        frameDuration -= Globals.LOGIC_UPDATE / 1000000;
        if (frameDuration <= 0) {
            frameDuration = 25;
            if (frame < PARTICLE_SPRITE[Globals.PARTICLE_PASSIVE_BARRIER].length - 1) {
                frame++;
            }
        }
    }

    @Override
    public void draw(Graphics2D g) {
        if (PARTICLE_SPRITE[Globals.PARTICLE_PASSIVE_BARRIER] == null) {
            return;
        }
        if (frame >= PARTICLE_SPRITE[Globals.PARTICLE_PASSIVE_BARRIER].length) {
            return;
        }
        BufferedImage sprite = PARTICLE_SPRITE[Globals.PARTICLE_PASSIVE_BARRIER][frame];
        int drawSrcX = x - sprite.getWidth() / 2;
        int drawSrcY = y - sprite.getHeight() / 2 - 20;
        int drawDscY = drawSrcY + sprite.getHeight();
        int drawDscX = drawSrcX + sprite.getWidth();
        g.drawImage(sprite, drawSrcX, drawSrcY, drawDscX, drawDscY, 0, 0, sprite.getWidth(), sprite.getHeight(), null);
        g.setColor(Color.WHITE);
    }
}
