package blockfighter.server.entities.proj;

import blockfighter.server.Globals;
import blockfighter.server.LogicModule;
import blockfighter.server.entities.buff.BuffKnockback;
import blockfighter.server.entities.damage.Damage;
import blockfighter.server.entities.player.Player;
import java.awt.geom.Rectangle2D;
import java.util.LinkedList;
import java.util.Map;

/**
 * This is the base projectile class. Create projectile classes off this.
 *
 * @author Ken Kwan
 */
public class ProjSwordMulti extends ProjBase {

    private final LinkedList<Player> queue = new LinkedList<>();

    /**
     * Projectile of Sword Skill Whirlwind.
     *
     * @param l Room/Logic Module
     * @param k Projectile Key
     * @param o Owning player
     * @param x Spawn x-coordinate
     * @param y Spawn y-coordinate
     */
    public ProjSwordMulti(LogicModule l, int k, Player o, double x, double y) {
        super(l, k);
        setOwner(o);
        this.x = x;
        this.y = y;
        hitbox = new Rectangle2D.Double[1];
        if (getOwner().getFacing() == Globals.RIGHT) {
            hitbox[0] = new Rectangle2D.Double(x - 60, y - 220, 240, 240);
        } else {
            hitbox[0] = new Rectangle2D.Double(x - 240 + 60, y - 220, 240, 240);

        }
        duration = 600;
    }

    @Override
    public void update() {
        duration -= Globals.nsToMs(Globals.LOGIC_UPDATE);
        for (Map.Entry<Byte, Player> pEntry : logic.getPlayers().entrySet()) {
            Player p = pEntry.getValue();
            if (p != getOwner() && !pHit.contains(p) && p.intersectHitbox(hitbox[0])) {
                queue.add(p);
                pHit.add(p);
                queueEffect(this);
            }
        }
    }

    @Override
    public void processQueue() {
        while (!queue.isEmpty()) {
            Player p = queue.poll();
            if (p != null && !p.isDead()) {
                int damage = (int) (getOwner().rollDamage());
                boolean crit = getOwner().rollCrit();
                if (crit) {
                    damage = (int) getOwner().criticalDamage(damage);
                }
                p.queueDamage(new Damage(damage, true, getOwner(), p, crit, hitbox[0], p.getHitbox()));
                p.queueBuff(new BuffKnockback(60, (getOwner().getFacing() == Globals.RIGHT) ? 1 : -1, -1, getOwner(), p));
            }
        }
        queuedEffect = false;
    }

}
