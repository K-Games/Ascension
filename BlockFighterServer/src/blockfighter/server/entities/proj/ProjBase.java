package blockfighter.server.entities.proj;

import blockfighter.server.Globals;
import blockfighter.server.LogicModule;
import blockfighter.server.entities.Player;
import blockfighter.server.entities.Projectile;
import blockfighter.server.net.Broadcaster;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

/**
 * Abstract class for projectiles/attacks
 *
 * @author ckwa290
 */
public abstract class ProjBase extends Thread implements Projectile {

    /**
     * Hash map key paired with this
     */
    protected final int key;
    /**
     * Reference to Logic Module.
     */
    protected final LogicModule logic;

    /**
     * Projectile x position.
     */
    protected double x,
            /**
             * Projectile y position.
             */
            y,
            /**
             * Projectile knockback x distance per tick
             */
            xSpeed,
            /**
             * Projectile knockback y distance per tick
             */
            ySpeed;

    /**
     * Projectile's owning player
     */
    protected Player owner;

    /**
     * Array of players hit by this projectile
     */
    protected ArrayList<Player> pHit = new ArrayList<>();

    /**
     * The duration of this projectile in ns.
     */
    protected long duration;

    /**
     * Hitbox(es) of this projectile
     */
    protected Rectangle2D.Double[] hitbox;

    /**
     * Reference to Server Broadcaster
     */
    protected Broadcaster broadcaster;

    protected boolean queuedEffect = false;

    /**
     * Constructor called by subclasses to reference broadcaster and logic.
     *
     * @param b Reference to server broadcaster
     * @param l Reference to Logic module
     * @param k Hash map key
     */
    public ProjBase(Broadcaster b, LogicModule l, int k) {
        broadcaster = b;
        logic = l;
        key = k;
    }

    /**
     * Constructor for a empty projectile.
     *
     * @param b Reference to server broadcaster
     * @param l Reference to Logic module
     * @param k Hash map key
     * @param o Owning player
     * @param x Spawning x
     * @param y Spawning y
     * @param duration
     */
    public ProjBase(Broadcaster b, LogicModule l, int k, Player o, double x, double y, long duration) {
        this(b, l, k);
        owner = o;
        xSpeed = 0;
        ySpeed = 0;
        this.x = x;
        this.y = y;
        hitbox = new Rectangle2D.Double[1];
        hitbox[0] = new Rectangle2D.Double(0, 0, 0, 0);
        this.duration = duration;
    }

    @Override
    public void update() {
        duration -= Globals.LOGIC_UPDATE / 1000000;
    }

    @Override
    public double getX() {
        return x;
    }

    @Override
    public double getY() {
        return y;
    }

    @Override
    public void setOwner(Player owner) {
        this.owner = owner;
    }

    @Override
    public Player getOwner() {
        return owner;
    }

    @Override
    public void run() {
        update();
    }

    @Override
    public boolean isExpired() {
        return duration <= 0;
    }

    @Override
    public boolean isQueued() {
        return queuedEffect;
    }

    @Override
    public int getKey() {
        return key;
    }
}
