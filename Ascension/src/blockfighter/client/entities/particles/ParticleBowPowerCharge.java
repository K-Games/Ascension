package blockfighter.client.entities.particles;

import blockfighter.client.entities.player.Player;
import blockfighter.shared.Globals;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class ParticleBowPowerCharge extends Particle {

    private double speedX, speedY;

    public ParticleBowPowerCharge(final Player owner) {
        this.duration = 0;
        for (int i = 0; i < 4; i++) {
            logic.getScreen().addParticle(new ParticleBowPowerCharge(owner, i));
        }
    }

    public ParticleBowPowerCharge(final Player owner, final int i) {
        super(owner);
        this.frame = 0;
        this.frameDuration = 25;
        this.duration = 400;
        this.x = owner.getX() + Globals.rng(300) - 150;
        this.y = (int) ((owner.getY() - 75) + (Globals.rng(2) == 0 ? 1 : -1) * Math.sqrt(150 * 150 - (owner.getX() - this.x) * (owner.getX() - this.x)));
    }

    @Override
    public void update() {
        super.update();
        if (Globals.nsToMs(logic.getTime() - this.lastFrameTime) >= this.frameDuration) {
            long durationLeft = this.duration - Globals.nsToMs(logic.getTime() - this.particleStartTime);
            double numberOfTicks = durationLeft / Globals.nsToMs((long) Globals.CLIENT_LOGIC_UPDATE);
            if (numberOfTicks <= 1) {
                this.x = owner.getX();
                this.y = owner.getY() - 75;
            } else {
                this.speedX = (owner.getX() - this.x) / numberOfTicks;
                this.speedY = ((owner.getY() - 75) - this.y) / numberOfTicks;
                this.x += this.speedX;
                this.y += this.speedY;
            }
            this.lastFrameTime = logic.getTime();
        }
    }

    @Override
    public void draw(final Graphics2D g) {
        if (Globals.Particles.BOW_POWER_CHARGE.getSprite() == null) {
            return;
        }
        if (this.frame >= Globals.Particles.BOW_POWER_CHARGE.getSprite().length) {
            return;
        }
        final BufferedImage sprite = Globals.Particles.BOW_POWER_CHARGE.getSprite()[this.frame];
        final int drawSrcX = this.x - sprite.getWidth() / 2;
        final int drawSrcY = this.y;
        final int drawDscY = drawSrcY + sprite.getHeight();
        final int drawDscX = drawSrcX + sprite.getWidth();
        g.drawImage(sprite, drawSrcX, drawSrcY, drawDscX, drawDscY, 0, 0, sprite.getWidth(), sprite.getHeight(), null);
        g.setColor(Color.WHITE);
    }
}
