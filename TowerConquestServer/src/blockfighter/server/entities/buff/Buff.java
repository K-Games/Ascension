package blockfighter.server.entities.buff;

import blockfighter.server.Globals;
import blockfighter.server.LogicModule;
import blockfighter.server.entities.GameEntity;
import blockfighter.server.entities.boss.Boss;
import blockfighter.server.entities.player.Player;

/**
 * Abstract class for all buffs/debuffs
 *
 * @author Ken Kwan
 */
public abstract class Buff implements GameEntity {

    /**
     * Owning player of buff
     */
    private Player playerOwner, playerTarget;
    private Boss bossOwner, bossTarget;
    private boolean isDebuff = false;
    private final Byte particleID = null;
    protected LogicModule logic;
    /**
     * Duration of buff/debuff in milliseconds
     */
    protected int duration;
    private long buffStartTime;

    /**
     * Constructor for buffs
     *
     * @param l logic(room) buffs belong to.
     * @param d duration of buff
     */
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

    public Buff(final LogicModule l, final int d, Player o, Boss t) {
        this(l, d);
        this.playerOwner = o;
        this.bossTarget = t;
    }

    public Buff(final LogicModule l, final int d, Boss o, Player t) {
        this(l, d);
        this.bossOwner = o;
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

    public void setOwner(final Boss owner) {
        this.bossOwner = owner;
    }

    public void setBossOwner(final Boss owner) {
        this.bossOwner = owner;
    }

    public void setTarget(final Player t) {
        this.playerTarget = t;
    }

    public void setTarget(final Boss t) {
        this.bossTarget = t;
    }

    public void setBossTarget(final Boss t) {
        this.bossTarget = t;
    }

    public Player getOwner() {
        return this.playerOwner;
    }

    public Player getTarget() {
        return this.playerTarget;
    }

    public Boss getBossOwner() {
        return this.bossOwner;
    }

    public Boss getBossTarget() {
        return this.bossTarget;
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
