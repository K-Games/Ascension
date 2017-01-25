package blockfighter.client.entities.particles.skills.shield;

import blockfighter.client.Core;
import blockfighter.client.entities.particles.Particle;
import blockfighter.client.entities.player.Player;
import blockfighter.shared.Globals;
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
            this.x = point.x;
            this.y = point.y;
        }
    }

    @Override
    public void update() {
        super.update();
        if (Globals.nsToMs(Core.getLogicModule().getTime() - this.particleStartTime) < 650
                && Globals.nsToMs(Core.getLogicModule().getTime() - this.lastParticleTime) >= 50) {
            final ParticleShieldChargeParticle b = new ParticleShieldChargeParticle(this.x, this.y, this.facing);
            Core.getLogicModule().getScreen().addParticle(b);
            this.lastParticleTime = Core.getLogicModule().getTime();
        }
    }

    @Override
    public void draw(final Graphics2D g) {
        if (Globals.Particles.SHIELD_CHARGE.getSprite() == null) {
            return;
        }
        if (this.frame >= Globals.Particles.SHIELD_CHARGE.getSprite().length) {
            return;
        }
        final Point p = this.owner.getPos();
        if (p != null) {
            this.x = p.x;
            this.y = p.y;
        }
        final BufferedImage sprite = Globals.Particles.SHIELD_CHARGE.getSprite()[this.frame];
        final int drawSrcX = this.x + ((this.facing == Globals.RIGHT) ? -150 : 150);
        final int drawSrcY = this.y - sprite.getHeight() + 30;
        final int drawDscY = drawSrcY + sprite.getHeight();
        final int drawDscX = drawSrcX + ((this.facing == Globals.RIGHT) ? sprite.getWidth() : -sprite.getWidth());
        g.drawImage(sprite, drawSrcX, drawSrcY, drawDscX, drawDscY, 0, 0, sprite.getWidth(), sprite.getHeight(), null);
    }
}
