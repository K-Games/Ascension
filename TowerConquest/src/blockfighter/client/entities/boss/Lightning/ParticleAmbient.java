package blockfighter.client.entities.boss.Lightning;

import blockfighter.client.Globals;
import blockfighter.client.entities.particles.Particle;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class ParticleAmbient extends Particle {

    public static BufferedImage[] SPRITE;
    private boolean small = false;

    public ParticleAmbient(int k, int x, int y) {
        super(k, x, y, Globals.RIGHT);
        frame = rng.nextInt(12) * 6;
        frameDuration = 50;
        duration = 300;
    }

    public ParticleAmbient(int k, int x, int y, boolean set) {
        this(k, x, y);
        small = set;
    }

    public static void load() {
        if (SPRITE != null) {
            return;
        }
        SPRITE = new BufferedImage[72];
        for (int i = 0; i < SPRITE.length; i++) {
            try {
                SPRITE[i] = ImageIO.read(Globals.class.getResourceAsStream("sprites/boss/lightning/particle/ambient/" + i + ".png"));
            } catch (Exception ex) {
            }
        }
    }

    public static void unload() {
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
            if (frame % 6 < 5) {
                frame++;
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
        int drawDscY = (int) (drawSrcY + sprite.getHeight() * ((small) ? 0.5 : 1));
        int drawDscX = (int) (drawSrcX + sprite.getWidth() * ((small) ? 0.5 : 1));
        g.drawImage(sprite, drawSrcX, drawSrcY, drawDscX, drawDscY, 0, 0, sprite.getWidth(), sprite.getHeight(), null);
    }
}
