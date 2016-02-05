package blockfighter.client.entities.particles;

import blockfighter.client.Globals;
import blockfighter.client.entities.player.Player;
import blockfighter.client.screen.ScreenIngame;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;

public class ParticleSwordTauntAura extends Particle {

    private final Player owner;

    public ParticleSwordTauntAura(final int k, final Player p) {
        super(k, 0, 0);
        this.frame = 0;
        this.frameDuration = 25;
        this.duration = 500;
        this.owner = p;
        final Point point = this.owner.getPos();
        if (point != null) {
            this.x = point.x;
            this.y = point.y;
        }
    }

    @Override
    public void update() {
        super.update();

        if (this.duration > 100) {
            for (int i = 0; i < 2; i++) {
                final ParticleSwordTauntAuraParticle b = new ParticleSwordTauntAuraParticle(
                        ((ScreenIngame) logic.getScreen()).getNextParticleKey(), this.x, this.y, this.facing);
                ((ScreenIngame) logic.getScreen()).addParticle(b);
            }
        }
        if (Globals.nsToMs(logic.getTime() - this.lastFrameTime) >= this.frameDuration) {
            this.frameDuration = 25;
            if (this.frame < PARTICLE_SPRITE[Globals.PARTICLE_SWORD_TAUNTAURA1].length - 1) {
                this.frame++;
            }
            this.lastFrameTime = logic.getTime();
        }
    }

    @Override
    public void draw(final Graphics2D g) {
        if (PARTICLE_SPRITE[Globals.PARTICLE_SWORD_TAUNTAURA1] == null) {
            return;
        }
        if (this.frame >= PARTICLE_SPRITE[Globals.PARTICLE_SWORD_TAUNTAURA1].length) {
            return;
        }
        final Point p = this.owner.getPos();
        if (p != null) {
            this.x = p.x;
            this.y = p.y;
        }
        final BufferedImage sprite = PARTICLE_SPRITE[Globals.PARTICLE_SWORD_TAUNTAURA1][this.frame];
        final int drawSrcX = this.x - sprite.getWidth() / 2;
        final int drawSrcY = this.y - sprite.getHeight() + 20;
        final int drawDscY = drawSrcY + sprite.getHeight();
        final int drawDscX = drawSrcX + sprite.getWidth();
        g.drawImage(sprite, drawSrcX, drawSrcY, drawDscX, drawDscY, 0, 0, sprite.getWidth(), sprite.getHeight(), null);
    }
}
