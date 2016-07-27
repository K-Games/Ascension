package blockfighter.client.entities.mob.boss.Lightning;

import blockfighter.client.Globals;
import blockfighter.client.entities.particles.Particle;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class ParticleBolt extends Particle {

    public static BufferedImage[] SPRITE;

    public ParticleBolt(final int x, final int y) {
        super(x, y, Globals.RIGHT);
        this.frame = 0;
        this.frameDuration = 50;
        this.duration = 400;
    }

    public static void prerender(final Graphics2D g) {
        for (BufferedImage frame : SPRITE) {
            g.drawImage(frame, 0, 0, null);
        }
    }

    public static void load() {
        if (SPRITE != null) {
            return;
        }
        SPRITE = new BufferedImage[8];
        for (int i = 0; i < SPRITE.length; i++) {
            SPRITE[i] = Globals.loadTextureResource("sprites/mob/bosslightning/particle/bolt/" + i + ".png");
        }
    }

    public static void unload() {
        if (SPRITE == null) {
            return;
        }
        for (int i = 0; i < SPRITE.length; i++) {
            SPRITE[i] = null;
        }
        SPRITE = null;
    }

    @Override
    public void update() {
        super.update();
        if (Globals.nsToMs(logic.getTime() - this.lastFrameTime) >= this.frameDuration) {
            if (this.frame < SPRITE.length) {
                this.frame++;
            }
            this.lastFrameTime = logic.getTime();
        }
        if (Globals.nsToMs(logic.getTime() - this.particleStartTime) >= 150) {
            for (int i = 0; i < 30; i++) {
                final ParticleBoltParticle b = new ParticleBoltParticle(this.x + 150,
                        this.y + 1100);
                logic.getScreen().addParticle(b);
            }
        }
    }

    @Override
    public void draw(final Graphics2D g) {
        if (SPRITE == null) {
            return;
        }
        if (this.frame >= SPRITE.length) {
            return;
        }
        final BufferedImage sprite = SPRITE[this.frame];
        final int drawSrcX = this.x;
        final int drawSrcY = this.y;
        final int drawDstX = drawSrcX + 300;
        final int drawDstY = drawSrcY + 1200;
        g.drawImage(sprite, drawSrcX, drawSrcY, drawDstX, drawDstY, 0, 0, sprite.getWidth(), sprite.getHeight(), null);
    }
}
