package blockfighter.server.entities.mob.boss.Lightning;

import blockfighter.server.Globals;
import blockfighter.server.LogicModule;
import blockfighter.server.entities.damage.Damage;
import blockfighter.server.entities.mob.Mob;
import blockfighter.server.entities.mob.MobProjectile;
import blockfighter.server.entities.player.Player;
import java.awt.geom.Rectangle2D;

/**
 * This is the base projectile class. Create projectile classes off this.
 *
 * @author Ken Kwan
 */
public class ProjAttack extends MobProjectile {

    /**
     * Projectile of Sword Skill Defensive Impact.
     *
     * @param l Room/Logic Module
     * @param o Owning player
     * @param x Spawn x-coordinate
     * @param y Spawn y-coordinate
     */
    public ProjAttack(final LogicModule l, final Mob o, final double x, final double y) {
        super(l, o, x, y, 200);
        this.hitbox = new Rectangle2D.Double[1];
        if (o.getFacing() == Globals.RIGHT) {
            this.hitbox[0] = new Rectangle2D.Double(x + 200, y - 450, 250, 450);
        } else {
            this.hitbox[0] = new Rectangle2D.Double(x - 250 - 200, y - 450, 250, 450);
        }
    }

    @Override
    public void processQueue() {
        while (!this.playerQueue.isEmpty()) {
            final Player p = this.playerQueue.poll();
            if (p != null && !p.isDead()) {
                final int damage = (int) (70 * Math.pow(getMobOwner().getStats()[Mob.STAT_LEVEL], 1.7));
                p.queueDamage(new Damage(damage, false, getMobOwner(), p, this.hitbox[0], p.getHitbox()));
            }
        }
        this.queuedEffect = false;
    }

}
