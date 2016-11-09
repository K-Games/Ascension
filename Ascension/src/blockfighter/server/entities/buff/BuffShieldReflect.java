package blockfighter.server.entities.buff;

import blockfighter.server.LogicModule;
import blockfighter.server.entities.player.Player;

public class BuffShieldReflect extends Buff {

    private double multiplier = 1;

    public BuffShieldReflect(final LogicModule l, final int d, final double m, final Player o, final Player t) {
        super(l, d, o, t);
        this.multiplier = m;
    }

    public double getMultiplier() {
        return this.multiplier;
    }
}
