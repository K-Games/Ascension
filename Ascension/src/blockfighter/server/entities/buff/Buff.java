package blockfighter.server.entities.buff;

import blockfighter.server.LogicModule;
import blockfighter.server.entities.GameEntity;
import blockfighter.server.entities.player.Player;
import blockfighter.shared.Globals;

public abstract class Buff implements GameEntity {

    private Player playerOwner, playerTarget;
    private boolean isDebuff = false;
    private final Byte particleID = null;
    protected LogicModule logic;

    protected int duration;
    protected long buffStartTime;

    public Buff(final LogicModule l, final int d) {
        this.logic = l;
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

    public void reduceDuration(final int amount) {
        this.duration -= amount;
        if (this.duration < 500) {
            this.duration = 500;
        }
    }

    public void setOwner(final Player owner) {
        this.playerOwner = owner;
    }

    public void setTarget(final Player t) {
        this.playerTarget = t;
    }

    public Player getOwner() {
        return this.playerOwner;
    }

    public Player getTarget() {
        return this.playerTarget;
    }

    @Override
    public void update() {
    }

    public int getDuration() {
        return this.duration;
    }

    public boolean isExpired() {
        return Globals.nsToMs(this.logic.getTime() - this.buffStartTime) >= this.duration;
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
