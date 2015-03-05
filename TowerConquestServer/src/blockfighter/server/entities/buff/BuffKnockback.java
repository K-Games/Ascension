package blockfighter.server.entities.buff;

import blockfighter.server.entities.boss.Boss;
import blockfighter.server.entities.player.Player;

/**
 *
 * @author Ken Kwan
 */
public class BuffKnockback extends Buff {

    private double xSpeed, ySpeed;
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
    public BuffKnockback(long d, double x, double y, Player o, Player t) {
        super(d);
        xSpeed = x;
        ySpeed = y;
        setOwner(o);
        setTarget(t);
        setDebuff(true);
    }

    public BuffKnockback(long d, double x, double y, Boss o, Player t) {
        super(d);
        xSpeed = x;
        ySpeed = y;
        setOwner(o);
        setTarget(t);
        setDebuff(true);
    }

    @Override
    public void update() {
        super.update();
        if (!applied) {
            if (xSpeed != 0) {
                getTarget().setXSpeed(xSpeed);
            }
            if (ySpeed != 0) {
                getTarget().setYSpeed(ySpeed);
            }
            applied = true;
        }
    }
}
