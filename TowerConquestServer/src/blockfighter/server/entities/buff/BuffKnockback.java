package blockfighter.server.entities.buff;

import blockfighter.server.entities.boss.Boss;
import blockfighter.server.entities.player.Player;

/**
 *
 * @author Ken Kwan
 */
public class BuffKnockback extends Buff {

    private final double xSpeed, ySpeed;
    private boolean applied = false;

    /**
     * Constructor for knockback debuff
     *
     * @param d duration in milliseconds
     * @param x x speed
     * @param y y speed
     * @param o owning player
     * @param t player being knocked
     */
    public BuffKnockback(final long d, final double x, final double y, final Player o, final Player t) {
        super(d);
        this.xSpeed = x;
        this.ySpeed = y;
        setOwner(o);
        setTarget(t);
        setDebuff(true);
    }

    public BuffKnockback(final long d, final double x, final double y, final Boss o, final Player t) {
        super(d);
        this.xSpeed = x;
        this.ySpeed = y;
        setOwner(o);
        setTarget(t);
        setDebuff(true);
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
