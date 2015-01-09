/*
 * Interface of player buffs/debuffs
 * An abstract class will implement this interface for logic updates
 */
package blockfighter.server.entities.buff;

import blockfighter.server.entities.Player;

/**
 *
 * @author Ken
 */
public interface Buff {

    /**
     * Set the owner of this buff/debuff.
     *
     * @param owner The owning Player entity
     */
    public abstract void setOwner(Player owner);

    /**
     * Return the owner of this buff/debuff.
     *
     * @return Owning Player entity
     */
    public abstract Player getOwner();

    /**
     * Update logic of this buff/debuff. Update duration
     */
    public abstract void update();

    /**
     * Return the duration left on this buff/debuff
     *
     * @return Duration left in ms.
     */
    public abstract long getDuration();

    /**
     * Check if buff/debuff expired(duration=0).
     *
     * @return True if duration <= 0
     */
    public abstract boolean isExpired();


}
