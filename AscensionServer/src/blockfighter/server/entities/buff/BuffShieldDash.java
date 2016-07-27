package blockfighter.server.entities.buff;

import blockfighter.server.LogicModule;
import blockfighter.server.entities.player.Player;

public class BuffShieldDash extends Buff implements BuffDmgIncrease {

    private final double dmgIncrease;

    public BuffShieldDash(final LogicModule l, final int d, final double inc, final Player o) {
        super(l, d, o);
        this.dmgIncrease = inc;
    }

    @Override
    public double getDmgIncrease() {
        return this.dmgIncrease;
    }

}
