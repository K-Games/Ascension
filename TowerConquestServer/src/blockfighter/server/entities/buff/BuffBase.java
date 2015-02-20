package blockfighter.server.entities.buff;

import blockfighter.server.Globals;
import blockfighter.server.entities.boss.Boss;
import blockfighter.server.entities.player.Player;

/**
 * Abstract class for all buffs/debuffs
 *
 * @author Ken Kwan
 */
public abstract class BuffBase implements Buff {

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
    public BuffBase(long d) {
        duration = d;
    }

    @Override
    public void setOwner(Player owner) {
        playerOwner = owner;
    }

    @Override
    public void setOwner(Boss owner) {
        bossOwner = owner;
    }

    @Override
    public void setTarget(Player t) {
        playerTarget = t;
    }

    @Override
    public void setTarget(Boss t) {
        bossTarget = t;
    }

    @Override
    public Player getOwner() {
        return playerOwner;
    }

    @Override
    public Player getTarget() {
        return playerTarget;
    }

    @Override
    public Boss getBossOwner() {
        return bossOwner;
    }

    @Override
    public Boss getBossTarget() {
        return bossTarget;
    }

    @Override
    public void update() {
        duration -= Globals.nsToMs(Globals.LOGIC_UPDATE);
    }

    @Override
    public long getDuration() {
        return duration;
    }

    @Override
    public boolean isExpired() {
        return duration <= 0;
    }

    @Override
    public boolean isDebuff() {
        return isDebuff;
    }

    @Override
    public void setDebuff(boolean set) {
        isDebuff = set;
    }

    @Override
    public Byte getParticleID() {
        return particleID;
    }
}
