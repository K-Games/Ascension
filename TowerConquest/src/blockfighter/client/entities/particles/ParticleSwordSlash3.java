package blockfighter.client.entities.particles;

import blockfighter.client.Globals;
import blockfighter.client.LogicModule;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class ParticleSwordSlash3 extends Particle {

    public ParticleSwordSlash3(LogicModule l, int k, int x, int y, byte f) {
        super(l, k, x, y, f);
        frame = 0;
        frameDuration = 20;
        duration = 200;
    }

    @Override
    public void update() {
        super.update();
        frameDuration -= Globals.LOGIC_UPDATE / 1000000;
        if (frameDuration <= 0 && frame < PARTICLE_SPRITE[Globals.PARTICLE_SWORD_SLASH3].length) {
            frameDuration = 20;
            frame++;
        }
    }

    @Override
    public void draw(Graphics2D g) {
        if (PARTICLE_SPRITE[Globals.PARTICLE_SWORD_SLASH3] == null) {
            loadParticles();
        }
        if (frame >= PARTICLE_SPRITE[Globals.PARTICLE_SWORD_SLASH3].length) {
            return;
        }
        BufferedImage sprite = PARTICLE_SPRITE[Globals.PARTICLE_SWORD_SLASH3][frame];
        int drawSrcX = x + ((facing == Globals.RIGHT) ? -30 : sprite.getWidth());
        int drawSrcY = y;
        int drawDscY = drawSrcY + sprite.getHeight();
        int drawDscX = x + ((facing == Globals.RIGHT) ? sprite.getWidth() - 30 : 0);
        g.drawImage(sprite, drawSrcX, drawSrcY, drawDscX, drawDscY, 0, 0, sprite.getWidth(), sprite.getHeight(), null);
    }
}
