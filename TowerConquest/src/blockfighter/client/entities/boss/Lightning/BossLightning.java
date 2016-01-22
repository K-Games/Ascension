package blockfighter.client.entities.boss.Lightning;

import blockfighter.client.Globals;
import blockfighter.client.entities.boss.Boss;
import blockfighter.client.entities.particles.Particle;
import blockfighter.client.screen.ScreenIngame;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import javax.imageio.ImageIO;

public class BossLightning extends Boss {

    private long lastParticleTime = 0;
    public static BufferedImage[][] SPRITE;
    public static final byte STATE_BOLTCHARGE = 0x03,
            STATE_BALLCHARGE = 0x04,
            STATE_ATTACK1 = 0x05,
            STATE_ATTACK2 = 0x06,
            STATE_BOLTCAST = 0x07;
    public static final byte PARTICLE_ATT1 = 0x00,
            PARTICLE_ATT2 = 0x01,
            PARTICLE_BOLT = 0x02,
            PARTICLE_BALL1 = 0x03,
            PARTICLE_BALL2 = 0x04;

    public BossLightning(int x, int y, byte k) {
        super(x, y, k);
        stats = new double[NUM_STATS];
    }

    public static void load() {
        if (SPRITE != null) {
            return;
        }
        SPRITE = new BufferedImage[8][];

        //Remove repetition later
        SPRITE[STATE_STAND] = new BufferedImage[10];
        SPRITE[STATE_WALK] = SPRITE[STATE_STAND];
        for (int i = 0; i < SPRITE[STATE_STAND].length; i++) {
            try {
                SPRITE[STATE_STAND][i] = ImageIO.read(Globals.class.getResourceAsStream("sprites/boss/lightning/stand/" + i + ".png"));
            } catch (Exception ex) {
            }
        }

        SPRITE[STATE_JUMP] = new BufferedImage[1];
        SPRITE[STATE_JUMP][0] = SPRITE[STATE_STAND][0];

        SPRITE[STATE_BOLTCHARGE] = new BufferedImage[10];
        for (int i = 0; i < SPRITE[STATE_BOLTCHARGE].length; i++) {
            try {
                SPRITE[STATE_BOLTCHARGE][i] = ImageIO.read(Globals.class.getResourceAsStream("sprites/boss/lightning/boltcharge/" + i + ".png"));
            } catch (Exception ex) {
            }
        }

        SPRITE[STATE_BALLCHARGE] = new BufferedImage[10];
        for (int i = 0; i < SPRITE[STATE_BALLCHARGE].length; i++) {
            try {
                SPRITE[STATE_BALLCHARGE][i] = ImageIO.read(Globals.class.getResourceAsStream("sprites/boss/lightning/ballcharge/" + i + ".png"));
            } catch (Exception ex) {
            }
        }

        SPRITE[STATE_ATTACK1] = new BufferedImage[10];
        for (int i = 0; i < SPRITE[STATE_ATTACK1].length; i++) {
            try {
                SPRITE[STATE_ATTACK1][i] = ImageIO.read(Globals.class.getResourceAsStream("sprites/boss/lightning/attack1/" + i + ".png"));
            } catch (Exception ex) {
            }
        }

        SPRITE[STATE_ATTACK2] = new BufferedImage[10];
        for (int i = 0; i < SPRITE[STATE_ATTACK2].length; i++) {
            try {
                SPRITE[STATE_ATTACK2][i] = ImageIO.read(Globals.class.getResourceAsStream("sprites/boss/lightning/attack2/" + i + ".png"));
            } catch (Exception ex) {
            }
        }

        SPRITE[STATE_BOLTCAST] = new BufferedImage[10];
        for (int i = 0; i < SPRITE[STATE_BOLTCAST].length; i++) {
            try {
                SPRITE[STATE_BOLTCAST][i] = ImageIO.read(Globals.class.getResourceAsStream("sprites/boss/lightning/boltcast/" + i + ".png"));
            } catch (Exception ex) {
            }
        }

        ParticleAmbient.load();
        ParticleBolt.load();
    }

    @Override
    public void unload() {
        if (SPRITE == null) {
            return;
        }
        System.out.println("Unloaded Lightning Assets");
        ParticleAmbient.unload();
        ParticleBolt.unload();
        for (int i = 0; i < SPRITE.length; i++) {
            for (int j = 0; SPRITE[i] != null && j < SPRITE[i].length; j++) {
                SPRITE[i][j] = null;
            }
            SPRITE[i] = null;
        }
        SPRITE = null;
    }

    @Override
    public void update() {
        lastParticleTime -= Globals.LOGIC_UPDATE / 1000000;
        if (lastParticleTime <= 0 && state != STATE_BALLCHARGE) {
            for (int i = 0; i < 3; i++) {
                ParticleAmbient b = new ParticleAmbient(((ScreenIngame) logic.getScreen()).getNextParticleKey(), x + (Globals.rng(300) - 200), y - (Globals.rng(200) + 150), true);
                ((ScreenIngame) logic.getScreen()).addParticle(b);
            }
            lastParticleTime = 100;
        }
    }

    @Override
    public void addParticle(byte[] data) {
        byte particleID = data[2];
        int x, y;
        Particle b;
        int pKey = ((ScreenIngame) logic.getScreen()).getNextParticleKey();
        switch (particleID) {
            case PARTICLE_ATT1:
                x = Globals.bytesToInt(Arrays.copyOfRange(data, 3, 7));
                y = Globals.bytesToInt(Arrays.copyOfRange(data, 7, 11));
                b = new ParticleAttEmitter(pKey, x, y);
                ((ScreenIngame) logic.getScreen()).addParticle(b);
                break;
            case PARTICLE_BALL1:
                x = Globals.bytesToInt(Arrays.copyOfRange(data, 3, 7));
                y = Globals.bytesToInt(Arrays.copyOfRange(data, 7, 11));
                b = new ParticleBallEmitter(pKey, x, y);
                ((ScreenIngame) logic.getScreen()).addParticle(b);
                break;
            case PARTICLE_BOLT:
                x = Globals.bytesToInt(Arrays.copyOfRange(data, 3, 7));
                y = Globals.bytesToInt(Arrays.copyOfRange(data, 7, 11));
                b = new ParticleBolt(pKey, x, y);
                ((ScreenIngame) logic.getScreen()).addParticle(b);
                break;
        }
    }

    @Override
    public void draw(Graphics2D g) {
        byte s = state, f = frame;
        if (SPRITE == null || f >= SPRITE[s].length) {
            return;
        }
        BufferedImage sprite = SPRITE[s][f];
        int drawSrcX = x - ((facing == Globals.RIGHT) ? 1 : -1) * sprite.getWidth() / 2;
        int drawSrcY = y - sprite.getHeight();

        switch (s) {
            case STATE_BALLCHARGE:
                drawSrcY += 70;
                drawSrcX += ((facing == Globals.RIGHT) ? 1 : -1) * 50;
                break;
            case STATE_ATTACK1:
                drawSrcX += ((facing == Globals.RIGHT) ? 1 : -1) * 50;
                break;
            case STATE_ATTACK2:
                drawSrcX += ((facing == Globals.RIGHT) ? 1 : -1) * 100;
                break;
        }
        int drawDscX = drawSrcX + ((facing == Globals.RIGHT) ? 1 : -1) * sprite.getWidth();
        int drawDscY = drawSrcY + sprite.getHeight();
        g.drawImage(sprite, drawSrcX, drawSrcY, drawDscX, drawDscY, 0, 0, sprite.getWidth(), sprite.getHeight(), null);
        g.setColor(Color.WHITE);
        g.drawRect(x - 121, y - 361, 201, 31);
        if (stats[STAT_MINHP] / stats[STAT_MAXHP] > .66) {
            g.setColor(new Color(90, 255, 0));
        } else if (stats[STAT_MINHP] / stats[STAT_MAXHP] > .33) {
            g.setColor(new Color(255, 165, 0));
        } else {
            g.setColor(new Color(255, 0, 0));
        }

        g.fillRect(x - 120, y - 360, (int) (stats[STAT_MINHP] / stats[STAT_MAXHP] * 200), 30);
    }
}
