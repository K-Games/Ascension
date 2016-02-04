package blockfighter.client.entities.particles;

import blockfighter.client.Globals;
import static blockfighter.client.Globals.NUM_PARTICLE_EFFECTS;
import blockfighter.client.LogicModule;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

/**
 *
 * @author Ken Kwan
 */
public abstract class Particle extends Thread {

    protected int x, y;
    protected double size = 10.0;
    protected long frameDuration;
    protected int frame = 0;
    protected byte facing = Globals.RIGHT;

    protected static BufferedImage[][] PARTICLE_SPRITE;
    protected final int key;
    private static boolean LOADED = false;
    /**
     * Reference to Logic Module.
     */
    protected static LogicModule logic;

    /**
     * The duration of this particle in ms.
     */
    protected long duration;

    public static void setLogic(final LogicModule l) {
        logic = l;
    }

    public static void unloadParticles() {
        for (int i = 0; PARTICLE_SPRITE != null && i < PARTICLE_SPRITE.length; i++) {
            for (int j = 0; PARTICLE_SPRITE[i] != null && j < PARTICLE_SPRITE[i].length; j++) {
                PARTICLE_SPRITE[i][j] = null;
            }
            PARTICLE_SPRITE[i] = null;
        }
        PARTICLE_SPRITE = null;
        LOADED = false;
        System.gc();
    }

    public static void loadParticles() throws Exception {
        if (LOADED) {
            return;
        }
        unloadParticles();
        LOADED = true;
        // Remove repetition later
        PARTICLE_SPRITE = new BufferedImage[NUM_PARTICLE_EFFECTS][];
        PARTICLE_SPRITE[Globals.PARTICLE_SWORD_SLASH1] = new BufferedImage[5];
        for (int i = 0; i < PARTICLE_SPRITE[Globals.PARTICLE_SWORD_SLASH1].length; i++) {
            PARTICLE_SPRITE[Globals.PARTICLE_SWORD_SLASH1][i] = ImageIO
                    .read(Globals.class.getResourceAsStream("sprites/particle/slash1/" + i + ".png"));
        }
        PARTICLE_SPRITE[Globals.PARTICLE_SWORD_SLASH2] = new BufferedImage[5];
        for (int i = 0; i < PARTICLE_SPRITE[Globals.PARTICLE_SWORD_SLASH2].length; i++) {
            PARTICLE_SPRITE[Globals.PARTICLE_SWORD_SLASH2][i] = ImageIO
                    .read(Globals.class.getResourceAsStream("sprites/particle/slash2/" + i + ".png"));
        }
        PARTICLE_SPRITE[Globals.PARTICLE_SWORD_SLASH3] = new BufferedImage[5];
        for (int i = 0; i < PARTICLE_SPRITE[Globals.PARTICLE_SWORD_SLASH3].length; i++) {
            PARTICLE_SPRITE[Globals.PARTICLE_SWORD_SLASH3][i] = ImageIO
                    .read(Globals.class.getResourceAsStream("sprites/particle/slash3/" + i + ".png"));
        }
        PARTICLE_SPRITE[Globals.PARTICLE_SWORD_GASH1] = new BufferedImage[4];
        for (int i = 0; i < PARTICLE_SPRITE[Globals.PARTICLE_SWORD_GASH1].length; i++) {
            PARTICLE_SPRITE[Globals.PARTICLE_SWORD_GASH1][i] = ImageIO
                    .read(Globals.class.getResourceAsStream("sprites/particle/gash1/" + i + ".png"));
        }
        PARTICLE_SPRITE[Globals.PARTICLE_SWORD_GASH2] = new BufferedImage[4];
        for (int i = 0; i < PARTICLE_SPRITE[Globals.PARTICLE_SWORD_GASH2].length; i++) {
            PARTICLE_SPRITE[Globals.PARTICLE_SWORD_GASH2][i] = ImageIO
                    .read(Globals.class.getResourceAsStream("sprites/particle/gash2/" + i + ".png"));
        }
        PARTICLE_SPRITE[Globals.PARTICLE_SWORD_GASH3] = new BufferedImage[4];
        for (int i = 0; i < PARTICLE_SPRITE[Globals.PARTICLE_SWORD_GASH3].length; i++) {
            PARTICLE_SPRITE[Globals.PARTICLE_SWORD_GASH3][i] = ImageIO
                    .read(Globals.class.getResourceAsStream("sprites/particle/gash3/" + i + ".png"));
        }
        PARTICLE_SPRITE[Globals.PARTICLE_SWORD_GASH4] = new BufferedImage[4];
        for (int i = 0; i < PARTICLE_SPRITE[Globals.PARTICLE_SWORD_GASH4].length; i++) {
            PARTICLE_SPRITE[Globals.PARTICLE_SWORD_GASH4][i] = ImageIO
                    .read(Globals.class.getResourceAsStream("sprites/particle/gash4/" + i + ".png"));
        }
        PARTICLE_SPRITE[Globals.PARTICLE_SWORD_VORPAL] = new BufferedImage[8];
        for (int i = 0; i < PARTICLE_SPRITE[Globals.PARTICLE_SWORD_VORPAL].length; i++) {
            PARTICLE_SPRITE[Globals.PARTICLE_SWORD_VORPAL][i] = ImageIO
                    .read(Globals.class.getResourceAsStream("sprites/particle/vorpal/" + i + ".png"));
        }
        PARTICLE_SPRITE[Globals.PARTICLE_SWORD_PHANTOM] = new BufferedImage[8];
        for (int i = 0; i < PARTICLE_SPRITE[Globals.PARTICLE_SWORD_PHANTOM].length; i++) {
            PARTICLE_SPRITE[Globals.PARTICLE_SWORD_PHANTOM][i] = ImageIO
                    .read(Globals.class.getResourceAsStream("sprites/particle/phantom/" + i + ".png"));
        }
        PARTICLE_SPRITE[Globals.PARTICLE_SWORD_PHANTOM2] = new BufferedImage[8];
        for (int i = 0; i < PARTICLE_SPRITE[Globals.PARTICLE_SWORD_PHANTOM2].length; i++) {
            PARTICLE_SPRITE[Globals.PARTICLE_SWORD_PHANTOM2][i] = ImageIO
                    .read(Globals.class.getResourceAsStream("sprites/particle/phantomslash/" + i + ".png"));
        }
        //PARTICLE_SPRITE[Globals.PARTICLE_SWORD_MULTI] = new BufferedImage[24];
        //for (int i = 0; i < PARTICLE_SPRITE[Globals.PARTICLE_SWORD_MULTI].length; i++) {
        //    PARTICLE_SPRITE[Globals.PARTICLE_SWORD_MULTI][i] = ImageIO
        //            .read(Globals.class.getResourceAsStream("sprites/particle/multi/" + i + ".png"));
        //}
        PARTICLE_SPRITE[Globals.PARTICLE_SWORD_CINDER] = new BufferedImage[8];
        for (int i = 0; i < PARTICLE_SPRITE[Globals.PARTICLE_SWORD_CINDER].length; i++) {
            PARTICLE_SPRITE[Globals.PARTICLE_SWORD_CINDER][i] = ImageIO
                    .read(Globals.class.getResourceAsStream("sprites/particle/cinder/" + i + ".png"));
        }
        PARTICLE_SPRITE[Globals.PARTICLE_BURN] = new BufferedImage[20];
        for (int i = 0; i < PARTICLE_SPRITE[Globals.PARTICLE_BURN].length; i++) {
            PARTICLE_SPRITE[Globals.PARTICLE_BURN][i] = ImageIO
                    .read(Globals.class.getResourceAsStream("sprites/particle/burn/" + i + ".png"));
        }
        PARTICLE_SPRITE[Globals.PARTICLE_SWORD_TAUNT] = new BufferedImage[16];
        for (int i = 0; i < PARTICLE_SPRITE[Globals.PARTICLE_SWORD_TAUNT].length; i++) {
            PARTICLE_SPRITE[Globals.PARTICLE_SWORD_TAUNT][i] = ImageIO
                    .read(Globals.class.getResourceAsStream("sprites/particle/taunt/" + i + ".png"));
        }
        PARTICLE_SPRITE[Globals.PARTICLE_SWORD_TAUNTAURA1] = new BufferedImage[20];
        for (int i = 0; i < PARTICLE_SPRITE[Globals.PARTICLE_SWORD_TAUNTAURA1].length; i++) {
            PARTICLE_SPRITE[Globals.PARTICLE_SWORD_TAUNTAURA1][i] = ImageIO
                    .read(Globals.class.getResourceAsStream("sprites/particle/tauntaura/" + i + ".png"));
        }
        PARTICLE_SPRITE[Globals.PARTICLE_SWORD_TAUNTAURA2] = new BufferedImage[10];
        for (int i = 0; i < PARTICLE_SPRITE[Globals.PARTICLE_SWORD_TAUNTAURA2].length; i++) {
            PARTICLE_SPRITE[Globals.PARTICLE_SWORD_TAUNTAURA2][i] = ImageIO
                    .read(Globals.class.getResourceAsStream("sprites/particle/tauntaura2/" + i + ".png"));
        }
        PARTICLE_SPRITE[Globals.PARTICLE_BOW_ARC] = new BufferedImage[8];
        for (int i = 0; i < PARTICLE_SPRITE[Globals.PARTICLE_BOW_ARC].length; i++) {
            PARTICLE_SPRITE[Globals.PARTICLE_BOW_ARC][i] = ImageIO
                    .read(Globals.class.getResourceAsStream("sprites/particle/arc/" + i + ".png"));
        }
        PARTICLE_SPRITE[Globals.PARTICLE_BOW_RAPID] = new BufferedImage[6];
        for (int i = 0; i < PARTICLE_SPRITE[Globals.PARTICLE_BOW_RAPID].length; i++) {
            PARTICLE_SPRITE[Globals.PARTICLE_BOW_RAPID][i] = ImageIO
                    .read(Globals.class.getResourceAsStream("sprites/particle/rapid/" + i + ".png"));
        }
        PARTICLE_SPRITE[Globals.PARTICLE_BOW_POWER] = new BufferedImage[24];
        for (int i = 0; i < PARTICLE_SPRITE[Globals.PARTICLE_BOW_POWER].length; i++) {
            PARTICLE_SPRITE[Globals.PARTICLE_BOW_POWER][i] = ImageIO
                    .read(Globals.class.getResourceAsStream("sprites/particle/power/" + i + ".png"));
        }
        PARTICLE_SPRITE[Globals.PARTICLE_BOW_POWERPARTICLE] = new BufferedImage[10];
        for (int i = 0; i < PARTICLE_SPRITE[Globals.PARTICLE_BOW_POWERPARTICLE].length; i++) {
            PARTICLE_SPRITE[Globals.PARTICLE_BOW_POWERPARTICLE][i] = ImageIO
                    .read(Globals.class.getResourceAsStream("sprites/particle/power2/" + i + ".png"));
        }
        PARTICLE_SPRITE[Globals.PARTICLE_BOW_POWERCHARGE] = new BufferedImage[24];
        for (int i = 0; i < PARTICLE_SPRITE[Globals.PARTICLE_BOW_POWERCHARGE].length; i++) {
            PARTICLE_SPRITE[Globals.PARTICLE_BOW_POWERCHARGE][i] = ImageIO
                    .read(Globals.class.getResourceAsStream("sprites/particle/powercharge/" + i + ".png"));
        }
        PARTICLE_SPRITE[Globals.PARTICLE_BOW_VOLLEYBOW] = new BufferedImage[1];
        for (int i = 0; i < PARTICLE_SPRITE[Globals.PARTICLE_BOW_VOLLEYBOW].length; i++) {
            PARTICLE_SPRITE[Globals.PARTICLE_BOW_VOLLEYBOW][i] = ImageIO
                    .read(Globals.class.getResourceAsStream("sprites/particle/volleybow/" + i + ".png"));
        }
        PARTICLE_SPRITE[Globals.PARTICLE_BOW_VOLLEYARROW] = new BufferedImage[6];
        for (int i = 0; i < PARTICLE_SPRITE[Globals.PARTICLE_BOW_VOLLEYARROW].length; i++) {
            PARTICLE_SPRITE[Globals.PARTICLE_BOW_VOLLEYARROW][i] = ImageIO
                    .read(Globals.class.getResourceAsStream("sprites/particle/volleyarrow/" + i + ".png"));
        }
        PARTICLE_SPRITE[Globals.PARTICLE_BOW_STORM] = new BufferedImage[20];
        for (int i = 0; i < PARTICLE_SPRITE[Globals.PARTICLE_BOW_STORM].length; i++) {
            PARTICLE_SPRITE[Globals.PARTICLE_BOW_STORM][i] = ImageIO
                    .read(Globals.class.getResourceAsStream("sprites/particle/stormarrow/" + i + ".png"));
        }
        PARTICLE_SPRITE[Globals.PARTICLE_BOW_FROSTARROW] = new BufferedImage[12];
        for (int i = 0; i < PARTICLE_SPRITE[Globals.PARTICLE_BOW_FROSTARROW].length; i++) {
            PARTICLE_SPRITE[Globals.PARTICLE_BOW_FROSTARROW][i] = ImageIO
                    .read(Globals.class.getResourceAsStream("sprites/particle/frostarrow/" + i + ".png"));
        }
        PARTICLE_SPRITE[Globals.PARTICLE_SHIELD_DASH] = new BufferedImage[15];
        for (int i = 0; i < PARTICLE_SPRITE[Globals.PARTICLE_SHIELD_DASH].length; i++) {
            PARTICLE_SPRITE[Globals.PARTICLE_SHIELD_DASH][i] = ImageIO
                    .read(Globals.class.getResourceAsStream("sprites/particle/dash/" + i + ".png"));
        }
        PARTICLE_SPRITE[Globals.PARTICLE_SHIELD_FORTIFY] = new BufferedImage[20];
        for (int i = 0; i < PARTICLE_SPRITE[Globals.PARTICLE_SHIELD_FORTIFY].length; i++) {
            PARTICLE_SPRITE[Globals.PARTICLE_SHIELD_FORTIFY][i] = ImageIO
                    .read(Globals.class.getResourceAsStream("sprites/particle/fortify/" + i + ".png"));
        }
        PARTICLE_SPRITE[Globals.PARTICLE_SHIELD_FORTIFYBUFF] = new BufferedImage[8];
        for (int i = 0; i < PARTICLE_SPRITE[Globals.PARTICLE_SHIELD_FORTIFYBUFF].length; i++) {
            PARTICLE_SPRITE[Globals.PARTICLE_SHIELD_FORTIFYBUFF][i] = ImageIO
                    .read(Globals.class.getResourceAsStream("sprites/particle/fortifybuff/" + i + ".png"));
        }
        PARTICLE_SPRITE[Globals.PARTICLE_SHIELD_CHARGE] = new BufferedImage[1];
        for (int i = 0; i < PARTICLE_SPRITE[Globals.PARTICLE_SHIELD_CHARGE].length; i++) {
            PARTICLE_SPRITE[Globals.PARTICLE_SHIELD_CHARGE][i] = ImageIO
                    .read(Globals.class.getResourceAsStream("sprites/particle/charge/" + i + ".png"));
        }
        PARTICLE_SPRITE[Globals.PARTICLE_SHIELD_CHARGEPARTICLE] = new BufferedImage[8];
        for (int i = 0; i < PARTICLE_SPRITE[Globals.PARTICLE_SHIELD_CHARGEPARTICLE].length; i++) {
            PARTICLE_SPRITE[Globals.PARTICLE_SHIELD_CHARGEPARTICLE][i] = ImageIO
                    .read(Globals.class.getResourceAsStream("sprites/particle/charge2/" + i + ".png"));
        }
        PARTICLE_SPRITE[Globals.PARTICLE_SHIELD_REFLECTCAST] = new BufferedImage[16];
        for (int i = 0; i < PARTICLE_SPRITE[Globals.PARTICLE_SHIELD_REFLECTCAST].length; i++) {
            PARTICLE_SPRITE[Globals.PARTICLE_SHIELD_REFLECTCAST][i] = ImageIO
                    .read(Globals.class.getResourceAsStream("sprites/particle/reflectcast/" + i + ".png"));
        }
        PARTICLE_SPRITE[Globals.PARTICLE_SHIELD_REFLECTBUFF] = new BufferedImage[16];
        for (int i = 0; i < PARTICLE_SPRITE[Globals.PARTICLE_SHIELD_REFLECTBUFF].length; i++) {
            PARTICLE_SPRITE[Globals.PARTICLE_SHIELD_REFLECTBUFF][i] = ImageIO
                    .read(Globals.class.getResourceAsStream("sprites/particle/reflectbuff/" + i + ".png"));
        }
        PARTICLE_SPRITE[Globals.PARTICLE_SHIELD_REFLECTHIT] = new BufferedImage[16];
        for (int i = 0; i < PARTICLE_SPRITE[Globals.PARTICLE_SHIELD_REFLECTHIT].length; i++) {
            PARTICLE_SPRITE[Globals.PARTICLE_SHIELD_REFLECTHIT][i] = ImageIO
                    .read(Globals.class.getResourceAsStream("sprites/particle/reflecthit/" + i + ".png"));
        }
        PARTICLE_SPRITE[Globals.PARTICLE_SHIELD_IRON] = new BufferedImage[12];
        for (int i = 0; i < PARTICLE_SPRITE[Globals.PARTICLE_SHIELD_IRON].length; i++) {
            PARTICLE_SPRITE[Globals.PARTICLE_SHIELD_IRON][i] = ImageIO
                    .read(Globals.class.getResourceAsStream("sprites/particle/ironfort/" + i + ".png"));
        }
        PARTICLE_SPRITE[Globals.PARTICLE_SHIELD_IRONALLY] = new BufferedImage[12];
        for (int i = 0; i < PARTICLE_SPRITE[Globals.PARTICLE_SHIELD_IRONALLY].length; i++) {
            PARTICLE_SPRITE[Globals.PARTICLE_SHIELD_IRONALLY][i] = ImageIO
                    .read(Globals.class.getResourceAsStream("sprites/particle/ironfortally/" + i + ".png"));
        }
        PARTICLE_SPRITE[Globals.PARTICLE_SHIELD_TOSS] = new BufferedImage[16];
        for (int i = 0; i < PARTICLE_SPRITE[Globals.PARTICLE_SHIELD_TOSS].length; i++) {
            PARTICLE_SPRITE[Globals.PARTICLE_SHIELD_TOSS][i] = ImageIO
                    .read(Globals.class.getResourceAsStream("sprites/particle/toss/" + i + ".png"));
        }
        PARTICLE_SPRITE[Globals.PARTICLE_SWORD_SLASHBUFF] = new BufferedImage[6];
        for (int i = 0; i < PARTICLE_SPRITE[Globals.PARTICLE_SWORD_SLASHBUFF].length; i++) {
            PARTICLE_SPRITE[Globals.PARTICLE_SWORD_SLASHBUFF][i] = ImageIO
                    .read(Globals.class.getResourceAsStream("sprites/particle/slashbuff/" + i + ".png"));
        }
        PARTICLE_SPRITE[Globals.PARTICLE_SHIELD_DASHBUFF] = new BufferedImage[1];
        for (int i = 0; i < PARTICLE_SPRITE[Globals.PARTICLE_SHIELD_DASHBUFF].length; i++) {
            PARTICLE_SPRITE[Globals.PARTICLE_SHIELD_DASHBUFF][i] = ImageIO
                    .read(Globals.class.getResourceAsStream("sprites/particle/dashbuff/" + i + ".png"));
        }
        PARTICLE_SPRITE[Globals.PARTICLE_BOW_VOLLEYBUFF] = new BufferedImage[6];
        for (int i = 0; i < PARTICLE_SPRITE[Globals.PARTICLE_BOW_VOLLEYBUFF].length; i++) {
            PARTICLE_SPRITE[Globals.PARTICLE_BOW_VOLLEYBUFF][i] = ImageIO
                    .read(Globals.class.getResourceAsStream("sprites/particle/volleybuff/" + i + ".png"));
        }
        PARTICLE_SPRITE[Globals.PARTICLE_PASSIVE_RESIST] = new BufferedImage[12];
        for (int i = 0; i < PARTICLE_SPRITE[Globals.PARTICLE_PASSIVE_RESIST].length; i++) {
            PARTICLE_SPRITE[Globals.PARTICLE_PASSIVE_RESIST][i] = ImageIO
                    .read(Globals.class.getResourceAsStream("sprites/particle/resist/" + i + ".png"));
        }
        PARTICLE_SPRITE[Globals.PARTICLE_PASSIVE_BARRIER] = new BufferedImage[12];
        for (int i = 0; i < PARTICLE_SPRITE[Globals.PARTICLE_PASSIVE_BARRIER].length; i++) {
            PARTICLE_SPRITE[Globals.PARTICLE_PASSIVE_BARRIER][i] = ImageIO
                    .read(Globals.class.getResourceAsStream("sprites/particle/barrier/" + i + ".png"));
        }
        PARTICLE_SPRITE[Globals.PARTICLE_PASSIVE_SHADOWATTACK] = new BufferedImage[16];
        for (int i = 0; i < PARTICLE_SPRITE[Globals.PARTICLE_PASSIVE_SHADOWATTACK].length; i++) {
            PARTICLE_SPRITE[Globals.PARTICLE_PASSIVE_SHADOWATTACK][i] = ImageIO
                    .read(Globals.class.getResourceAsStream("sprites/particle/shadowattack/" + i + ".png"));
        }
        PARTICLE_SPRITE[Globals.PARTICLE_BOW_RAPID2] = new BufferedImage[3];
        for (int i = 0; i < PARTICLE_SPRITE[Globals.PARTICLE_BOW_RAPID2].length; i++) {
            PARTICLE_SPRITE[Globals.PARTICLE_BOW_RAPID2][i] = ImageIO
                    .read(Globals.class.getResourceAsStream("sprites/particle/rapid2/" + i + ".png"));
        }
    }

    public void update() {
        this.duration -= Globals.LOGIC_UPDATE / 1000000;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public int getKey() {
        return this.key;
    }

    @Override
    public void run() {
        update();
    }

    public boolean isExpired() {
        return this.duration <= 0;
    }

    public Particle(final int k, final int x, final int y) {
        this.key = k;
        this.x = x;
        this.y = y;
        this.duration = 200;
        setDaemon(true);
    }

    public Particle(final int k, final int x, final int y, final byte f) {
        this.key = k;
        this.x = x;
        this.y = y;
        this.facing = f;
        this.duration = 200;
        setDaemon(true);
    }

    @SuppressWarnings("unused")
    public void draw(final Graphics2D g) {
    }

    public void setExpire() {
        this.duration = 0;
    }

    public static BufferedImage[][] getParticleSprites() {
        return PARTICLE_SPRITE;
    }
}
