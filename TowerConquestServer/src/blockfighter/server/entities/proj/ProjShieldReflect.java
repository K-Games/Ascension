package blockfighter.server.entities.proj;

import blockfighter.server.LogicModule;
import blockfighter.server.entities.mob.Mob;
import blockfighter.server.entities.damage.Damage;
import blockfighter.server.entities.player.Player;
import java.awt.geom.Rectangle2D;

/**
 * This is the base projectile class. Create projectile classes off this.
 *
 * @author Ken Kwan
 */
public class ProjShieldReflect extends Projectile {

    private final double dmg;

    public ProjShieldReflect(final LogicModule l, final int k, final Player o, final double x, final double y, final double damage) {
        super(l, k, o, x, y, 400);
        this.hitbox = new Rectangle2D.Double[1];
        this.hitbox[0] = new Rectangle2D.Double(this.x - 325, this.y - 450, 650, 650);
        this.dmg = damage;
    }

    @Override
    public void processQueue() {
        while (!this.playerQueue.isEmpty()) {
            final Player p = this.playerQueue.poll(), owner = getOwner();
            if (p != null && !p.isDead()) {
                final Damage dmgEntity = new Damage((int) this.dmg, true, owner, p, false, this.hitbox[0], p.getHitbox());
                dmgEntity.setCanReflect(false);
                p.queueDamage(dmgEntity);
            }
        }
        while (!this.mobQueue.isEmpty()) {
            final Mob b = this.mobQueue.poll();
            final Player owner = getOwner();
            if (b != null && !b.isDead()) {
                final Damage dmgEntity = new Damage((int) this.dmg, true, owner, b, false, this.hitbox[0], b.getHitbox());
                dmgEntity.setCanReflect(false);
                b.queueDamage(dmgEntity);
            }
        }
        this.queuedEffect = false;
    }

}
