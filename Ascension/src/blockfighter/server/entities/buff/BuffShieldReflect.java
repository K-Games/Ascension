package blockfighter.server.entities.buff;

import blockfighter.server.LogicModule;
import blockfighter.server.entities.player.Player;

public class BuffShieldReflect extends Buff implements BuffDmgReduct {

    private double multiplier = 1;

    private final double dmgReduct, dmgTakenMult;

    public BuffShieldReflect(final LogicModule l, final int d, final double m, final Player o, final Player t, final double reduct) {
        super(l, d, o, t);
        this.multiplier = m;
        this.dmgReduct = reduct;
        this.dmgTakenMult = 1D - this.dmgReduct;
    }

    public double getMultiplier() {
        return this.multiplier;
    }

    @Override
    public double getDmgReduction() {
        return this.dmgReduct;
    }

    @Override
    public double getDmgTakenMult() {
        return this.dmgTakenMult;
    }
}
