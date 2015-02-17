package blockfighter.client.entities.particles;

import blockfighter.client.Globals;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class ParticleShieldReflectBuff extends Particle {

    double pX, speedX;

    public ParticleShieldReflectBuff(int k, int x, int y, byte f) {
        super(k, x, y, f);
        this.x += rng.nextInt(10) * 10 - 80;
        this.y += -rng.nextInt(100) - 60;
        pX = this.x;
        speedX = (rng.nextInt(10) - 5) * 1.5D;

        frame = rng.nextInt(5);
        frameDuration = 25;
        duration = 400;
    }

    @Override
    public void update() {
        super.update();
        frameDuration -= Globals.LOGIC_UPDATE / 1000000;
        y -= 10;
        pX += speedX;
        x = (int) pX;
        if (frameDuration <= 0) {
            frameDuration = 25;
            if (frame < PARTICLE_SPRITE[Globals.PARTICLE_SHIELD_REFLECTBUFF].length) {
                frame++;
            }
        }
    }

    @Override
    public void draw(Graphics2D g) {
        if (PARTICLE_SPRITE[Globals.PARTICLE_SHIELD_REFLECTBUFF] == null) {
            return;
        }
        if (frame >= PARTICLE_SPRITE[Globals.PARTICLE_SHIELD_REFLECTBUFF].length) {
            return;
        }
        BufferedImage sprite = PARTICLE_SPRITE[Globals.PARTICLE_SHIELD_REFLECTBUFF][frame];
        g.drawImage(sprite, x, y, null);
    }
}
