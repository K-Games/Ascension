package blockfighter.server.entities.buff;

import blockfighter.server.entities.player.Player;

/**
 *
 * @author Ken Kwan
 */
public class BuffShieldReflect extends Buff {

    private double multiplier = 1;

    /**
     * Construct reflect damage buff
     *
     * @param d duration in ms
     * @param m
     * @param o
     * @param t
     */
    public BuffShieldReflect(final long d, final double m, final Player o, final Player t) {
        super(d);
        setOwner(o);
        setTarget(t);
        this.multiplier = m;
    }

    public double getMultiplier() {
        return this.multiplier;
    }
}
