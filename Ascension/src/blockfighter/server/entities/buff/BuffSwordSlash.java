package blockfighter.server.entities.buff;

import blockfighter.server.LogicModule;
import blockfighter.server.entities.player.Player;

public class BuffSwordSlash extends Buff implements BuffDmgReduct {

    private final double dmgReduct, dmgTakenMult;

    public BuffSwordSlash(final LogicModule l, final int d, final double reduct, final Player o) {
        super(l, d, o);
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
