package blockfighter.client.entities.particles;

import blockfighter.shared.Globals;
import static blockfighter.client.entities.particles.Particle.logic;
import blockfighter.client.entities.player.Player;
import java.awt.Point;

public class ParticleShieldDashBuffEmitter extends Particle {

    private long lastParticleTime = 0;

    public ParticleShieldDashBuffEmitter(final Player p) {
        super(p);
        this.frame = 0;
        this.duration = 5000;
    }

    @Override
    public void update() {
        super.update();
        if (!isExpired() && Globals.nsToMs(logic.getTime() - lastParticleTime) >= 50) {
            final Point p = this.owner.getPos();
            if (p != null) {
                this.x = p.x;
                this.y = p.y;
            }
            for (int i = 0; i < 2; i++) {
                final ParticleShieldDashBuffParticle b = new ParticleShieldDashBuffParticle(this.x, this.y, this.facing);
                logic.getScreen().addParticle(b);
            }
            lastParticleTime = logic.getTime();
        }
    }

    @Override
    public boolean isExpired() {
        return super.isExpired() || this.owner.isDead();
    }
}
