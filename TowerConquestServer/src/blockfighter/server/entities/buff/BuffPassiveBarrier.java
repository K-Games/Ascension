package blockfighter.server.entities.buff;

import blockfighter.server.LogicModule;
import blockfighter.server.entities.player.Player;

public class BuffPassiveBarrier extends Buff {

    private double barrierAmount;

    public BuffPassiveBarrier(final LogicModule l, final double amt, final Player o) {
        super(l, 0, o);
        this.barrierAmount = amt;
    }

    @Override
    public void update() {
    }

    public double reduceDmg(final double dmg) {
        double finalDmg;
        if (this.barrierAmount >= dmg) {
            this.barrierAmount -= dmg;
            finalDmg = 0;
        } else {
            finalDmg = dmg - this.barrierAmount;
            this.barrierAmount = 0;
        }
        return finalDmg;
    }

    @Override
    public boolean isExpired() {
        return this.barrierAmount <= 0;
    }
}
