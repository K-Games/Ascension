package blockfighter.client.entities.particles;

import blockfighter.client.Globals;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class ParticleShieldFortifyBuff extends Particle {

    double pX, speedX;

    public ParticleShieldFortifyBuff(int k, int x, int y, byte f) {
        super(k, x, y, f);
        this.x += Globals.rng(10) * 10 - 60;
        this.y += -30 - Globals.rng(100);
        frameDuration = 25;
        duration = 200;
    }

    @Override
    public void update() {
        super.update();
        frameDuration -= Globals.LOGIC_UPDATE / 1000000;
        y -= 7;
        if (frameDuration <= 0) {
            frameDuration = 25;
            if (frame < PARTICLE_SPRITE[Globals.PARTICLE_SHIELD_FORTIFYBUFF].length) {
                frame++;
            }
        }
    }

    @Override
    public void draw(Graphics2D g) {
        if (PARTICLE_SPRITE[Globals.PARTICLE_SHIELD_FORTIFYBUFF] == null) {
            return;
        }
        if (frame >= PARTICLE_SPRITE[Globals.PARTICLE_SHIELD_FORTIFYBUFF].length) {
            return;
        }
        BufferedImage sprite = PARTICLE_SPRITE[Globals.PARTICLE_SHIELD_FORTIFYBUFF][frame];
        g.drawImage(sprite, x, y, null);
    }
}
