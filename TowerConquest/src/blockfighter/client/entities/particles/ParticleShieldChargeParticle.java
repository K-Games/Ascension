package blockfighter.client.entities.particles;

import blockfighter.client.Globals;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class ParticleShieldChargeParticle extends Particle {

    public ParticleShieldChargeParticle(int k, int x, int y, byte f) {
        super(k, x, y, f);
        frame = 0;
        frameDuration = 50;
        duration = 400;
        this.y += rng.nextInt(11) * 15;
        if (facing == Globals.RIGHT) x += 428;
    }

    @Override
    public void update() {
        super.update();
        frameDuration -= Globals.LOGIC_UPDATE / 1000000;
        if (frameDuration <= 0) {
            frameDuration = 50;
            if (frame < PARTICLE_SPRITE[Globals.PARTICLE_SHIELD_CHARGEPARTICLE].length) {
                frame++;
            }
        }
    }

    @Override
    public void draw(Graphics2D g) {
        if (PARTICLE_SPRITE[Globals.PARTICLE_SHIELD_CHARGEPARTICLE] == null) {
            return;
        }
        if (frame >= PARTICLE_SPRITE[Globals.PARTICLE_SHIELD_CHARGEPARTICLE].length) {
            return;
        }
        BufferedImage sprite = PARTICLE_SPRITE[Globals.PARTICLE_SHIELD_CHARGEPARTICLE][frame];
        int drawSrcX = x;
        int drawSrcY = y;
        int drawDscY = drawSrcY + sprite.getHeight();
        int drawDscX = x + sprite.getWidth() ;
        g.drawImage(sprite, drawSrcX, drawSrcY, drawDscX, drawDscY, 0, 0, sprite.getWidth(), sprite.getHeight(), null);
    }
}
