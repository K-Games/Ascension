package blockfighter.client.entities.boss.Lightning;

import blockfighter.client.Globals;
import blockfighter.client.entities.particles.Particle;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class ParticleAmbient extends Particle {

    public static BufferedImage[] SPRITE;
    private boolean small = false;

    public ParticleAmbient(final int k, final int x, final int y) {
        super(k, x, y, Globals.RIGHT);
        this.frame = Globals.rng(12) * 6;
        this.frameDuration = 50;
        this.duration = 300;
    }

    public ParticleAmbient(final int k, final int x, final int y, final boolean set) {
        this(k, x, y);
        this.small = set;
    }

    public static void load() {
        if (SPRITE != null) {
            return;
        }
        SPRITE = new BufferedImage[72];
        for (int i = 0; i < SPRITE.length; i++) {
            try {
                SPRITE[i] = ImageIO.read(Globals.class.getResourceAsStream("sprites/boss/lightning/particle/ambient/" + i + ".png"));
            } catch (final Exception ex) {
            }
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
