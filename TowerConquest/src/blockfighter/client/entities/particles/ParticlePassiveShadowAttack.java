package blockfighter.client.entities.particles;

import blockfighter.client.Globals;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class ParticlePassiveShadowAttack extends Particle {

    private byte type;
    private double speedX, speedY, dX, dY;

    public ParticlePassiveShadowAttack(int k, int x, int y) {
        super(k, x, y, Globals.RIGHT);
        frame = 0;
        frameDuration = 50;
        duration = 200;
        type = (byte) Globals.rng(4);
        switch (type) {
            case 0:
                this.x += 200;
                this.y -= 50;
                speedX = -20;
                speedY = 15;
                break;
            case 1:
                this.x += 250;
                this.y -= 20;
                speedX = -40;
                break;
            case 2:
                this.x -= 50;
                this.y -= 50;
                speedX = 20;
                speedY = 15;
                break;
            case 3:
                this.x -= 150;
                this.y -= 10;
                speedX = 40;
                break;
        }

        dX = this.x;
        dY = this.y;
        frame = type * 4;
    }

    @Override
    public void update() {
        super.update();
        frameDuration -= Globals.LOGIC_UPDATE / 1000000;
        dX += speedX;
        dY += speedY;
        x = (int) dX;
        y = (int) dY;
        if (frameDuration <= 0) {
            frameDuration = 50;
            if (frame < type * 3 + 3) {
                frame++;
            }
        }
    }

    @Override
    public void draw(Graphics2D g) {
        if (PARTICLE_SPRITE[Globals.PARTICLE_PASSIVE_SHADOWATTACK] == null) {
            return;
        }
        if (frame >= PARTICLE_SPRITE[Globals.PARTICLE_PASSIVE_SHADOWATTACK].length) {
            return;
        }
        BufferedImage sprite = PARTICLE_SPRITE[Globals.PARTICLE_PASSIVE_SHADOWATTACK][frame];
        int drawSrcX = x - sprite.getWidth();
        int drawSrcY = y - sprite.getHeight();
        int drawDscY = drawSrcY + sprite.getHeight();
        int drawDscX = drawSrcX + sprite.getWidth();
        g.drawImage(sprite, drawSrcX, drawSrcY, drawDscX, drawDscY, 0, 0, sprite.getWidth(), sprite.getHeight(), null);
        g.setColor(Color.WHITE);
    }
}
