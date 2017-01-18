package blockfighter.client.entities.particles.skill;

import blockfighter.client.entities.particles.Particle;
import blockfighter.client.entities.player.Player;
import blockfighter.client.entities.player.skills.SkillBowVolley;
import blockfighter.shared.Globals;
import java.awt.Point;

public class ParticleBowVolleyBuffEmitter extends Particle {

    private long lastParticleTime = 0;

    public ParticleBowVolleyBuffEmitter(final Player p) {
        super(p);
        this.frame = 0;
        this.duration = (int) (SkillBowVolley.getBuffDuration() * 1000);
    }

    @Override
    public void update() {
        super.update();
        if (!isExpired() && Globals.nsToMs(logic.getTime() - lastParticleTime) >= 100) {
            final Point p = this.owner.getPos();
            if (p != null) {
                this.x = p.x;
                this.y = p.y;
            }
            final ParticleBowVolleyBuffParticle b = new ParticleBowVolleyBuffParticle(this.x, this.y, this.facing);
            logic.getScreen().addParticle(b);
            lastParticleTime = logic.getTime();
        }
    }

    @Override
    public boolean isExpired() {
        return super.isExpired() || this.owner.isDead();
    }
}
