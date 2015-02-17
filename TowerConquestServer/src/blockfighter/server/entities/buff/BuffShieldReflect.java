package blockfighter.server.entities.buff;

import blockfighter.server.entities.player.Player;

/**
 *
 * @author Ken Kwan
 */
public class BuffShieldReflect extends BuffBase {

    private double multiplier = 1;

    /**
     * Construct reflect damage buff
     *
     * @param d duration in ms
     * @param m
     * @param o
     * @param t
     */
    public BuffShieldReflect(long d, double m, Player o, Player t) {
        super(d);
        setOwner(o);
        setTarget(t);
        multiplier = m;
    }

    public double getMultiplier() {
        return multiplier;
    }
}
