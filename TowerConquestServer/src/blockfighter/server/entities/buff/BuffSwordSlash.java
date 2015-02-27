package blockfighter.server.entities.buff;

import blockfighter.server.entities.player.Player;

public class BuffSwordSlash extends Buff implements BuffDmgReduct {

    private final double dmgReduct, dmgTakenMult;

    public BuffSwordSlash(long d, double reduct, Player o) {
        super(d);
        dmgReduct = reduct;
        dmgTakenMult = 1D - dmgReduct;
        setOwner(o);
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
