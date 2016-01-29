package blockfighter.server.entities.buff;

import blockfighter.server.entities.player.Player;

public class BuffSwordTaunt extends Buff implements BuffDmgReduct, BuffDmgIncrease {

    private final double dmgReduct, dmgTakenMult, dmgIncrease;

    public BuffSwordTaunt(final long d, final double reduct, final double increase, final Player o) {
        super(d, o);
        this.dmgReduct = reduct;
        this.dmgIncrease = increase;
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

    @Override
    public double getDmgIncrease() {
        return this.dmgIncrease;
    }

}
