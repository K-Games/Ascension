package blockfighter.client.entities.particles.skills.shield;

import blockfighter.client.entities.particles.Particle;
import blockfighter.client.entities.player.Player;
import blockfighter.shared.Globals;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;

public class ParticleShieldReflectCast extends Particle {

    public ParticleShieldReflectCast(final Player p) {
        super(p);
        this.frame = 0;
        this.frameDuration = 25;
        this.duration = 400;
    }

    @Override
    public void update() {
        super.update();
        if (Globals.nsToMs(logic.getTime() - this.lastFrameTime) >= this.frameDuration) {
            this.frameDuration = 25;
            if (Globals.Particles.SHIELD_REFLECT_CAST.getSprite() != null && this.frame < Globals.Particles.SHIELD_REFLECT_CAST.getSprite().length - 1) {
                this.frame++;
            }
            this.lastFrameTime = logic.getTime();
        }
    }

    @Override
    public void draw(final Graphics2D g) {
        if (Globals.Particles.SHIELD_REFLECT_CAST.getSprite() == null) {
            return;
        }
        if (this.frame >= Globals.Particles.SHIELD_REFLECT_CAST.getSprite().length) {
            return;
        }
        final Point p = this.owner.getPos();
        final BufferedImage sprite = Globals.Particles.SHIELD_REFLECT_CAST.getSprite()[this.frame];
        this.x = p.x - sprite.getWidth() / 2;
        this.y = p.y - 350;
        final int drawSrcX = this.x;
        final int drawSrcY = this.y;
        final int drawDscY = drawSrcY + sprite.getHeight();
        final int drawDscX = this.x + sprite.getWidth();
        g.drawImage(sprite, drawSrcX, drawSrcY, drawDscX, drawDscY, 0, 0, sprite.getWidth(), sprite.getHeight(), null);
    }
}
