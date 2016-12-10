package blockfighter.client.entities.particles;

import static blockfighter.client.entities.particles.Particle.PARTICLE_SPRITE;
import blockfighter.client.entities.player.Player;
import blockfighter.shared.Globals;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class ParticleSwordPhantom2 extends Particle {

    private final Integer[] trailX = new Integer[2], trailY = new Integer[4];
    private Color trailColor = new Color(0, 0, 0, 250);

    public ParticleSwordPhantom2(final Player p) {
        super(p);
        this.frame = 0;
        this.frameDuration = 50;
        this.duration = 400;
        this.trailX[0] = x;
        this.trailY[0] = y - 60;
        this.trailY[1] = y - 40;
    }

    @Override
    public void update() {
        super.update();
        if (Globals.nsToMs(logic.getTime() - this.particleStartTime) >= 50 && trailX[1] == null) {
            this.trailX[1] = this.owner.getX();
            this.trailY[3] = this.owner.getY() - 100;
            this.trailY[2] = this.owner.getY() - 20;
        }

        if (Globals.nsToMs(logic.getTime() - this.lastFrameTime) >= this.frameDuration) {
            double fade = 1D * Globals.nsToMs(logic.getTime() - this.particleStartTime) / (this.duration);
            double alpha = 250 * (1D - ((fade > 1) ? 1D : fade));
            this.frameDuration = 50;
            trailColor = new Color(0, 0, 0, (int) alpha);
            if (PARTICLE_SPRITE != null && this.frame < PARTICLE_SPRITE[Globals.PARTICLE_SWORD_PHANTOM2].length) {
                this.frame++;
            }
            this.lastFrameTime = logic.getTime();
        }
    }

    @Override
    public void draw(final Graphics2D g) {
        if (this.trailX[0] != null && this.trailX[1] != null
                && this.trailY[0] != null && this.trailY[1] != null && this.trailY[2] != null && this.trailY[3] != null) {
            final int[] xPoints = {this.trailX[0], this.trailX[0], this.trailX[1], this.trailX[1]};
            final int[] yPoints = {this.trailY[0], this.trailY[1], this.trailY[2], this.trailY[3]};
            g.setColor(trailColor);
            g.fillPolygon(xPoints, yPoints, 4);
        }
        if (PARTICLE_SPRITE[Globals.PARTICLE_SWORD_PHANTOM2] == null) {
            return;
        }
        if (this.frame >= PARTICLE_SPRITE[Globals.PARTICLE_SWORD_PHANTOM2].length) {
            return;
        }
        final BufferedImage sprite = PARTICLE_SPRITE[Globals.PARTICLE_SWORD_PHANTOM2][this.frame];
        final int drawSrcX = this.x + ((this.facing == Globals.RIGHT) ? -10 : 10);
        final int drawSrcY = this.y - sprite.getHeight() + 10;
        final int drawDscY = drawSrcY + sprite.getHeight();
        final int drawDscX = drawSrcX + ((this.facing == Globals.RIGHT) ? sprite.getWidth() : -sprite.getWidth());
        g.drawImage(sprite, drawSrcX, drawSrcY, drawDscX, drawDscY, 0, 0, sprite.getWidth(), sprite.getHeight(), null);

    }
}
