package blockfighter.client.entities.particles;

import blockfighter.shared.Globals;
import blockfighter.client.entities.player.Player;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;

public class ParticleShieldMagnetizeStart extends Particle {

    public ParticleShieldMagnetizeStart(final Player p) {
        super(p);
        this.frame = 0;
        this.frameDuration = 25;
        this.duration = 325;
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
            this.frameDuration = 25;
            if (PARTICLE_SPRITE != null && this.frame < PARTICLE_SPRITE[Globals.PARTICLE_SHIELD_MAGNETIZESTART].length) {
                this.frame++;
                if (this.frame == PARTICLE_SPRITE[Globals.PARTICLE_SHIELD_MAGNETIZESTART].length) {
                    this.frame = 0;
                }
            }
            this.lastFrameTime = logic.getTime();
        }
    }

    @Override
    public void draw(final Graphics2D g) {
        if (PARTICLE_SPRITE[Globals.PARTICLE_SHIELD_MAGNETIZESTART] == null) {
            return;
        }
        if (this.frame >= PARTICLE_SPRITE[Globals.PARTICLE_SHIELD_MAGNETIZESTART].length) {
            return;
        }
        final Point p = this.owner.getPos();
        if (p != null) {
            this.x = p.x;
            this.y = p.y;
        }
        final BufferedImage sprite = PARTICLE_SPRITE[Globals.PARTICLE_SHIELD_MAGNETIZESTART][this.frame];
        final int drawSrcX = this.x - sprite.getWidth() / 2;
        final int drawSrcY = this.y - sprite.getHeight() + 70;
        final int drawDscY = drawSrcY + sprite.getHeight();
        final int drawDscX = drawSrcX + sprite.getWidth();
        g.drawImage(sprite, drawSrcX, drawSrcY, drawDscX, drawDscY, 0, 0, sprite.getWidth(), sprite.getHeight(), null);
    }
}
