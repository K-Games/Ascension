package blockfighter.client.entities.particles;

import blockfighter.shared.Globals;
import blockfighter.client.entities.player.Player;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;

public class ParticleBowVolleyBow extends Particle {

    public ParticleBowVolleyBow(final byte f, final Player p) {
        super(f, p);
        this.frame = 0;
        this.duration = 250;
        this.frameDuration = 25;
    }

    @Override
    public void update() {
        super.update();
        if (Globals.nsToMs(logic.getTime() - this.lastFrameTime) >= this.frameDuration) {
            if (PARTICLE_SPRITE != null && this.frame < PARTICLE_SPRITE[Globals.PARTICLE_BOW_VOLLEYBOW].length) {
                this.frame++;
            }
            this.lastFrameTime = logic.getTime();
        }
    }

    @Override
    public void draw(final Graphics2D g) {
        if (PARTICLE_SPRITE[Globals.PARTICLE_BOW_VOLLEYBOW] == null) {
            return;
        }
        if (this.frame >= PARTICLE_SPRITE[Globals.PARTICLE_BOW_VOLLEYBOW].length) {
            return;
        }
        final Point p = this.owner.getPos();
        if (p != null) {
            this.x = p.x + ((this.facing == Globals.RIGHT) ? -15 : 15);
            this.y = p.y - 75;
        }
        final BufferedImage sprite = PARTICLE_SPRITE[Globals.PARTICLE_BOW_VOLLEYBOW][this.frame];
        final int drawSrcX = this.x + ((this.facing == Globals.RIGHT) ? -sprite.getWidth() / 2 : sprite.getWidth() / 2);
        final int drawSrcY = this.y - sprite.getHeight() / 2;
        final int drawDscY = drawSrcY + sprite.getHeight();
        final int drawDscX = drawSrcX + ((this.facing == Globals.RIGHT) ? sprite.getWidth() : -sprite.getWidth());
        g.drawImage(sprite, drawSrcX, drawSrcY, drawDscX, drawDscY, 0, 0, sprite.getWidth(), sprite.getHeight(), null);
    }
}
