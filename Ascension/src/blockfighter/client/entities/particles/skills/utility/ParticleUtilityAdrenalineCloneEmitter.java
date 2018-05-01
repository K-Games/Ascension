package blockfighter.client.entities.particles.skills.utility;

import blockfighter.client.Core;
import blockfighter.client.entities.particles.Particle;
import blockfighter.client.entities.player.Player;
import blockfighter.shared.Globals;
import java.awt.Point;

public class ParticleUtilityAdrenalineCloneEmitter extends Particle {

    private long lastParticleTime = 0;

    public ParticleUtilityAdrenalineCloneEmitter(final Player p) {
        super(p);
        this.frame = 0;
        this.duration = 5000;
        this.particleData = Globals.Particles.UTILITY_ADRENALINE_CLONE_EMITTER;
    }

    @Override
    public void update() {
        super.update();
        if (!isExpired() && Globals.nsToMs(Core.getLogicModule().getTime() - lastParticleTime) >= 50) {
            final Point p = this.owner.getPos();
            if (p != null) {
                this.x = p.x;
                this.y = p.y;
                final ParticleUtilityAdrenalineCloneParticle b = new ParticleUtilityAdrenalineCloneParticle(this.x, this.y, this.owner, this.owner.getFacing(), this.owner.getAnimState(), this.owner.getFrame());
                Core.getLogicModule().getScreen().addParticle(b);
            }
            this.lastParticleTime = Core.getLogicModule().getTime();
        }
    }

    @Override
    public boolean isExpired() {
        return super.isExpired() || this.owner.isDead();
    }
}
