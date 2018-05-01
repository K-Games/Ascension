package blockfighter.client.entities.particles.skills.shield;

import blockfighter.client.entities.particles.Particle;
import blockfighter.client.entities.player.Player;
import blockfighter.shared.Globals;
import java.awt.Graphics2D;
import java.awt.Point;

public class ParticleShieldMagnetizeStart extends Particle {

    public ParticleShieldMagnetizeStart(final Player p) {
        super(p);
        this.frame = 0;
        this.frameDuration = 25;
        this.duration = 325;
        final Point point = this.owner.getPos();
        if (point != null) {
            this.x = point.x;
            this.y = point.y;
        }
        this.particleData = Globals.Particles.SHIELD_MAGNETIZE_START;
    }

    @Override
    public void draw(final Graphics2D g) {
        final Point p = this.owner.getPos();
        if (p != null) {
            this.x = p.x;
            this.y = p.y;
        }
        draw(g, -140, 70, false);
    }
}
