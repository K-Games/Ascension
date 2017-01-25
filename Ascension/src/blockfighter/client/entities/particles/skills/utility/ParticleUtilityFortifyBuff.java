package blockfighter.client.entities.particles.skills.utility;

import blockfighter.client.Core;
import blockfighter.client.entities.particles.Particle;
import blockfighter.shared.Globals;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class ParticleUtilityFortifyBuff extends Particle {

    double pX, speedX;

    public ParticleUtilityFortifyBuff(final int x, final int y, final byte f) {
        super(x, y, f);
        this.x += Globals.rng(50) - 35;
        this.y += -30 - Globals.rng(80);
        this.frameDuration = 25;
        this.duration = 200;
    }

    @Override
    public void update() {
        super.update();

        this.y -= 7;
        if (Globals.nsToMs(Core.getLogicModule().getTime() - this.lastFrameTime) >= this.frameDuration) {
            if (Globals.Particles.UTILITY_FORTIFY_BUFF.getSprite() != null && this.frame < Globals.Particles.UTILITY_FORTIFY_BUFF.getSprite().length) {
                this.frame++;
            }
            this.lastFrameTime = Core.getLogicModule().getTime();
        }
    }

    @Override
    public void draw(final Graphics2D g) {
        if (Globals.Particles.UTILITY_FORTIFY_BUFF.getSprite() == null) {
            return;
        }
        if (this.frame >= Globals.Particles.UTILITY_FORTIFY_BUFF.getSprite().length) {
            return;
        }
        final BufferedImage sprite = Globals.Particles.UTILITY_FORTIFY_BUFF.getSprite()[this.frame];
        g.drawImage(sprite, this.x, this.y, null);
    }
}
