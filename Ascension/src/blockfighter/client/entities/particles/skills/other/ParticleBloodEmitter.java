package blockfighter.client.entities.particles.skills.other;

import blockfighter.client.entities.particles.Particle;
import blockfighter.client.entities.player.Player;
import blockfighter.shared.Globals;
import java.awt.Point;

public class ParticleBloodEmitter extends Particle {

    private Player source;
    private long lastParticleTime = 0;

    public ParticleBloodEmitter(final Player p) {
        super(p);
        this.frame = 0;
        this.duration = 500;

    }

    public ParticleBloodEmitter(final Player p, final Player source) {
        super(p);
        this.frame = 0;
        this.duration = 50;
        this.source = source;
    }

    @Override
    public void update() {
        super.update();
        if (!isExpired() && Globals.nsToMs(logic.getTime() - lastParticleTime) >= 10) {
            final Point p = this.owner.getPos();
            if (p != null) {
                this.x = p.x;
                this.y = p.y;
            }
            for (int i = 0; i < ((source == null) ? 5 : 15); i++) {
                if (source == null) {
                    final ParticleBlood b = new ParticleBlood(this.x, this.y, this.owner.getFacing());
                    logic.getScreen().addParticle(b);
                } else {
                    final ParticleBlood b = new ParticleBlood(this.x, this.y, (this.owner.getX() <= this.source.getX()) ? Globals.RIGHT : Globals.LEFT);
                    logic.getScreen().addParticle(b);
                }
            }
            lastParticleTime = logic.getTime();
        }
    }
}
