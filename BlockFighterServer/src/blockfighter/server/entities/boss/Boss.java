/*
 * Interface for Bosses
 * Abstract class will implement base methods.
 */
package blockfighter.server.entities.boss;

import blockfighter.server.Globals;
import java.awt.geom.Rectangle2D;

/**
 *
 * @author Ken
 */
public interface Boss {

    /**
     * Return boss's current X position.
     *
     * @return X in double
     */
    public double getX();

    /**
     * Return this boss's current Y position.
     *
     * @return Y in double
     */
    public abstract double getY();

    /**
     * Return this boss's current state.
     * <p>
     * Used for updating animation state and player interactions. States are listed in Globals.
     * </p>
     *
     * @return The boss's state in byte
     */
    public abstract byte getBossState();

    /**
     * Return this boss's facing direction.
     * <p>
     * Direction value is found in Globals.
     * </p>
     *
     * @return The boss's facing direction in byte
     */
    public abstract byte getFacing();

    /**
     * Return this boss's current animation frame.
     *
     * @return The boss's current animation frame
     */
    public abstract byte getFrame();

    /**
     * Set the boss's x and y position.
     * <p>
     * This does not interpolate. The player is instantly moved to this location.
     * </p>
     *
     * @param x New x location in double
     * @param y New y location in double
     */
    public void setPos(double x, double y);

    /**
     * Set change in Y on the next tick.
     *
     * @param speed Distance in double
     */
    public void setYSpeed(double speed);

    /**
     * Set change in X on the next tick.
     *
     * @param speed Distance in double
     */
    public void setXSpeed(double speed);

    /**
     * Updates all logic of this boss.
     * <p>
     * Must be called every tick. Specific logic updates are separated into other methods. Specific logic updates must be private.
     * </p>
     */
    public void update();

    /**
     * Check if a rectangle intersects with this boss's hitbox
     *
     * @param box Box to be checked
     * @return True of the boxes intersect
     */
    public abstract boolean intersectHitbox(Rectangle2D.Double box);

    /**
     * Return if player is stunned
     *
     * @return True if stun duration is > 0
     */
    public abstract boolean isStunned();

    /**
     * Return if player is being knocked back.
     *
     * @return true if knockback duration is > 0
     */
    public abstract boolean isKnockback();

    /**
     * Set player facing direction.
     * <p>
     * Direction constants in Globals
     * </p>
     *
     * @param f Direction in byte
     */
    public abstract void setFacing(byte f);

    public abstract void setBossState(byte newState);

    /**
     * Send the player's current position to every connected player
     * <p>
     * X and y are casted and sent as int.
     * <br/>
     * Uses Server Broadcaster to send to all<br/>
     * Byte sent: 0 - Data type 1 - Index 2,3,4,5 - x 6,7,8,9 - y
     * </p>
     */
    public abstract void sendPos();

    /**
     * Send the player's current facing direction to every connected player
     * <p>
     * Facing uses direction constants in Globals.<br/>
     * Uses Server Broadcaster to send to all
     * <br/>Byte sent: 0 - Data type 1 - Index 2 - Facing direction
     * </p>
     */
    public void sendFacing();

    /**
     * Send the player's current state(for animation) and current frame of animation to every connected player
     * <p>
     * State constants are in Globals.<br/>
     * Uses Server Broadcaster to send to all<br/>
     * Byte sent: 0 - Data type 1 - Index 2 - Player state 3 - Current frame
     * </p>
     */
    public void sendState();

}
