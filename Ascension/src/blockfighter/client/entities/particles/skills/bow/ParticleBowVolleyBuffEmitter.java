package blockfighter.client.entities.particles.skills.bow;

import blockfighter.client.Core;
import blockfighter.client.entities.particles.Particle;
import blockfighter.client.entities.player.Player;
import blockfighter.client.entities.player.skills.Skill;
import blockfighter.shared.Globals;
import java.awt.Point;

public class ParticleBowVolleyBuffEmitter extends Particle {

    private long lastParticleTime = 0;

    public ParticleBowVolleyBuffEmitter(final Player p) {
        super(p);
        this.frame = 0;
        Skill skill = Globals.SkillClassMap.BOW_VOLLEY.getClientSkillInstance();
        this.duration = skill.getCustomValue(skill.getCustomDataHeaders()[1]).intValue();
        this.particleData = Globals.Particles.BOW_VOLLEY_BUFF_EMITTER;
    }

    @Override
    public void update() {
        if (!isExpired() && Globals.nsToMs(Core.getLogicModule().getTime() - lastParticleTime) >= 100) {
            final Point p = this.owner.getPos();
            if (p != null) {
                this.x = p.x;
                this.y = p.y;
            }
            final ParticleBowVolleyBuffParticle b = new ParticleBowVolleyBuffParticle(this.x, this.y, this.facing);
            Core.getLogicModule().getScreen().addParticle(b);
            lastParticleTime = Core.getLogicModule().getTime();
        }
    }

    @Override
    public boolean isExpired() {
        return super.isExpired() || this.owner.isDead();
    }
}
