package blockfighter.server.entities.proj;

import blockfighter.server.LogicModule;
import blockfighter.server.entities.boss.Boss;
import blockfighter.server.entities.damage.Damage;
import blockfighter.server.entities.player.Player;
import java.awt.geom.Rectangle2D;

/**
 * This is the base projectile class. Create projectile classes off this.
 *
 * @author Ken Kwan
 */
public class ProjShieldReflect extends Projectile {

    private double dmg;

    public ProjShieldReflect(LogicModule l, int k, Player o, double x, double y, double damage) {
        super(l, k);
        setOwner(o);
        this.x = x;
        this.y = y;
        hitbox = new Rectangle2D.Double[1];
        hitbox[0] = new Rectangle2D.Double(x - 325, y - 450, 650, 650);
        duration = 400;
        dmg = damage;
    }

    @Override
    public void processQueue() {
        while (!playerQueue.isEmpty()) {
            Player p = playerQueue.poll(), owner = getOwner();
            if (p != null && !p.isDead()) {
                Damage dmgEntity = new Damage((int) dmg, true, owner, p, false, hitbox[0], p.getHitbox());
                dmgEntity.setCanReflect(false);
                p.queueDamage(dmgEntity);
            }
        }
        while (!bossQueue.isEmpty()) {
            Boss b = bossQueue.poll();
            Player owner = getOwner();
            if (b != null && !b.isDead()) {
                Damage dmgEntity = new Damage((int) dmg, true, owner, b, false, hitbox[0], b.getHitbox());
                dmgEntity.setCanReflect(false);
                b.queueDamage(dmgEntity);
            }
        }
        queuedEffect = false;
    }

}
