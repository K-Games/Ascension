package blockfighter.client.entities.particles.skills.bow;

import blockfighter.client.Core;
import blockfighter.client.entities.particles.Particle;
import blockfighter.client.entities.player.skills.bow.SkillBowStorm;
import blockfighter.shared.Globals;

public class ParticleBowStormEmitter extends Particle {

    private long lastParticleTime = 0;

    public ParticleBowStormEmitter(final int x, final int y, final byte f) {
        super(x, y, f);
        this.frame = 0;
        this.duration = new SkillBowStorm().getCustomValue(SkillBowStorm.CUSTOM_DATA_HEADERS[1]).intValue();
        this.particleData = Globals.Particles.BOW_STORM_EMITTER;
    }

    @Override
    public void update() {
        if (Globals.nsToMs(Core.getLogicModule().getTime() - this.particleStartTime) <= 4800) {
            if (Globals.nsToMs(Core.getLogicModule().getTime() - lastParticleTime) >= 50) {
                for (byte i = 0; i < 2; i++) {
                    final ParticleBowStormArrow b = new ParticleBowStormArrow(this.x + ((this.facing == Globals.RIGHT) ? -280 : 280) + (((this.facing == Globals.RIGHT) ? Globals.rng(20) : -Globals.rng(20)) * 35), this.y, this.facing);
                    Core.getLogicModule().getScreen().addParticle(b);
                }
                lastParticleTime = Core.getLogicModule().getTime();
            }
        }

    }
}
