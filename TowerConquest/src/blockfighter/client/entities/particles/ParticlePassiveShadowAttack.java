package blockfighter.client.entities.particles;

import blockfighter.client.Globals;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class ParticlePassiveShadowAttack extends Particle {

    private final byte type;
    private double speedX, speedY, dX, dY;

    public ParticlePassiveShadowAttack(final int x, final int y) {
        super(x, y, Globals.RIGHT);
        this.frame = 0;
        this.frameDuration = 50;
        this.duration = 200;
        this.type = (byte) Globals.rng(4);
        switch (this.type) {
            case 0:
                this.x += 200;
                this.y -= 50;
                this.speedX = -20;
                this.speedY = 15;
                break;
            case 1:
                this.x += 250;
                this.y -= 20;
                this.speedX = -40;
                break;
            case 2:
                this.x -= 50;
                this.y -= 50;
                this.speedX = 20;
                this.speedY = 15;
                break;
            case 3:
                this.x -= 150;
                this.y -= 10;
                this.speedX = 40;
                break;
        }

        this.dX = this.x;
        this.dY = this.y;
        this.frame = this.type * 4;
    }

    @Override
    public void update() {
        super.update();

        this.dX += this.speedX;
        this.dY += this.speedY;
        this.x = (int) this.dX;
        this.y = (int) this.dY;
        if (Globals.nsToMs(logic.getTime() - this.lastFrameTime) >= this.frameDuration) {
            if (this.frame < this.type * 3 + 3) {
                this.frame++;
            }
            this.lastFrameTime = logic.getTime();
        }
    }

    @Override
    public void draw(final Graphics2D g) {
        if (PARTICLE_SPRITE[Globals.PARTICLE_PASSIVE_SHADOWATTACK] == null) {
            return;
        }
        if (this.frame >= PARTICLE_SPRITE[Globals.PARTICLE_PASSIVE_SHADOWATTACK].length) {
            return;
        }
        final BufferedImage sprite = PARTICLE_SPRITE[Globals.PARTICLE_PASSIVE_SHADOWATTACK][this.frame];
        final int drawSrcX = this.x - sprite.getWidth();
        final int drawSrcY = this.y - sprite.getHeight();
        final int drawDscY = drawSrcY + sprite.getHeight();
        final int drawDscX = drawSrcX + sprite.getWidth();
        g.drawImage(sprite, drawSrcX, drawSrcY, drawDscX, drawDscY, 0, 0, sprite.getWidth(), sprite.getHeight(), null);
        g.setColor(Color.WHITE);
    }
}
