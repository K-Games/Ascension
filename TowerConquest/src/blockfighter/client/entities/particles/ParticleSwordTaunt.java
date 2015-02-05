package blockfighter.client.entities.particles;

import blockfighter.client.Globals;
import blockfighter.client.LogicModule;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class ParticleSwordTaunt extends Particle {

    public ParticleSwordTaunt(LogicModule l, int k, int x, int y, byte f) {
        super(l, k, x, y, f);
        frame = 0;
        frameDuration = 25;
        duration = 400;
    }

    @Override
    public void update() {
        super.update();
        frameDuration -= Globals.LOGIC_UPDATE / 1000000;
        if (frameDuration <= 0) {
            frameDuration = 25;
            if (frame < PARTICLE_SPRITE[Globals.PARTICLE_SWORD_TAUNT].length-1) {
                frame++;
            }
        }
    }

    @Override
    public void draw(Graphics2D g) {
        if (PARTICLE_SPRITE[Globals.PARTICLE_SWORD_TAUNT] == null) {
            loadParticles();
        }
        if (frame >= PARTICLE_SPRITE[Globals.PARTICLE_SWORD_TAUNT].length) {
            return;
        }
        BufferedImage sprite = PARTICLE_SPRITE[Globals.PARTICLE_SWORD_TAUNT][frame];
        int drawSrcX = x + ((facing == Globals.RIGHT) ? 0 : sprite.getWidth());
        int drawSrcY = y;
        int drawDscY = drawSrcY + sprite.getHeight();
        int drawDscX = x + ((facing == Globals.RIGHT) ? sprite.getWidth() : 0);
        g.drawImage(sprite, drawSrcX, drawSrcY, drawDscX, drawDscY, 0, 0, sprite.getWidth(), sprite.getHeight(), null);
    }
}
