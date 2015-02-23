package blockfighter.server.entities.buff;

import blockfighter.server.entities.player.Player;

public class BuffPassiveBarrier extends BuffBase {

    private double barrierAmount;

    public BuffPassiveBarrier(double amt, Player o) {
        super(0);
        barrierAmount = amt;
        setOwner(o);
    }

    @Override
    public void update() {
    }

    public double reduceDmg(double dmg) {
        double finalDmg;
        if (barrierAmount >= dmg) {
            barrierAmount -= dmg;
            finalDmg = 0;
        } else {
            finalDmg = dmg - barrierAmount;
            barrierAmount = 0;
        }
        return finalDmg;
    }

    @Override
    public boolean isExpired() {
        return barrierAmount <= 0;
    }
}
