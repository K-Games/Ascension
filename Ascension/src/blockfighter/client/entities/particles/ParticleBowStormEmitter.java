package blockfighter.client.entities.particles;

import blockfighter.client.Globals;
import static blockfighter.client.entities.particles.Particle.logic;

public class ParticleBowStormEmitter extends Particle {

    private long lastParticleTime = 0;

    public ParticleBowStormEmitter(final int x, final int y, final byte f) {
        super(x, y, f);
        this.frame = 0;
        this.duration = 5000;
    }

    @Override
    public void update() {
        super.update();
        if (Globals.nsToMs(logic.getTime() - this.particleStartTime) <= 4800) {
            if (Globals.nsToMs(logic.getTime() - lastParticleTime) >= 50) {
                for (byte i = 0; i < 1; i++) {
                    final ParticleBowStormArrow b = new ParticleBowStormArrow(this.x, this.y, this.facing);
                    logic.getScreen().addParticle(b);
                }
                lastParticleTime = logic.getTime();
            }
        }

    }
}
