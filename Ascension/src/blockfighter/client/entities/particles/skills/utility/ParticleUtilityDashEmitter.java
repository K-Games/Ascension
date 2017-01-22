package blockfighter.client.entities.particles.skills.utility;

import blockfighter.client.entities.particles.Particle;
import blockfighter.client.entities.player.Player;
import java.awt.Point;

public class ParticleUtilityDashEmitter extends Particle {

    public ParticleUtilityDashEmitter(final byte f, final Player p) {
        super(f, p);
        this.frame = 0;
        this.duration = 50;
    }

    @Override
    public void update() {
        super.update();
        if (!isExpired()) {
            final Point p = this.owner.getPos();
            if (p != null) {
                this.x = p.x;
                this.y = p.y;
            }
            final ParticleUtilityDash b = new ParticleUtilityDash(this.x, this.y, this.facing);
            logic.getScreen().addParticle(b);
        }
    }
}
