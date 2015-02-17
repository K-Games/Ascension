package blockfighter.server.entities.buff;

public class BuffShieldIron extends BuffBase implements BuffDmgReduct {

    private final double dmgReduct, dmgTakenMult;

    public BuffShieldIron(long d, double reduct) {
        super(d);
        dmgReduct = reduct;
        dmgTakenMult = 1D - dmgReduct;
    }

    @Override
    public double getDmgReduction() {
        return dmgReduct;
    }

    @Override
    public double getDmgTakenMult() {
        return dmgTakenMult;
    }

}
