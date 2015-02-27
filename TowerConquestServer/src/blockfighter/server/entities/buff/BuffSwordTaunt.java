package blockfighter.server.entities.buff;

import blockfighter.server.entities.player.Player;

public class BuffSwordTaunt extends Buff implements BuffDmgReduct, BuffDmgIncrease {

    private final double dmgReduct, dmgTakenMult, dmgIncrease;

    public BuffSwordTaunt(long d, double reduct, double increase, Player o) {
        super(d);
        setOwner(o);
        dmgReduct = reduct;
        dmgIncrease = increase;
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

    @Override
    public double getDmgIncrease() {
        return dmgIncrease;
    }

}
