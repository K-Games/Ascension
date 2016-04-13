package blockfighter.client.entities.particles;

import blockfighter.client.Globals;
import blockfighter.client.entities.player.Player;
import java.awt.Point;

public class ParticleShieldDashEmitter extends Particle {

    private final Player owner;

    public ParticleShieldDashEmitter(final byte f, final Player p) {
        super(0, 0, f);
        this.frame = 0;
        this.duration = 250;
        this.owner = p;
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
            final ParticleShieldDash b = new ParticleShieldDash(this.x + ((this.facing == Globals.RIGHT) ? -172 : -200), this.y - 330, this.facing);
            logic.getScreen().addParticle(b);
        }
    }
}
