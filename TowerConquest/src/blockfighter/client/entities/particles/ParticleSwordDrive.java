package blockfighter.client.entities.particles;

import blockfighter.client.Globals;
import blockfighter.client.entities.player.Player;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;

public class ParticleSwordDrive extends Particle {

    private final Player owner;

    public ParticleSwordDrive(final int k, final byte f, final Player p) {
        super(k, 0, 0, f);
        this.frame = 0;
        this.frameDuration = 25;
        this.duration = 300;
        this.owner = p;
    }

    @Override
    public void update() {
        super.update();
        this.frameDuration -= Globals.LOGIC_UPDATE / 1000000;
        if (this.frameDuration <= 0) {
            this.frameDuration = 25;
            this.frame++;
            if (this.frame >= PARTICLE_SPRITE[Globals.PARTICLE_SWORD_DRIVE].length - 1) {
                this.frame = 0;
            }
        }
    }

    @Override
    public void draw(final Graphics2D g) {
        if (PARTICLE_SPRITE[Globals.PARTICLE_SWORD_DRIVE] == null) {
            return;
        }
        if (this.frame >= PARTICLE_SPRITE[Globals.PARTICLE_SWORD_DRIVE].length) {
            return;
        }
        final Point p = this.owner.getPos();
        if (p != null) {
            if (this.facing == Globals.RIGHT) {
                this.x = p.x - 310;
            } else {
                this.x = p.x - 560 + 310;
            }
        }
        if (p != null) {
            this.y = p.y - 140;
        }
        final BufferedImage sprite = PARTICLE_SPRITE[Globals.PARTICLE_SWORD_DRIVE][this.frame];
        final int drawSrcX = this.x + ((this.facing == Globals.RIGHT) ? 0 : sprite.getWidth());
        final int drawSrcY = this.y - 70;
        final int drawDscY = drawSrcY + sprite.getHeight();
        final int drawDscX = this.x + ((this.facing == Globals.RIGHT) ? sprite.getWidth() : 0);
        g.drawImage(sprite, drawSrcX, drawSrcY, drawDscX, drawDscY, 0, 0, sprite.getWidth(), sprite.getHeight(), null);
    }
}
