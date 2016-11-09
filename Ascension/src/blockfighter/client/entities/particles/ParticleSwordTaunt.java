package blockfighter.client.entities.particles;

import blockfighter.shared.Globals;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class ParticleSwordTaunt extends Particle {

    public ParticleSwordTaunt(final int x, final int y, final byte f) {
        super(x, y, f);
        this.frame = 0;
        this.frameDuration = 50;
        this.duration = 300;
    }

    @Override
    public void update() {
        super.update();

        if (Globals.nsToMs(logic.getTime() - this.lastFrameTime) >= this.frameDuration) {

            if (PARTICLE_SPRITE != null && this.frame < PARTICLE_SPRITE[Globals.PARTICLE_SWORD_TAUNT].length) {
                this.frame++;
                this.frameDuration = (this.frame == 2) ? 100 : 50;
            }
            this.lastFrameTime = logic.getTime();
        }
    }

    @Override
    public void draw(final Graphics2D g) {
        if (PARTICLE_SPRITE[Globals.PARTICLE_SWORD_TAUNT] == null) {
            return;
        }
        if (this.frame >= PARTICLE_SPRITE[Globals.PARTICLE_SWORD_TAUNT].length) {
            return;
        }
        final BufferedImage sprite = PARTICLE_SPRITE[Globals.PARTICLE_SWORD_TAUNT][this.frame];
        final int drawSrcX = this.x + ((this.facing == Globals.RIGHT) ? -170 : 170);
        final int drawSrcY = this.y - sprite.getHeight() + 80;
        final int drawDscY = drawSrcY + sprite.getHeight();
        final int drawDscX = drawSrcX + ((this.facing == Globals.RIGHT) ? sprite.getWidth() : -sprite.getWidth());
        g.drawImage(sprite, drawSrcX, drawSrcY, drawDscX, drawDscY, 0, 0, sprite.getWidth(), sprite.getHeight(), null);
    }
}
