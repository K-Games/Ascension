package blockfighter.client.entities.particles.skills.sword;

import blockfighter.client.Core;
import blockfighter.client.entities.particles.Particle;
import blockfighter.shared.Globals;
import java.awt.Graphics2D;

public class ParticleSwordTaunt extends Particle {

    public ParticleSwordTaunt(final int x, final int y, final byte f) {
        super(x, y, f);
        this.frame = 0;
        this.frameDuration = 50;
        this.duration = 300;
        this.particleData = Globals.Particles.SWORD_TAUNT;
    }

    @Override
    public void update() {
        if (Globals.nsToMs(Core.getLogicModule().getTime() - this.lastFrameTime) >= this.frameDuration) {
            if (Globals.Particles.SWORD_TAUNT.getSprites() != null && this.frame < Globals.Particles.SWORD_TAUNT.getSprites().length) {
                this.frame++;
                this.frameDuration = (this.frame == 2) ? 100 : 50;
            }
            this.lastFrameTime = Core.getLogicModule().getTime();
        }
    }

    @Override
    public void draw(final Graphics2D g) {
        draw(g, -170, 80);
    }
}
