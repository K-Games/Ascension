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
    private Byte particleID = null;
    /**
     * Duration of buff/debuff in milliseconds
     */
    protected long duration;

    /**
     * Constructor for buffs
     *
     * @param d duration in milliseconds
     */
    public Buff(long d) {
        duration = d;
    }

    public void reduceDuration(long amount) {
        duration -= amount;
        if (duration < 500) {
            duration = 500;
        }
    }

    public void setOwner(Player owner) {
        playerOwner = owner;
    }

    public void setOwner(Boss owner) {
        bossOwner = owner;
    }

    public void setBossOwner(Boss owner) {
        bossOwner = owner;
    }

    public void setTarget(Player t) {
        playerTarget = t;
    }

    public void setTarget(Boss t) {
        bossTarget = t;
    }

    public void setBossTarget(Boss t) {
        bossTarget = t;
    }

    public Player getOwner() {
        return playerOwner;
    }

    public Player getTarget() {
        return playerTarget;
    }

    public Boss getBossOwner() {
        return bossOwner;
    }

    public Boss getBossTarget() {
        return bossTarget;
    }

    @Override
    public void update() {
        duration -= Globals.nsToMs(Globals.LOGIC_UPDATE);
    }

    public long getDuration() {
        return duration;
    }

    public boolean isExpired() {
        return duration <= 0;
    }

    public boolean isDebuff() {
        return isDebuff;
    }

    public void setDebuff(boolean set) {
        isDebuff = set;
    }

    public Byte getParticleID() {
        return particleID;
    }
}
