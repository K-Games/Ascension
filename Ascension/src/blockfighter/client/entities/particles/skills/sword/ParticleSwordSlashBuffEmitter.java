package blockfighter.client.entities.particles.skills.sword;

import blockfighter.client.entities.particles.Particle;
import blockfighter.client.entities.player.Player;
import blockfighter.shared.Globals;
import java.awt.Point;

public class ParticleSwordSlashBuffEmitter extends Particle {

    private long lastParticleTime = 0;

    public ParticleSwordSlashBuffEmitter(final Player p) {
        super(p);
        this.frame = 0;
        this.duration = 1700;
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
            for (int i = 0; i < 1; i++) {
                final ParticleSwordSlashBuffParticle b = new ParticleSwordSlashBuffParticle(this.x, this.y, this.facing);
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
