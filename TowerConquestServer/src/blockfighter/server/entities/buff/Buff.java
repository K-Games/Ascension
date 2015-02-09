/*
 * Interface of player buffs/debuffs
 * An abstract class will implement this interface for logic updates
 */
package blockfighter.server.entities.buff;

import blockfighter.server.entities.boss.Boss;
import blockfighter.server.entities.player.Player;

/**
 *
 * @author Ken Kwan
 */
public interface Buff {

    /**
     * Set the player owner of this buff/debuff.
     *
     * @param owner The owning Player entity
     */
    public abstract void setOwner(Player owner);

    /**
     * Set the boss owner of this buff/debuff.
     *
     * @param owner The owning Player entity
     */
    public abstract void setOwner(Boss owner);

    /**
     * Set the target or affected player of this buff
     *
     * @param target
     */
    public abstract void setTarget(Player target);

    /**
     * Set the target or affected boss of this buff
     *
     * @param target
     */
    public abstract void setTarget(Boss target);

    /**
     * Return the owner of this buff/debuff.
     *
     * @return Owning Player entity
     */
    public abstract Player getOwner();

    /**
     * Get the targeted or affected player of this buff.
     *
     * @return Player target/affected
     */
    public abstract Player getTarget();

    /**
     * Get the owning Boss of this buff.
     *
     * @return Owning Boss entity
     */
    public abstract Boss getBossOwner();

    /**
     * Get the targeted or affected boss of this buff.
     *
     * @return Boss target/affected
     */
    public abstract Boss getBossTarget();

    /**
     * Update logic of this buff/debuff. Update duration
     */
    public abstract void update();

    /**
     * Return the duration left on this buff/debuff
     *
     * @return Duration left in milliseconds.
     */
    public abstract long getDuration();

    /**
     * Check if buff/debuff expired(duration=0).
     *
     * @return True if duration <= 0
     */
    public abstract boolean isExpired();

}
