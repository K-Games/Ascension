package blockfighter.server.entities.buff;

import blockfighter.server.Globals;
import blockfighter.server.LogicModule;
import blockfighter.server.entities.GameEntity;
import blockfighter.server.entities.mob.Mob;
import blockfighter.server.entities.player.Player;

public abstract class Buff implements GameEntity {

    private Player playerOwner, playerTarget;
    private Mob mobOwner, mobTarget;
    private boolean isDebuff = false;
    private final Byte particleID = null;
    protected LogicModule room;

    protected int duration;
    private long buffStartTime;

    public Buff(final LogicModule l, final int d) {
        this.room = l;
        this.duration = d;
        this.buffStartTime = l.getTime();
    }

    public Buff(final LogicModule l, final int d, Player o) {
        this(l, d);
        this.playerOwner = o;
    }

    public Buff(final LogicModule l, final int d, Player o, Player t) {
        this(l, d);
        this.playerOwner = o;
        this.playerTarget = t;
    }

    public Buff(final LogicModule l, final int d, Player o, Mob t) {
        this(l, d);
        this.playerOwner = o;
        this.mobTarget = t;
    }

    public Buff(final LogicModule l, final int d, Mob o, Player t) {
        this(l, d);
        this.mobOwner = o;
        this.playerTarget = t;
    }

    public void reduceDuration(final int amount) {
        this.duration -= amount;
        if (this.duration < 500) {
            this.duration = 500;
        }
    }

    public void setOwner(final Player owner) {
        this.playerOwner = owner;
    }

    public void setOwner(final Mob owner) {
        this.mobOwner = owner;
    }

    public void setMobOwner(final Mob owner) {
        this.mobOwner = owner;
    }

    public void setTarget(final Player t) {
        this.playerTarget = t;
    }

    public void setTarget(final Mob t) {
        this.mobTarget = t;
    }

    public void setMobTarget(final Mob t) {
        this.mobTarget = t;
    }

    public Player getOwner() {
        return this.playerOwner;
    }

    public Player getTarget() {
        return this.playerTarget;
    }

    public Mob getMobOwner() {
        return this.mobOwner;
    }

    public Mob getMobTarget() {
        return this.mobTarget;
    }

    @Override
    public void update() {
    }

    public int getDuration() {
        return this.duration;
    }

    public boolean isExpired() {
        return Globals.nsToMs(this.room.getTime() - this.buffStartTime) >= this.duration;
    }

    public boolean isDebuff() {
        return this.isDebuff;
    }

    public void setDebuff(final boolean set) {
        this.isDebuff = set;
    }

    public Byte getParticleID() {
        return this.particleID;
    }
}
