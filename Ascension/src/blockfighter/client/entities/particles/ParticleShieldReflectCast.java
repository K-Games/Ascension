package blockfighter.client.entities.particles;

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
            if (PARTICLE_SPRITE != null && this.frame < PARTICLE_SPRITE[Globals.PARTICLE_SHIELD_REFLECTCAST].length - 1) {
                this.frame++;
            }
            this.lastFrameTime = logic.getTime();
        }
    }

    @Override
    public void draw(final Graphics2D g) {
        if (PARTICLE_SPRITE[Globals.PARTICLE_SHIELD_REFLECTCAST] == null) {
            return;
        }
        if (this.frame >= PARTICLE_SPRITE[Globals.PARTICLE_SHIELD_REFLECTCAST].length) {
            return;
        }
        final Point p = this.owner.getPos();
        final BufferedImage sprite = PARTICLE_SPRITE[Globals.PARTICLE_SHIELD_REFLECTCAST][this.frame];
        this.x = p.x - sprite.getWidth() / 2;
        this.y = p.y - 350;
        final int drawSrcX = this.x;
        final int drawSrcY = this.y;
        final int drawDscY = drawSrcY + sprite.getHeight();
        final int drawDscX = this.x + sprite.getWidth();
        g.drawImage(sprite, drawSrcX, drawSrcY, drawDscX, drawDscY, 0, 0, sprite.getWidth(), sprite.getHeight(), null);
    }
}
