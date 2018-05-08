package blockfighter.client.entities.particles.skills.shield;

import blockfighter.client.entities.particles.Particle;
import blockfighter.client.entities.player.Player;
import blockfighter.shared.Globals;
import java.awt.Graphics2D;
import java.awt.Point;

public class ParticleShieldRoarHit extends Particle {

    public ParticleShieldRoarHit(final Player p) {
        super(p);
        final Point point = this.owner.getPos();
        if (point != null) {
            this.x = point.x;
            this.y = point.y;
        }
        this.frame = 0;
        this.frameDuration = 50;
        this.duration = 350;
        this.particleData = Globals.Particles.SHIELD_ROARHIT;
    }

    @Override
    public void draw(final Graphics2D g) {
        draw(g, -121, 0, false);
    }
}
