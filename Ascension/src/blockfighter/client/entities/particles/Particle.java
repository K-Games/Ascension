package blockfighter.client.entities.particles;

import blockfighter.client.Globals;
import blockfighter.client.LogicModule;
import blockfighter.client.Main;
import blockfighter.client.entities.player.Player;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public abstract class Particle extends Thread {

    protected int x, y;
    protected double size = 10.0;
    protected int frameDuration;
    protected long lastFrameTime = 0,
            particleStartTime = 0;
    protected int frame = 0;
    protected byte facing = Globals.RIGHT;

    protected final Player owner;

    protected static BufferedImage[][] PARTICLE_SPRITE;
    private final static String[] PARTICLE_SPRITE_FOLDER = new String[Globals.NUM_PARTICLE_EFFECTS];
    private final static int[] PARTICLE_FRAMES = new int[Globals.NUM_PARTICLE_EFFECTS];

    protected final int key;
    private static boolean LOADED = false;

    protected static LogicModule logic;

    protected int duration;

    public static void init() {
        logic = Main.getLogicModule();
        PARTICLE_SPRITE_FOLDER[Globals.PARTICLE_SWORD_SLASH1] = "slash1";
        PARTICLE_SPRITE_FOLDER[Globals.PARTICLE_SWORD_SLASH2] = "slash2";
        PARTICLE_SPRITE_FOLDER[Globals.PARTICLE_SWORD_SLASH3] = "slash3";
        PARTICLE_SPRITE_FOLDER[Globals.PARTICLE_SWORD_GASH1] = "gash1";
        PARTICLE_SPRITE_FOLDER[Globals.PARTICLE_SWORD_GASH2] = "gash2";
        PARTICLE_SPRITE_FOLDER[Globals.PARTICLE_SWORD_VORPAL] = "vorpal";
        PARTICLE_SPRITE_FOLDER[Globals.PARTICLE_SWORD_PHANTOM] = "phantom";
        PARTICLE_SPRITE_FOLDER[Globals.PARTICLE_SWORD_PHANTOM2] = "phantomslash";
        PARTICLE_SPRITE_FOLDER[Globals.PARTICLE_SWORD_CINDER] = "cinder";
        PARTICLE_SPRITE_FOLDER[Globals.PARTICLE_BURN] = "burn";
        PARTICLE_SPRITE_FOLDER[Globals.PARTICLE_SWORD_TAUNT] = "taunt";
        PARTICLE_SPRITE_FOLDER[Globals.PARTICLE_SWORD_TAUNTAURA1] = "tauntaura";
        PARTICLE_SPRITE_FOLDER[Globals.PARTICLE_SWORD_TAUNTAURA2] = "tauntaura2";
        PARTICLE_SPRITE_FOLDER[Globals.PARTICLE_BOW_ARC] = "arc";
        PARTICLE_SPRITE_FOLDER[Globals.PARTICLE_BOW_RAPID] = "rapid";
        PARTICLE_SPRITE_FOLDER[Globals.PARTICLE_BOW_POWER] = "power";
        PARTICLE_SPRITE_FOLDER[Globals.PARTICLE_BOW_POWERPARTICLE] = "power2";
        PARTICLE_SPRITE_FOLDER[Globals.PARTICLE_BOW_POWERCHARGE] = "powercharge";
        PARTICLE_SPRITE_FOLDER[Globals.PARTICLE_BOW_VOLLEYBOW] = "volleybow";
        PARTICLE_SPRITE_FOLDER[Globals.PARTICLE_BOW_VOLLEYARROW] = "volleyarrow";
        PARTICLE_SPRITE_FOLDER[Globals.PARTICLE_BOW_STORM] = "stormarrow";
        PARTICLE_SPRITE_FOLDER[Globals.PARTICLE_BOW_FROSTARROW] = "frostarrow";
        PARTICLE_SPRITE_FOLDER[Globals.PARTICLE_SHIELD_DASH] = "dash";
        PARTICLE_SPRITE_FOLDER[Globals.PARTICLE_SHIELD_FORTIFY] = "fortify";
        PARTICLE_SPRITE_FOLDER[Globals.PARTICLE_SHIELD_FORTIFYBUFF] = "fortifybuff";
        PARTICLE_SPRITE_FOLDER[Globals.PARTICLE_SHIELD_CHARGE] = "charge";
        PARTICLE_SPRITE_FOLDER[Globals.PARTICLE_SHIELD_CHARGEPARTICLE] = "charge2";
        PARTICLE_SPRITE_FOLDER[Globals.PARTICLE_SHIELD_REFLECTCAST] = "reflectcast";
        PARTICLE_SPRITE_FOLDER[Globals.PARTICLE_SHIELD_REFLECTBUFF] = "reflectbuff";
        PARTICLE_SPRITE_FOLDER[Globals.PARTICLE_SHIELD_REFLECTHIT] = "reflecthit";
        PARTICLE_SPRITE_FOLDER[Globals.PARTICLE_SWORD_SLASHBUFF] = "slashbuff";
        PARTICLE_SPRITE_FOLDER[Globals.PARTICLE_SHIELD_DASHBUFF] = "dashbuff";
        PARTICLE_SPRITE_FOLDER[Globals.PARTICLE_BOW_VOLLEYBUFF] = "volleybuff";
        PARTICLE_SPRITE_FOLDER[Globals.PARTICLE_PASSIVE_RESIST] = "resist";
        PARTICLE_SPRITE_FOLDER[Globals.PARTICLE_PASSIVE_BARRIER] = "barrier";
        PARTICLE_SPRITE_FOLDER[Globals.PARTICLE_PASSIVE_SHADOWATTACK] = "shadowattack";
        PARTICLE_SPRITE_FOLDER[Globals.PARTICLE_BOW_RAPID2] = "rapid2";
        PARTICLE_SPRITE_FOLDER[Globals.PARTICLE_SHIELD_MAGNETIZESTART] = "magnetizestart";
        PARTICLE_SPRITE_FOLDER[Globals.PARTICLE_SHIELD_MAGNETIZEBURST] = "magnetizeburst";
        PARTICLE_SPRITE_FOLDER[Globals.PARTICLE_SHIELD_ROAR] = "roar";
        PARTICLE_SPRITE_FOLDER[Globals.PARTICLE_SHIELD_ROARHIT] = "roarhit";

        PARTICLE_FRAMES[Globals.PARTICLE_SWORD_SLASH1] = 3;
        PARTICLE_FRAMES[Globals.PARTICLE_SWORD_SLASH2] = 3;
        PARTICLE_FRAMES[Globals.PARTICLE_SWORD_SLASH3] = 5;
        PARTICLE_FRAMES[Globals.PARTICLE_SWORD_GASH1] = 5;
        PARTICLE_FRAMES[Globals.PARTICLE_SWORD_GASH2] = 5;
        PARTICLE_FRAMES[Globals.PARTICLE_SWORD_VORPAL] = 4;
        PARTICLE_FRAMES[Globals.PARTICLE_SWORD_PHANTOM] = 6;
        PARTICLE_FRAMES[Globals.PARTICLE_SWORD_PHANTOM2] = 4;
        PARTICLE_FRAMES[Globals.PARTICLE_SWORD_CINDER] = 6;
        PARTICLE_FRAMES[Globals.PARTICLE_BURN] = 20;
        PARTICLE_FRAMES[Globals.PARTICLE_SWORD_TAUNT] = 5;
        PARTICLE_FRAMES[Globals.PARTICLE_SWORD_TAUNTAURA1] = 5;
        PARTICLE_FRAMES[Globals.PARTICLE_SWORD_TAUNTAURA2] = 10;
        PARTICLE_FRAMES[Globals.PARTICLE_BOW_ARC] = 8;
        PARTICLE_FRAMES[Globals.PARTICLE_BOW_RAPID] = 6;
        PARTICLE_FRAMES[Globals.PARTICLE_BOW_POWER] = 24;
        PARTICLE_FRAMES[Globals.PARTICLE_BOW_POWERPARTICLE] = 10;
        PARTICLE_FRAMES[Globals.PARTICLE_BOW_POWERCHARGE] = 1;
        PARTICLE_FRAMES[Globals.PARTICLE_BOW_VOLLEYBOW] = 10;
        PARTICLE_FRAMES[Globals.PARTICLE_BOW_VOLLEYARROW] = 0;
        PARTICLE_FRAMES[Globals.PARTICLE_BOW_STORM] = 20;
        PARTICLE_FRAMES[Globals.PARTICLE_BOW_FROSTARROW] = 12;
        PARTICLE_FRAMES[Globals.PARTICLE_SHIELD_DASH] = 8;
        PARTICLE_FRAMES[Globals.PARTICLE_SHIELD_FORTIFY] = 20;
        PARTICLE_FRAMES[Globals.PARTICLE_SHIELD_FORTIFYBUFF] = 8;
        PARTICLE_FRAMES[Globals.PARTICLE_SHIELD_CHARGE] = 1;
        PARTICLE_FRAMES[Globals.PARTICLE_SHIELD_CHARGEPARTICLE] = 5;
        PARTICLE_FRAMES[Globals.PARTICLE_SHIELD_REFLECTCAST] = 16;
        PARTICLE_FRAMES[Globals.PARTICLE_SHIELD_REFLECTBUFF] = 16;
        PARTICLE_FRAMES[Globals.PARTICLE_SHIELD_REFLECTHIT] = 10;
        PARTICLE_FRAMES[Globals.PARTICLE_SWORD_SLASHBUFF] = 6;
        PARTICLE_FRAMES[Globals.PARTICLE_SHIELD_DASHBUFF] = 1;
        PARTICLE_FRAMES[Globals.PARTICLE_BOW_VOLLEYBUFF] = 6;
        PARTICLE_FRAMES[Globals.PARTICLE_PASSIVE_RESIST] = 12;
        PARTICLE_FRAMES[Globals.PARTICLE_PASSIVE_BARRIER] = 7;
        PARTICLE_FRAMES[Globals.PARTICLE_PASSIVE_SHADOWATTACK] = 16;
        PARTICLE_FRAMES[Globals.PARTICLE_BOW_RAPID2] = 3;
        PARTICLE_FRAMES[Globals.PARTICLE_SHIELD_MAGNETIZESTART] = 13;
        PARTICLE_FRAMES[Globals.PARTICLE_SHIELD_MAGNETIZEBURST] = 11;
        PARTICLE_FRAMES[Globals.PARTICLE_SHIELD_ROAR] = 10;
        PARTICLE_FRAMES[Globals.PARTICLE_SHIELD_ROARHIT] = 7;
    }

    public static void unloadParticles() {
        System.out.println("Unloading Particles...");
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
        System.out.println("Loading Particles...");
        LOADED = true;
        PARTICLE_SPRITE = new BufferedImage[Globals.NUM_PARTICLE_EFFECTS][];
        for (int spriteID = 0; spriteID < PARTICLE_SPRITE.length; spriteID++) {
            if (PARTICLE_SPRITE_FOLDER[spriteID] != null && PARTICLE_FRAMES[spriteID] > 0) {
                PARTICLE_SPRITE[spriteID] = new BufferedImage[PARTICLE_FRAMES[spriteID]];
                for (int frame = 0; frame < PARTICLE_SPRITE[spriteID].length; frame++) {
                    PARTICLE_SPRITE[spriteID][frame] = Globals.loadTextureResource("sprites/particle/" + PARTICLE_SPRITE_FOLDER[spriteID] + "/" + frame + ".png");
                }
            }
        }
    }

    public void update() {
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
        return Globals.nsToMs(logic.getTime() - this.particleStartTime) >= this.duration;
    }

    public Particle(final int k, final int x, final int y, final Player owner) {
        if (logic != null) {
            this.particleStartTime = logic.getTime();
            this.lastFrameTime = logic.getTime();
        } else {
            this.particleStartTime = System.nanoTime();
            this.lastFrameTime = particleStartTime;
        }
        this.key = k;
        this.x = x;
        this.y = y;
        this.duration = 200;
        this.owner = owner;
        setDaemon(true);
    }

    public Particle(final int x, final int y) {
        this(logic.getScreen().getNextParticleKey(), x, y, null);
    }

    public Particle(final int x, final int y, final byte f) {
        this(x, y);
        this.facing = f;
    }

    public Particle(final byte f, final Player owner) {
        this(owner.getX(), owner.getY(), owner);
        this.facing = f;
    }

    public Particle(final int x, final int y, final Player owner) {
        this(logic.getScreen().getNextParticleKey(), x, y, owner);
    }

    public Particle(final Player owner) {
        this(owner.getX(), owner.getY(), owner);
    }

    public Particle(final Player owner, final byte f) {
        this(owner.getX(), owner.getY(), owner);
        this.facing = f;
    }

    public void draw(final Graphics2D g) {
    }

    public void setExpire() {
        this.duration = 0;
    }

    public static BufferedImage[][] getParticleSprites() {
        return PARTICLE_SPRITE;
    }
}
