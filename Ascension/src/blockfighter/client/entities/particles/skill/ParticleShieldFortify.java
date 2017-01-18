package blockfighter.client.entities.particles.skill;

import blockfighter.client.entities.particles.Particle;
import blockfighter.client.entities.player.Player;
import blockfighter.shared.Globals;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;

public class ParticleShieldFortify extends Particle {

    public ParticleShieldFortify(final Player p) {
        super(p);
        this.frame = 0;
        this.frameDuration = 25;
        this.duration = 500;
    }

    @Override
    public void update() {
        super.update();
        if (Globals.nsToMs(logic.getTime() - this.lastFrameTime) >= this.frameDuration) {
            if (Globals.Particles.SHIELD_FORTIFY.getSprite() != null && this.frame < Globals.Particles.SHIELD_FORTIFY.getSprite().length - 1) {
                this.frame++;
            }

            this.lastFrameTime = logic.getTime();
        }
    }

    @Override
    public void draw(final Graphics2D g) {
        if (Globals.Particles.SHIELD_FORTIFY.getSprite() == null) {
            return;
        }
        if (this.frame >= Globals.Particles.SHIELD_FORTIFY.getSprite().length) {
            return;
        }
        final Point p = this.owner.getPos();
        this.x = p.x - 238;
        this.y = p.y - 410;
        final BufferedImage sprite = Globals.Particles.SHIELD_FORTIFY.getSprite()[this.frame];
        final int drawSrcX = this.x;
        final int drawSrcY = this.y;
        final int drawDscY = drawSrcY + sprite.getHeight();
        final int drawDscX = this.x + sprite.getWidth();
        g.drawImage(sprite, drawSrcX, drawSrcY, drawDscX, drawDscY, 0, 0, sprite.getWidth(), sprite.getHeight(), null);
    }
}