package blockfighter.client.entities.particles.skill;

import blockfighter.client.entities.particles.Particle;
import blockfighter.client.entities.player.Player;
import blockfighter.shared.Globals;
import java.awt.Point;

public class ParticleBurnBuffEmitter extends Particle {

    private long lastParticleTime = 0;

    public ParticleBurnBuffEmitter(final Player p) {
        super(p);
        this.frame = 0;
        this.duration = 4000;
    }

    @Override
    public void update() {
        super.update();
        if (!isExpired() && Globals.nsToMs(logic.getTime() - lastParticleTime) >= 500) {
            final Point p = this.owner.getPos();
            if (p != null) {
                this.x = p.x;
                this.y = p.y;
            }
            for (int i = 0; i < 15; i++) {
                final ParticleBurnBuffParticle b = new ParticleBurnBuffParticle(this.x, this.y, this.facing);
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