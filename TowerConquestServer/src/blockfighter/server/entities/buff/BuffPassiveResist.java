package blockfighter.server.entities.buff;

import blockfighter.server.LogicModule;

public class BuffPassiveResist extends Buff implements BuffDmgReduct {

    private final double dmgReduct, dmgTakenMult;

    public BuffPassiveResist(final LogicModule l, final int d, final double reduct) {
        super(l, d);
        this.dmgReduct = reduct;
        this.dmgTakenMult = 1D - this.dmgReduct;
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
