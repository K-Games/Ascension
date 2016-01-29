package blockfighter.server.entities.buff;

import blockfighter.server.Globals;
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
    /**
     * Duration of buff/debuff in milliseconds
     */
    protected long duration;

    /**
     * Constructor for buffs
     *
     * @param d duration in milliseconds
     */
    public Buff(final long d) {
        this.duration = d;
    }

    public Buff(final long d, Player o) {
        this.duration = d;
        this.playerOwner = o;
    }

    public Buff(final long d, Player o, Player t) {
        this.duration = d;
        this.playerOwner = o;
        this.playerTarget = t;
    }

    public Buff(final long d, Player o, Boss t) {
        this.duration = d;
        this.playerOwner = o;
        this.bossTarget = t;
    }

    public Buff(final long d, Boss o, Player t) {
        this.duration = d;
        this.bossOwner = o;
        this.playerTarget = t;
    }

    public void reduceDuration(final long amount) {
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
        this.duration -= Globals.nsToMs(Globals.LOGIC_UPDATE);
    }

    public long getDuration() {
        return this.duration;
    }

    public boolean isExpired() {
        return this.duration <= 0;
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
