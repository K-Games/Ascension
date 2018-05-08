package blockfighter.client.entities.particles.skills.sword;

import blockfighter.client.Core;
import blockfighter.client.entities.particles.Particle;
import blockfighter.client.entities.player.Player;
import blockfighter.shared.Globals;
import java.awt.Point;

public class ParticleSwordTauntBuffEmitter extends Particle {

    private long lastParticleTime = 0;

    public ParticleSwordTauntBuffEmitter(final Player p) {
        super(p);
        this.frame = 0;
        this.duration = 5000;
        this.particleData = Globals.Particles.SWORD_TAUNT_BUFF_EMITTER;
    }

    @Override
    public void update() {
        if (!isExpired() && Globals.nsToMs(Core.getLogicModule().getTime() - lastParticleTime) >= 50) {
            final Point p = this.owner.getPos();
            if (p != null) {
                this.x = p.x;
                this.y = p.y;
            }
            for (int i = 0; i < 2; i++) {
                final ParticleSwordTauntAuraParticle b = new ParticleSwordTauntAuraParticle(this.x, this.y, this.facing);
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
