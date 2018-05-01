package blockfighter.client.entities.particles.skills.bow;

import blockfighter.client.Core;
import blockfighter.client.entities.particles.Particle;
import blockfighter.client.entities.player.Player;
import blockfighter.shared.Globals;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class ParticleBowPowerCharge extends Particle {

    private double speedX, speedY;

    public ParticleBowPowerCharge(final Player owner) {
        this.duration = 0;
        for (int i = 0; i < 4; i++) {
            Core.getLogicModule().getScreen().addParticle(new ParticleBowPowerCharge(owner, i));
        }
    }

    public ParticleBowPowerCharge(final Player owner, final int i) {
        super(owner);
        this.frame = 0;
        this.frameDuration = 25;
        this.duration = 400;
        this.x = owner.getX() + Globals.rng(300) - 150;
        this.y = (int) ((owner.getY() - 75) + (Globals.rng(2) == 0 ? 1 : -1) * Math.sqrt(150 * 150 - (owner.getX() - this.x) * (owner.getX() - this.x)));
        this.particleData = Globals.Particles.BOW_POWER_CHARGE;
    }

    @Override
    public void update() {
        long durationLeft = this.duration - Globals.nsToMs(Core.getLogicModule().getTime() - this.particleStartTime);
        double numberOfTicks = 1f * durationLeft / Globals.nsToMs((long) Globals.CLIENT_LOGIC_UPDATE);
        if (numberOfTicks <= 1) {
            this.x = owner.getX();
            this.y = owner.getY() - 75;
        } else {
            this.speedX = (owner.getX() - this.x) / numberOfTicks;
            this.speedY = ((owner.getY() - 75) - this.y) / numberOfTicks;
            this.x += this.speedX;
            this.y += this.speedY;
        }
    }

    @Override
    public void draw(final Graphics2D g) {
        if (this.spriteFrameExists()) {
            final BufferedImage sprite = Globals.Particles.BOW_POWER_CHARGE.getSprites()[this.frame];
            draw(g, -sprite.getWidth() / 2, 0, false);
        }
    }
}
