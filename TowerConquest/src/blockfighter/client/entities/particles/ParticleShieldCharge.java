package blockfighter.client.entities.particles;

import blockfighter.client.Globals;
import blockfighter.client.entities.player.Player;
import blockfighter.client.screen.ScreenIngame;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;

public class ParticleShieldCharge extends Particle {

    private final Player owner;

    public ParticleShieldCharge(final int k, final byte f, final Player p) {
        super(k, 0, 0, f);
        this.frame = 0;
        this.frameDuration = 25;
        this.duration = 750;
        this.owner = p;
        final Point point = this.owner.getPos();
        if (point != null) {
            this.x = point.x;
            this.y = point.y;
        }
    }

    @Override
    public void update() {
        super.update();
        this.frameDuration -= Globals.LOGIC_UPDATE / 1000000;
        if (this.duration > 100 && this.duration % 50 == 0) {
            final ParticleShieldChargeParticle b = new ParticleShieldChargeParticle(((ScreenIngame) logic.getScreen()).getNextParticleKey(),
                    this.x, this.y,
                    this.facing);
            ((ScreenIngame) logic.getScreen()).addParticle(b);
        }
        if (this.frameDuration <= 0) {
            this.frameDuration = 25;
            this.frame++;
            if (this.frame >= PARTICLE_SPRITE[Globals.PARTICLE_SHIELD_CHARGE].length - 1) {
                this.frame = 0;
            }
        }
    }

    @Override
    public void draw(final Graphics2D g) {
        if (PARTICLE_SPRITE[Globals.PARTICLE_SHIELD_CHARGE] == null) {
            return;
        }
        if (this.frame >= PARTICLE_SPRITE[Globals.PARTICLE_SHIELD_CHARGE].length) {
            return;
        }
        final Point p = this.owner.getPos();
        if (p != null) {
            if (this.facing == Globals.RIGHT) {
                this.x = p.x - 200;
            } else {
                this.x = p.x - 428 + 200;
            }
        }
        if (p != null) {
            this.y = p.y - 176;
        }
        final BufferedImage sprite = PARTICLE_SPRITE[Globals.PARTICLE_SHIELD_CHARGE][this.frame];
        final int drawSrcX = this.x + ((this.facing == Globals.RIGHT) ? 0 : sprite.getWidth());
        final int drawSrcY = this.y;
        final int drawDscY = drawSrcY + sprite.getHeight();
        final int drawDscX = this.x + ((this.facing == Globals.RIGHT) ? sprite.getWidth() : 0);
        g.drawImage(sprite, drawSrcX, drawSrcY, drawDscX, drawDscY, 0, 0, sprite.getWidth(), sprite.getHeight(), null);
    }
}
