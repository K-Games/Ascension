package blockfighter.client.entities.particles;

import blockfighter.client.AscensionClient;
import blockfighter.client.LogicModule;
import blockfighter.client.entities.player.Player;
import blockfighter.shared.Globals;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class Particle implements Callable<Particle> {

    protected int x, y;
    protected double size = 10.0;
    protected int frameDuration;
    protected long lastFrameTime = 0,
            particleStartTime = 0;
    protected int frame = 0;
    protected byte facing = Globals.RIGHT;

    protected final Player owner;

    private static final ConcurrentLinkedQueue<Integer> PARTICLE_KEYS = new ConcurrentLinkedQueue<>();
    private static int numParticleKeys = 500;

    protected final int key;
    private static boolean LOADED = false;

    protected static LogicModule logic;

    protected int duration;

    public static void init() {
        logic = AscensionClient.getLogicModule();

        for (int key = 0; key < numParticleKeys; key++) {
            PARTICLE_KEYS.add(key);
        }

    }

    public static int getNextParticleKey() {
        Integer nextKey = PARTICLE_KEYS.poll();
        while (nextKey == null) {
            PARTICLE_KEYS.add(numParticleKeys);
            numParticleKeys++;
            nextKey = PARTICLE_KEYS.poll();
        }
        return nextKey;
    }

    public static void returnParticleKey(final int key) {
        PARTICLE_KEYS.add(key);
    }

    public static void unloadParticles() {
        Globals.log(Particle.class, "Unloading Particles...", Globals.LOG_TYPE_DATA);
        for (Globals.Particles particle : Globals.Particles.values()) {
            for (int j = 0; particle.getSprite() != null && j < particle.getSprite().length; j++) {
                particle.setSprite(null);
            }
        }
        LOADED = false;
        System.gc();
    }

    public static void loadParticles() throws Exception {
        if (LOADED) {
            return;
        }
        Globals.log(Particle.class, "Loading Particles...", Globals.LOG_TYPE_DATA);
        LOADED = true;
        for (Globals.Particles particle : Globals.Particles.values()) {
            if (particle.getSpriteFolder() != null && particle.getNumFrames() > 0) {
                BufferedImage[] loadSprites = new BufferedImage[particle.getNumFrames()];
                particle.setSprite(loadSprites);
                for (int frame = 0; frame < loadSprites.length; frame++) {
                    loadSprites[frame] = Globals.loadTextureResource("sprites/particle/" + particle.getSpriteFolder() + "/" + frame + ".png");
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
    public Particle call() {
        if (!isExpired()) {
            update();
        }
        return this;
    }

    public boolean isExpired() {
        return Globals.nsToMs(logic.getTime() - this.particleStartTime) >= this.duration;
    }

    public Particle() {
        this(0, 0);
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
        if (owner != null) {
            this.facing = owner.getFacing();
        }
    }

    public Particle(final int x, final int y) {
        this(getNextParticleKey(), x, y, null);
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
        this(getNextParticleKey(), x, y, owner);
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

}
