package blockfighter.client.entities.particles.skills.shield;

import blockfighter.client.Core;
import blockfighter.client.entities.particles.Particle;
import blockfighter.client.entities.player.Player;
import blockfighter.shared.Globals;
import java.awt.Point;

public class ParticleShieldReflectEmitter extends Particle {

    private long lastParticleTime = 0;

    public ParticleShieldReflectEmitter(final Player p) {
        super(p);
        this.frame = 0;
        this.duration = 3000;
        this.particleData = Globals.Particles.SHIELD_REFLECT_EMITTER;
    }

    @Override
    public void update() {
        if (!isExpired() && Globals.nsToMs(Core.getLogicModule().getTime() - lastParticleTime) >= 50) {
            final Point p = this.owner.getPos();
            if (p != null) {
                this.x = p.x;
                this.y = p.y;
            }
            for (int i = 0; i < 3; i++) {
                final ParticleShieldReflectBuff b = new ParticleShieldReflectBuff(this.x, this.y, this.facing);
                Core.getLogicModule().getScreen().addParticle(b);
            }
            lastParticleTime = Core.getLogicModule().getTime();
        }
    }

    @Override
    public boolean isExpired() {
        return super.isExpired() || this.owner.isDead();
    }
}
