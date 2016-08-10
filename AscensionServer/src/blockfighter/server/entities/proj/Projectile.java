package blockfighter.server.entities.proj;

import blockfighter.server.Globals;
import blockfighter.server.LogicModule;
import blockfighter.server.entities.GameEntity;
import blockfighter.server.entities.mob.Mob;
import blockfighter.server.entities.player.Player;
import blockfighter.server.net.PacketSender;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public abstract class Projectile extends Thread implements GameEntity {

    protected final int key;

    protected final LogicModule room;

    protected double x, y;

    private Player owner;

    private Mob mobOwner;

    protected HashMap<Byte, Player> pHit = new HashMap<>();
    protected final LinkedList<Player> playerQueue = new LinkedList<>();
    protected HashMap<Integer, Mob> bHit = new HashMap<>();
    protected final LinkedList<Mob> mobQueue = new LinkedList<>();

    protected int duration;

    protected Rectangle2D.Double[] hitbox;

    protected boolean queuedEffect = false, screenshake = false;

    protected long projStartTime = 0;

    public Projectile(final LogicModule l) {
        this.room = l;
        this.key = this.room.getNextProjKey();
        projStartTime = this.room.getTime();
    }

    public Projectile(final LogicModule l, final Player o, final double x, final double y, final int duration) {
        this(l);
        this.owner = o;
        this.x = x;
        this.y = y;
        this.hitbox = new Rectangle2D.Double[1];
        this.hitbox[0] = new Rectangle2D.Double(0, 0, 0, 0);
        this.duration = duration;
    }

    public Projectile(final LogicModule l, final Mob o, final double x, final double y, final int duration) {
        this(l);
        this.mobOwner = o;
        this.x = x;
        this.y = y;
        this.hitbox = new Rectangle2D.Double[1];
        this.duration = duration;
    }

    @Override
    public void update() {
        if (this.hitbox[0] == null) {
            return;
        }
        if (this.room.getMap().isPvP()) {
            for (final Map.Entry<Byte, Player> pEntry : this.room.getPlayersNearProj(this).entrySet()) {
                final Player p = pEntry.getValue();
                if (p != getOwner() && !this.pHit.containsKey(p.getKey()) && !p.isDead() && !p.isInvulnerable() && p.intersectHitbox(this.hitbox[0])) {
                    this.playerQueue.add(p);
                    this.pHit.put(p.getKey(), p);
                    queueEffect(this);
                }
            }
        }

        for (final Map.Entry<Integer, Mob> bEntry : this.room.getMobs().entrySet()) {
            final Mob b = bEntry.getValue();
            if (!this.bHit.containsKey(b.getKey()) && b.intersectHitbox(this.hitbox[0])) {
                this.mobQueue.add(b);
                this.bHit.put(b.getKey(), b);
                queueEffect(this);
            }
        }
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public void setOwner(final Player owner) {
        this.owner = owner;
    }

    public Player getOwner() {
        return this.owner;
    }

    public void setMobOwner(final Mob owner) {
        this.mobOwner = owner;
    }

    public Mob getMobOwner() {
        return this.mobOwner;
    }

    @Override
    public void run() {
        try {
            update();
        } catch (final Exception ex) {
            Globals.logError(ex.getStackTrace()[0].toString(), ex, true);
        }
    }

    public boolean isExpired() {
        return Globals.nsToMs(this.room.getTime() - this.projStartTime) >= this.duration;
    }

    public boolean isQueued() {
        return this.queuedEffect;
    }

    public int getKey() {
        return this.key;
    }

    public void applyEffect() {
    }

    public Rectangle2D.Double[] getHitbox() {
        return this.hitbox;
    }

    public void queueEffect(final Projectile p) {
        if (!isQueued()) {
            if (this.screenshake) {
                PacketSender.sendScreenShake(this.getOwner());
            }
            this.room.queueProjEffect(p);
            this.queuedEffect = true;
        }
    }
}
