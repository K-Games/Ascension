package blockfighter.server.entities.mob.boss.ShadowFiend;

import blockfighter.server.Globals;
import blockfighter.server.LogicModule;
import blockfighter.server.entities.buff.BuffKnockback;
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
public class ProjRaze extends MobProjectile {

    private final double speedX;

    /**
     * Projectile of Sword Skill Defensive Impact.
     *
     * @param l Room/Logic Module
     * @param o Owning player
     * @param x Spawn x-coordinate
     * @param y Spawn y-coordinate
     * @param right
     */
    public ProjRaze(final LogicModule l, final Mob o, final double x, final double y, final boolean right) {
        super(l, o, x, y, 1000);
        this.hitbox = new Rectangle2D.Double[1];
        this.hitbox[0] = new Rectangle2D.Double(x - 10, y - 250, 20, 250);
        if (right) {
            speedX = 10;
        } else {
            speedX = -10;
        }
    }

    @Override
    public void update() {
        this.x += this.speedX;
        this.hitbox[0].x += this.speedX;
        this.pHit.clear();
        super.update();
    }

    @Override
    public void processQueue() {
        while (!this.playerQueue.isEmpty()) {
            final Player p = this.playerQueue.poll();
            if (p != null && !p.isDead()) {
                final int damage = (int) (70 * Math.pow(getMobOwner().getStats()[Mob.STAT_LEVEL], 1.7));
                p.queueDamage(new Damage(damage, false, getMobOwner(), p, this.hitbox[0], p.getHitbox()));
                p.queueBuff(new BuffKnockback(this.logic, 100, (p.getFacing() == Globals.RIGHT) ? -10 : 10, 0, getMobOwner(), p));
            }
        }
        this.queuedEffect = false;
    }

}
