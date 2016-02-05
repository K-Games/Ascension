package blockfighter.client.entities.particles;

import blockfighter.client.Globals;
import blockfighter.client.entities.player.Player;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;

public class ParticleShieldIronAlly extends Particle {

    private final Player owner;

    public ParticleShieldIronAlly(final int k, final Player p) {
        super(k, 0, 0);
        this.frame = 0;
        this.frameDuration = 25;
        this.duration = 300;
        this.owner = p;
    }

    @Override
    public void update() {
        super.update();
        if (Globals.nsToMs(logic.getTime() - this.lastFrameTime) >= this.frameDuration) {
            if (this.frame < PARTICLE_SPRITE[Globals.PARTICLE_SHIELD_IRONALLY].length - 1) {
                this.frame++;
            }
            this.lastFrameTime = logic.getTime();
        }
    }

    @Override
    public void draw(final Graphics2D g) {
        if (PARTICLE_SPRITE[Globals.PARTICLE_SHIELD_IRONALLY] == null) {
            return;
        }
        if (this.frame >= PARTICLE_SPRITE[Globals.PARTICLE_SHIELD_IRONALLY].length) {
            return;
        }
        final Point p = this.owner.getPos();
        final BufferedImage sprite = PARTICLE_SPRITE[Globals.PARTICLE_SHIELD_IRONALLY][this.frame];
        this.x = p.x - sprite.getWidth() / 2;
        this.y = p.y - sprite.getHeight() - 50;
        final int drawSrcX = this.x;
        final int drawSrcY = this.y;
        final int drawDscY = drawSrcY + sprite.getHeight();
        final int drawDscX = this.x + sprite.getWidth();
        g.drawImage(sprite, drawSrcX, drawSrcY, drawDscX, drawDscY, 0, 0, sprite.getWidth(), sprite.getHeight(), null);
    }
}
