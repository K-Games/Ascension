package blockfighter.server.entities.buff;

import blockfighter.server.LogicModule;
import blockfighter.server.entities.mob.Mob;
import blockfighter.server.entities.player.Player;

public class BuffKnockback extends Buff {

    private final double xSpeed, ySpeed;
    private boolean applied = false;

    public BuffKnockback(final LogicModule l, final int d, final double x, final double y, final Player o, final Player t) {
        super(l, d, o, t);
        this.xSpeed = x;
        this.ySpeed = y;
        super.setDebuff(true);
    }

    public BuffKnockback(final LogicModule l, final int d, final double x, final double y, final Mob o, final Player t) {
        super(l, d, o, t);
        this.xSpeed = x;
        this.ySpeed = y;
        super.setDebuff(true);
    }

    @Override
    public void update() {
        super.update();
        if (!this.applied) {
            if (this.xSpeed != 0) {
                getTarget().setXSpeed(this.xSpeed);
            }
            if (this.ySpeed != 0) {
                getTarget().setYSpeed(this.ySpeed);
            }
            this.applied = true;
        }
    }
}
