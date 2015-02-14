package blockfighter.server.entities.proj;

import blockfighter.server.Globals;
import blockfighter.server.LogicModule;
import blockfighter.server.entities.boss.Boss;
import blockfighter.server.entities.player.Player;
import blockfighter.server.net.PacketSender;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

/**
 * Abstract class for projectiles/attacks
 *
 * @author Ken Kwan
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
            y;

    /**
     * Owning player of Projectile
     */
    private Player owner;

    private Boss bossOwner;
    /**
     * Array of players hit by this projectile
     */
    protected ArrayList<Player> pHit = new ArrayList<>();

    /**
     * The duration of this projectile in ns.
     */
    protected long duration;

    /**
     * Hit boxes of this projectile
     */
    protected Rectangle2D.Double[] hitbox;

    /**
     * Reference to Server PacketSender
     */
    protected static PacketSender sender;

    /**
     * Checks if this projectile has already been queued to have effects to be applied
     */
    protected boolean queuedEffect = false;

    /**
     * Constructor called by subclasses to reference sender and logic.
     *
     * @param l Reference to Logic module
     * @param k Hash map key
     */
    public ProjBase(LogicModule l, int k) {
        logic = l;
        key = k;
    }

    /**
     * Constructor for a empty projectile.
     *
     * @param l Reference to Logic module
     * @param k Hash map key
     * @param o Owning player
     * @param x Spawning x
     * @param y Spawning y
     * @param duration
     */
    public ProjBase(LogicModule l, int k, Player o, double x, double y, long duration) {
        this(l, k);
        owner = o;
        this.x = x;
        this.y = y;
        hitbox = new Rectangle2D.Double[1];
        hitbox[0] = new Rectangle2D.Double(0, 0, 0, 0);
        this.duration = duration;
    }

    public ProjBase(LogicModule l, int k, Boss o, double x, double y, long duration) {
        this(l, k);
        bossOwner = o;
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

    /**
     * Set the static packet sender.
     *
     * @param ps Server PacketSender.
     */
    public static void setPacketSender(PacketSender ps) {
        sender = ps;
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
    public Boss getBossOwner() {
        return bossOwner;
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

    @Override
    public void processQueue() {
    }

    @Override
    public Rectangle2D.Double[] getHitbox() {
        return hitbox;
    }

    @Override
    public void queueEffect(ProjBase p) {
        if (!isQueued()) {
            logic.queueProjEffect(p);
            queuedEffect = true;
        }
    }
}
