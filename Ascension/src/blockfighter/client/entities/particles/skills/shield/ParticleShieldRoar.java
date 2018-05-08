package blockfighter.client.entities.particles.skills.shield;

import blockfighter.client.entities.particles.Particle;
import blockfighter.client.entities.player.Player;
import blockfighter.shared.Globals;
import java.awt.Graphics2D;
import java.awt.Point;

public class ParticleShieldRoar extends Particle {

    public ParticleShieldRoar(final byte f, final Player p) {
        super(f, p);
        this.frame = 0;
        this.frameDuration = 50;
        this.duration = 500;
        this.particleData = Globals.Particles.SHIELD_ROAR;
    }

    @Override
    public void draw(final Graphics2D g) {
        final Point p = this.owner.getPos();
        if (p != null) {
            this.x = p.x;
            this.y = p.y;
        }
        draw(g, -150, 20);
    }
}
