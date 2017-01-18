package blockfighter.client.entities.particles.skill;

import blockfighter.client.entities.particles.Particle;
import blockfighter.client.entities.player.Player;
import blockfighter.shared.Globals;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;

public class ParticleShieldMagnetizeBurst extends Particle {

    public ParticleShieldMagnetizeBurst(final Player p) {
        super(p);
        this.frame = 0;
        this.frameDuration = 50;
        this.duration = 600;
        final Point point = this.owner.getPos();
        if (point != null) {
            this.x = point.x;
            this.y = point.y;
        }
    }

    @Override
    public void update() {
        super.update();

        if (Globals.nsToMs(logic.getTime() - this.lastFrameTime) >= this.frameDuration) {
            if (Globals.Particles.SHIELD_MAGNETIZE_BURST.getSprite() != null && this.frame < Globals.Particles.SHIELD_MAGNETIZE_BURST.getSprite().length) {
                this.frame++;
                if (this.frame == Globals.Particles.SHIELD_MAGNETIZE_BURST.getSprite().length) {
                    this.frame = 0;
                }
            }
            this.lastFrameTime = logic.getTime();
        }
    }

    @Override
    public void draw(final Graphics2D g) {
        if (Globals.Particles.SHIELD_MAGNETIZE_BURST.getSprite() == null) {
            return;
        }
        if (this.frame >= Globals.Particles.SHIELD_MAGNETIZE_BURST.getSprite().length) {
            return;
        }
        final Point p = this.owner.getPos();
        if (p != null) {
            this.x = p.x;
            this.y = p.y;
        }
        final BufferedImage sprite = Globals.Particles.SHIELD_MAGNETIZE_BURST.getSprite()[this.frame];
        final int drawSrcX = this.x - (sprite.getWidth()) / 2 + 20;
        final int drawSrcY = this.y - sprite.getHeight() + 60;
        final int drawDscY = drawSrcY + sprite.getHeight();
        final int drawDscX = drawSrcX + sprite.getWidth();
        g.drawImage(sprite, drawSrcX, drawSrcY, drawDscX, drawDscY, 0, 0, sprite.getWidth(), sprite.getHeight(), null);
    }
}
