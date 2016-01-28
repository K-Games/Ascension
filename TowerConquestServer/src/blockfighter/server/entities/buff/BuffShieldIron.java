package blockfighter.server.entities.buff;

public class BuffShieldIron extends Buff implements BuffDmgReduct {

    private final double dmgReduct, dmgTakenMult;

    public BuffShieldIron(final long d, final double reduct) {
        super(d);
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
