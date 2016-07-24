package blockfighter.client.entities.particles;

import blockfighter.client.Globals;
import blockfighter.client.entities.player.Player;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;

public class ParticleShieldCharge extends Particle {

    private long lastParticleTime = 0;

    public ParticleShieldCharge(final byte f, final Player p) {
        super(f, p);
        this.frame = 0;
        this.duration = 750;
        final Point point = this.owner.getPos();
        if (point != null) {
            this.x = (this.facing == Globals.RIGHT) ? point.x - 150 : point.x - 253 + 150;
            this.y = point.y - 170;
        }
    }

    @Override
    public void update() {
        super.update();
        if (Globals.nsToMs(logic.getTime() - this.particleStartTime) < 650
                && Globals.nsToMs(logic.getTime() - this.lastParticleTime) >= 50) {
            final ParticleShieldChargeParticle b = new ParticleShieldChargeParticle(this.x, this.y, this.facing);
            logic.getScreen().addParticle(b);
            this.lastParticleTime = logic.getTime();
        }
    }

    @Override
    public void draw(final Graphics2D g) {
        if (PARTICLE_SPRITE[Globals.PARTICLE_SHIELD_CHARGE] == null) {
            return;
        }
        final Point p = this.owner.getPos();
        if (p != null) {
            this.x = (this.facing == Globals.RIGHT) ? p.x - 150 : p.x - 253 + 150;
            this.y = p.y - 170;
        }
        if (this.frame >= PARTICLE_SPRITE[Globals.PARTICLE_SHIELD_CHARGE].length) {
            return;
        }
        final BufferedImage sprite = PARTICLE_SPRITE[Globals.PARTICLE_SHIELD_CHARGE][this.frame];
        final int drawSrcX = this.x + ((this.facing == Globals.RIGHT) ? 0 : sprite.getWidth());
        final int drawSrcY = this.y;
        final int drawDscY = drawSrcY + sprite.getHeight();
        final int drawDscX = this.x + ((this.facing == Globals.RIGHT) ? sprite.getWidth() : 0);
        g.drawImage(sprite, drawSrcX, drawSrcY, drawDscX, drawDscY, 0, 0, sprite.getWidth(), sprite.getHeight(), null);
    }
}
