package blockfighter.server.entities.proj;

import blockfighter.server.LogicModule;
import blockfighter.server.RoomData;
import blockfighter.server.entities.GameEntity;
import blockfighter.server.entities.damage.DamageBuilder;
import blockfighter.server.entities.player.Player;
import blockfighter.server.net.PacketSender;
import blockfighter.shared.Globals;
import java.awt.geom.Rectangle2D;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.concurrent.Callable;

public abstract class Projectile implements GameEntity, Callable<Projectile> {

    protected final int key;

    protected final LogicModule logic;
    protected final RoomData room;

    protected double x, y;

    private Player owner;

    protected HashMap<Byte, Player> pHit = new HashMap<>();
    protected final ArrayDeque<Player> playerQueue = new ArrayDeque<>();

    protected int duration;

    protected Rectangle2D.Double[] hitbox;

    protected boolean queuedEffect = false, screenshake = false;

    protected long projStartTime = 0;

    public Projectile(final LogicModule l) {
        this.logic = l;
        this.room = l.getRoomData();
        this.key = this.room.getNextProjKey();
        projStartTime = this.logic.getTime();
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

    @Override
    public void update() {
        if (this.hitbox[0] == null) {
            return;
        }
        if (this.room.getMap().isPvP()) {
            this.room.getPlayersNearProj(this).entrySet().forEach((pEntry) -> {
                final Player p = pEntry.getValue();
                if (p != getOwner() && !this.pHit.containsKey(p.getKey()) && !p.isDead() && !p.isInvulnerable() && p.intersectHitbox(this.hitbox[0])) {
                    this.playerQueue.add(p);
                    this.pHit.put(p.getKey(), p);
                    queueEffect(this);
                }
            });
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

    @Override
    public Projectile call() {
        try {
            update();
        } catch (final Exception ex) {
            Globals.logError(ex.toString(), ex);
        }
        return this;
    }

    public boolean isExpired() {
        return Globals.nsToMs(this.logic.getTime() - this.projStartTime) >= this.duration;
    }

    public boolean isQueued() {
        return this.queuedEffect;
    }

    public int getKey() {
        return this.key;
    }

    public void applyEffect() {
        while (!this.playerQueue.isEmpty()) {
            final Player p = this.playerQueue.poll();
            if (p != null && !p.isDead()) {
                applyDamage(p);
            }
        }

        this.queuedEffect = false;
    }

    public Rectangle2D.Double[] getHitbox() {
        return this.hitbox;
    }

    public void queueEffect(final Projectile p) {
        if (!isQueued()) {
            if (this.screenshake) {
                PacketSender.sendScreenShake(this.getOwner());
            }
            this.logic.queueProjEffect(p);
            this.queuedEffect = true;
        }
    }

    public void applyDamage(final Player target) {
        final boolean isCrit = this.owner.rollCrit();
        final int damage = calculateDamage(isCrit);
        target.queueDamage(new DamageBuilder()
                .setDamage(damage)
                .setOwner(this.owner)
                .setTarget(target)
                .setIsCrit(isCrit)
                .build());
    }

    public abstract int calculateDamage(final boolean isCrit);
}
