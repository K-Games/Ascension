package blockfighter.client.entities.mob.boss.Lightning;

import blockfighter.client.Globals;
import blockfighter.client.entities.mob.Mob;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class BossLightning extends Mob {

    private long lastParticleTime = 0;
    public static BufferedImage[][] SPRITE;

    public static final byte ANIM_BOLTCHARGE = 0x05,
            ANIM_BALLCHARGE = 0x06,
            ANIM_ATTACK1 = 0x07,
            ANIM_ATTACK2 = 0x08,
            ANIM_BOLTCAST = 0x09;

    public static final byte PARTICLE_ATT1 = 0x00,
            PARTICLE_ATT2 = 0x01,
            PARTICLE_BOLT = 0x02,
            PARTICLE_BALL1 = 0x03,
            PARTICLE_BALL2 = 0x04;

    public BossLightning(final int x, final int y, final int k) {
        super(x, y, k);
        this.stats = new double[NUM_STATS];
    }

    public static void load() {
        if (SPRITE != null) {
            return;
        }
        SPRITE = new BufferedImage[10][];

        SPRITE[ANIM_STAND] = new BufferedImage[10];
        SPRITE[ANIM_WALK] = SPRITE[ANIM_STAND];
        for (int i = 0; i < SPRITE[ANIM_STAND].length; i++) {
            try {
                SPRITE[ANIM_STAND][i] = Globals.loadTextureResource("sprites/mob/bosslightning/stand/" + i + ".png");
            } catch (final Exception ex) {
            }
        }

        SPRITE[ANIM_JUMP] = new BufferedImage[1];
        SPRITE[ANIM_JUMP][0] = SPRITE[ANIM_STAND][0];

        SPRITE[ANIM_BOLTCHARGE] = new BufferedImage[10];
        for (int i = 0; i < SPRITE[ANIM_BOLTCHARGE].length; i++) {
            SPRITE[ANIM_BOLTCHARGE][i] = Globals.loadTextureResource("sprites/mob/bosslightning/boltcharge/" + i + ".png");
        }

        SPRITE[ANIM_BALLCHARGE] = new BufferedImage[10];
        for (int i = 0; i < SPRITE[ANIM_BALLCHARGE].length; i++) {
            SPRITE[ANIM_BALLCHARGE][i] = Globals.loadTextureResource("sprites/mob/bosslightning/ballcharge/" + i + ".png");
        }

        SPRITE[ANIM_ATTACK1] = new BufferedImage[10];
        for (int i = 0; i < SPRITE[ANIM_ATTACK1].length; i++) {
            SPRITE[ANIM_ATTACK1][i] = Globals.loadTextureResource("sprites/mob/bosslightning/attack1/" + i + ".png");
        }

        SPRITE[ANIM_ATTACK2] = new BufferedImage[10];
        for (int i = 0; i < SPRITE[ANIM_ATTACK2].length; i++) {
            SPRITE[ANIM_ATTACK2][i] = Globals.loadTextureResource("sprites/mob/bosslightning/attack2/" + i + ".png");
        }

        SPRITE[ANIM_BOLTCAST] = new BufferedImage[10];
        for (int i = 0; i < SPRITE[ANIM_BOLTCAST].length; i++) {
            SPRITE[ANIM_BOLTCAST][i] = Globals.loadTextureResource("sprites/mob/bosslightning/boltcast/" + i + ".png");
        }

        ParticleAmbient.load();
        ParticleBolt.load();
    }

    public static void prerender(final Graphics2D g) {
        for (BufferedImage[] animState : SPRITE) {
            if (animState != null) {
                for (BufferedImage frame : animState) {
                    g.drawImage(frame, 0, 0, null);
                }
            }
        }
    }

    @Override
    public void unload() {
        if (SPRITE == null) {
            return;
        }
        System.out.println("Unloaded Lightning Assets...");
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
        if (Globals.nsToMs(logic.getTime() - lastParticleTime) >= 100 && this.animState != ANIM_BALLCHARGE) {
            for (int i = 0; i < 3; i++) {
                final ParticleAmbient b = new ParticleAmbient(this.x + (Globals.rng(300) - 200), this.y - (Globals.rng(200) + 150), true);
                logic.getScreen().addParticle(b);
            }
            this.lastParticleTime = logic.getTime();
        }
    }

    @Override
    public void addParticle(final byte[] data) {
    }

    @Override
    public void draw(final Graphics2D g) {
        final byte s = this.animState, f = this.frame;
        if (SPRITE == null || f >= SPRITE[s].length) {
            return;
        }
        final BufferedImage sprite = SPRITE[s][f];
        int drawSrcX = this.x - ((this.facing == Globals.RIGHT) ? 1 : -1) * sprite.getWidth() / 2;
        int drawSrcY = this.y - sprite.getHeight();

        switch (s) {
            case ANIM_BALLCHARGE:
                drawSrcY += 70;
                drawSrcX += ((this.facing == Globals.RIGHT) ? 1 : -1) * 50;
                break;
            case ANIM_ATTACK1:
                drawSrcX += ((this.facing == Globals.RIGHT) ? 1 : -1) * 50;
                break;
            case ANIM_ATTACK2:
                drawSrcX += ((this.facing == Globals.RIGHT) ? 1 : -1) * 100;
                break;
        }
        final int drawDscX = drawSrcX + ((this.facing == Globals.RIGHT) ? 1 : -1) * sprite.getWidth();
        final int drawDscY = drawSrcY + sprite.getHeight();
        g.drawImage(sprite, drawSrcX, drawSrcY, drawDscX, drawDscY, 0, 0, sprite.getWidth(), sprite.getHeight(), null);
        g.setColor(Color.WHITE);
        g.drawRect(this.x - 121, this.y - 361, 201, 31);
        if (this.stats[STAT_MINHP] / this.stats[STAT_MAXHP] > .66) {
            g.setColor(new Color(90, 255, 0));
        } else if (this.stats[STAT_MINHP] / this.stats[STAT_MAXHP] > .33) {
            g.setColor(new Color(255, 165, 0));
        } else {
            g.setColor(new Color(255, 0, 0));
        }

        g.fillRect(this.x - 120, this.y - 360, (int) (this.stats[STAT_MINHP] / this.stats[STAT_MAXHP] * 200), 30);
    }
}
