package blockfighter.server.entities.buff;

import blockfighter.server.LogicModule;

public class BuffChargeCripple extends Buff implements BuffSpeedDecrease {

    private static final double speedDecrease = 0.7;

    public BuffChargeCripple(final LogicModule l, final int d) {
        super(l, d);
        super.setDebuff(true);
    }

    @Override
    public double getXSpeedDecrease() {
        return this.speedDecrease;
    }

    @Override
    public double getYSpeedDecrease() {
        return this.speedDecrease;
    }

}
