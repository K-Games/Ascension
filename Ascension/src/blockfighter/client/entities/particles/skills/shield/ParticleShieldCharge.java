package blockfighter.client.entities.particles.skills.shield;

import blockfighter.client.Core;
import blockfighter.client.entities.particles.Particle;
import blockfighter.client.entities.player.Player;
import blockfighter.shared.Globals;
import java.awt.Graphics2D;
import java.awt.Point;

public class ParticleShieldCharge extends Particle {

    private long lastParticleTime = 0;

    public ParticleShieldCharge(final byte f, final Player p) {
        super(f, p);
        this.frame = 0;
        this.duration = 200;
        final Point point = this.owner.getPos();
        if (point != null) {
            this.x = point.x;
            this.y = point.y;
        }
        this.particleData = Globals.Particles.SHIELD_CHARGE;
    }

    @Override
    public void update() {
        if (Globals.nsToMs(Core.getLogicModule().getTime() - this.particleStartTime) < 650
                && Globals.nsToMs(Core.getLogicModule().getTime() - this.lastParticleTime) >= 20) {
            final ParticleShieldChargeParticle b = new ParticleShieldChargeParticle(this.x, this.y, this.facing);
            Core.getLogicModule().getScreen().addParticle(b);
            this.lastParticleTime = Core.getLogicModule().getTime();
        }
    }

    @Override
    public void draw(final Graphics2D g) {
        final Point p = this.owner.getPos();
        if (p != null) {
            this.x = p.x;
            this.y = p.y;
        }
        draw(g, -150, 30);
    }
}
