package blockfighter.server.entities.proj;

import blockfighter.server.Globals;
import blockfighter.server.LogicModule;
import blockfighter.server.entities.GameEntity;
import blockfighter.server.entities.boss.Boss;
import blockfighter.server.entities.player.Player;
import blockfighter.server.net.PacketSender;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;

/**
 * Abstract class for projectiles/attacks
 *
 * @author Ken Kwan
 */
public abstract class Projectile extends Thread implements GameEntity {

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
     * Owning Player of Projectile
     */
    private Player owner;

    /**
     * Owning Boss of Projectile
     */
    private Boss bossOwner;
    /**
     * Array of players hit by this projectile
     */
    protected ArrayList<Player> pHit = new ArrayList<>();
    
    /**
     * Queue of players to be hit by projectile
     */
    protected final LinkedList<Player> playerQueue = new LinkedList<>();
    protected ArrayList<Boss> bHit = new ArrayList<>();
    protected final LinkedList<Boss> bossQueue = new LinkedList<>();
    /**
     * The duration of this projectile in nanoseconds.
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
    public Projectile(LogicModule l, int k) {
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
    public Projectile(LogicModule l, int k, Player o, double x, double y, long duration) {
        this(l, k);
        owner = o;
        this.x = x;
        this.y = y;
        hitbox = new Rectangle2D.Double[1];
        hitbox[0] = new Rectangle2D.Double(0, 0, 0, 0);
        this.duration = duration;
    }

    public Projectile(LogicModule l, int k, Boss o, double x, double y, long duration) {
        this(l, k);
        bossOwner = o;
        this.x = x;
        this.y = y;
        hitbox = new Rectangle2D.Double[1];
        this.duration = duration;
    }

    @Override
    public void update() {
        duration -= Globals.LOGIC_UPDATE / 1000000;
        if (hitbox[0] == null) {
            return;
        }
        if (logic.getMap().isPvP()) {
            for (Map.Entry<Byte, Player> pEntry : logic.getPlayers().entrySet()) {
                Player p = pEntry.getValue();
                if (p != getOwner() && !pHit.contains(p) && !p.isInvulnerable() && p.intersectHitbox(hitbox[0])) {
                    playerQueue.add(p);
                    pHit.add(p);
                    queueEffect(this);
                }
            }
        }

        for (Map.Entry<Byte, Boss> bEntry : logic.getBosses().entrySet()) {
            Boss b = bEntry.getValue();
            if (!bHit.contains(b) && b.intersectHitbox(hitbox[0])) {
                bossQueue.add(b);
                bHit.add(b);
                queueEffect(this);
            }
        }
    }

    public double getX() {
        return x;
    }

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

    public void setOwner(Player owner) {
        this.owner = owner;
    }

    public Player getOwner() {
        return owner;
    }

    public void setBossOwner(Boss owner) {
        bossOwner = owner;
    }

    public Boss getBossOwner() {
        return bossOwner;
    }

    @Override
    public void run() {
        try {
            update();
        } catch (Exception ex) {
            Globals.log(ex.getLocalizedMessage(), ex, true);
        }
    }

    public boolean isExpired() {
        return duration <= 0;
    }

    public boolean isQueued() {
        return queuedEffect;
    }

    public int getKey() {
        return key;
    }

    public void processQueue() {
    }

    public Rectangle2D.Double[] getHitbox() {
        return hitbox;
    }

    public void queueEffect(Projectile p) {
        if (!isQueued()) {
            logic.queueProjEffect(p);
            queuedEffect = true;
        }
    }
}
