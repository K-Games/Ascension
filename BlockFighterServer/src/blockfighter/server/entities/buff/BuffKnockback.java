package blockfighter.server.entities.buff;

import blockfighter.server.entities.player.Player;

/**
 *
 * @author Ken
 */
public class BuffKnockback extends BuffBase {

    private double xSpeed, ySpeed;
    private boolean applied = false;

    /**
     * Constructor for knockback debuff
     *
     * @param d duration in ms
     * @param x x speed
     * @param y y speed
     * @param o owning player(knocked player)
     */
    public BuffKnockback(long d, double x, double y, Player o) {
        super(d);
        xSpeed = x;
        ySpeed = y;
        owner = o;
    }

    @Override
    public void update() {
        super.update();
        if (!applied) {
            owner.setXSpeed(xSpeed);
            owner.setYSpeed(ySpeed);
            applied = true;
        }
    }
}
