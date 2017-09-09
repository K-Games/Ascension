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
    }

    @Override
    public void update() {
        super.update();
        if (Globals.nsToMs(Core.getLogicModule().getTime() - this.particleStartTime) <= 4800) {
            if (Globals.nsToMs(Core.getLogicModule().getTime() - lastParticleTime) >= 50) {
                for (byte i = 0; i < 2; i++) {
                    final ParticleBowStormArrow b = new ParticleBowStormArrow(this.x, this.y, this.facing);
                    Core.getLogicModule().getScreen().addParticle(b);
                }
                lastParticleTime = Core.getLogicModule().getTime();
            }
        }

    }
}
