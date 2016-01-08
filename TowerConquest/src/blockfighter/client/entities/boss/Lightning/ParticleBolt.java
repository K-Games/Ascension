package blockfighter.client.entities.boss.Lightning;

import blockfighter.client.Globals;
import blockfighter.client.entities.particles.Particle;
import blockfighter.client.screen.ScreenIngame;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class ParticleBolt extends Particle {

    public static BufferedImage[] SPRITE;

    public ParticleBolt(int k, int x, int y) {
        super(k, x, y, Globals.RIGHT);
        frame = 0;
        frameDuration = 50;
        duration = 400;
    }

    public static void load() {
        if (SPRITE != null) {
            return;
        }
        SPRITE = new BufferedImage[8];
        for (int i = 0; i < SPRITE.length; i++) {
            try {
                SPRITE[i] = ImageIO.read(Globals.class.getResourceAsStream("sprites/boss/lightning/particle/bolt/" + i + ".png"));
            } catch (Exception ex) {
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
        frameDuration -= Globals.LOGIC_UPDATE / 1000000;
        if (frameDuration <= 0) {
            frameDuration = 50;
            if (frame < SPRITE.length) {
                frame++;
            }
        }
        if (duration == 250) {
            for (int i = 0; i < 30; i++) {
                ParticleBoltParticle b = new ParticleBoltParticle(((ScreenIngame) logic.getScreen()).getNextParticleKey(), x + 150, y + 1100);
                ((ScreenIngame) logic.getScreen()).addParticle(b);
            }
        }
    }

    @Override
    public void draw(Graphics2D g) {
        if (SPRITE == null) {
            return;
        }
        if (frame >= SPRITE.length) {
            return;
        }
        BufferedImage sprite = SPRITE[frame];
        int drawSrcX = x;
        int drawSrcY = y;
        int drawDstX = drawSrcX + 300;
        int drawDstY = drawSrcY + 1200;
        g.drawImage(sprite, drawSrcX, drawSrcY, drawDstX, drawDstY, 0, 0, sprite.getWidth(), sprite.getHeight(), null);
    }
}
