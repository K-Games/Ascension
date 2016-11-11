package blockfighter.client.entities.mob.boss.Lightning;

import blockfighter.client.entities.particles.Particle;
import blockfighter.shared.Globals;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class ParticleAmbient extends Particle {

    public static BufferedImage[] SPRITE;
    private boolean small = false;

    public ParticleAmbient(final int x, final int y) {
        super(x, y, Globals.RIGHT);
        this.frame = Globals.rng(12) * 6;
        this.frameDuration = 50;
        this.duration = 300;
    }

    public ParticleAmbient(final int x, final int y, final boolean set) {
        this(x, y);
        this.small = set;
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
        SPRITE = new BufferedImage[72];
        for (int i = 0; i < SPRITE.length; i++) {
            SPRITE[i] = Globals.loadTextureResource("sprites/mob/bosslightning/particle/ambient/" + i + ".png");
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
            if (this.frame % 6 < 5) {
                this.frame++;
            }
            this.lastFrameTime = logic.getTime();
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
        final int drawDscY = (int) (drawSrcY + sprite.getHeight() * ((this.small) ? 0.5 : 1));
        final int drawDscX = (int) (drawSrcX + sprite.getWidth() * ((this.small) ? 0.5 : 1));
        g.drawImage(sprite, drawSrcX, drawSrcY, drawDscX, drawDscY, 0, 0, sprite.getWidth(), sprite.getHeight(), null);
    }
}
