package blockfighter.server.entities.Projectiles;

import blockfighter.server.Globals;
import blockfighter.server.entities.Player;
import blockfighter.server.entities.projectiles.ProjBase;
import blockfighter.server.LogicModule;
import blockfighter.server.net.Broadcaster;
import java.awt.geom.Rectangle2D;
import java.util.LinkedList;

/**
 * This is the base projectile class. Create projectile classes off this.
 *
 * @author Ken
 */
public class ProjTest extends ProjBase {

    private final LinkedList<Player> queue = new LinkedList<>();

    /**
     * Create a basic projectile.
     * <p>
     * Knock back any players hit
     * </p>
     * @param b Reference to server broadcaster
     * @param l Reference to Logic module
     * @param o Owning player
     * @param x Spawning x
     * @param y Spawning y
     * @param duration
     */
    public ProjTest(Broadcaster b, LogicModule l, int k, Player o, double x, double y, double duration) {
        super(b, l, k);
        owner = o;
        if (owner.getFacing() == Globals.LEFT) {
            xSpeed = -6;
        } else {
            xSpeed = 6;
        }
        ySpeed = -8;
        this.x = x;
        this.y = y;
        hitbox = new Rectangle2D.Double[1];
        if (owner.getFacing() == Globals.LEFT) {
            hitbox[0] = new Rectangle2D.Double(x - 35, y - 96, 35, 96);
        } else {
            hitbox[0] = new Rectangle2D.Double(x, y - 96, 35, 96);
        }
        this.duration = duration;
    }

    @Override
    public void update() {
        duration -= Globals.LOGIC_UPDATE;
        for (Player p : logic.getPlayers()) {
            if (p != owner && p != null && !pHit.contains(p) && p.intersectHitbox(hitbox[0])) {
                queue.add(p);
                pHit.add(p);
                logic.queueKnockPlayer(this);
            }
        }
    }

    /**
     * Process any knockbacks to be applied to players hit by this projectile.
     */
    @Override
    public void processQueue() {
        while (!queue.isEmpty()) {
            Player p = queue.pop();
            p.setKnockback(500000000, xSpeed, ySpeed);
        }
    }

    @Override
    public int getKey() {
        return key;
    }
}
